package semantic.visitor;

import flask.ast.nodes.ASTNode;
import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.expressions.access.FunctionCallNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.ListNode;
import flask.ast.nodes.expressions.atoms.TupleNode;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.ForStatementNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.WhileStatementNode;
import flask.ast.nodes.statements.imports.FromImportNode;
import flask.ast.nodes.statements.imports.ImportNode;
import flask.ast.nodes.statements.simple.AssignmentNode;
import flask.ast.nodes.statements.simple.BreakNode;
import flask.ast.nodes.statements.simple.ContinueNode;
import flask.ast.nodes.statements.simple.ReturnNode;
import flask.ast.visitor.ASTBaseVisitor;
import semantic.error.SemanticError;
import semantic.error.SemanticErrorType;
import semantic.scope.Scope;
import semantic.scope.ScopeType;
import semantic.scope.SymbolTable;
import semantic.symbol.FunctionSignature;
import semantic.symbol.Symbol;
import semantic.symbol.SymbolType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Walks the Python/Flask AST ({@code ProgramNode} and everything beneath
 * it), building up scopes and symbols as declarations are encountered and
 * reporting semantic errors as violations are found.
 *
 * <p>This class extends the existing {@link ASTBaseVisitor}, the same base
 * every other Flask-side visitor in this project builds on, rather than
 * implementing {@link flask.ast.visitor.ASTVisitor} directly — every node
 * type this visitor does not override (literals, binary/unary operations,
 * class bodies, ...) is therefore still fully traversed through the
 * inherited default pass-through, so "visit every node" holds for the
 * whole hierarchy without this class needing to enumerate every node type
 * itself.
 *
 * <p>Scope-shaping overrides (unchanged in intent from the previous phase):
 * <ul>
 *   <li>{@link #visitProgram} — pushes the single {@link ScopeType#GLOBAL} scope.</li>
 *   <li>{@link #visitFunctionDef} — defines the function's own name, then
 *       pushes a {@link ScopeType#FUNCTION} scope and defines its parameters.</li>
 *   <li>{@link #visitForStatement} / {@link #visitWhileStatement} — push a
 *       {@link ScopeType#LOOP} scope around the loop body; {@code for} also
 *       defines its target(s) as loop variables.</li>
 *   <li>{@link #visitAssignment} — defines its target(s) as variables in
 *       whatever scope is currently active.</li>
 * </ul>
 *
 * <p>Checking overrides added this phase, one per Python semantic error
 * required by the current instructions:
 * <ul>
 *   <li>{@link #visitIdentifier} — #1 use of undefined variable.</li>
 *   <li>{@link #defineWithDuplicateCheck} (used by every binding site
 *       below) — #2 duplicate variable declaration, #6 duplicate function
 *       definition, #7 duplicate parameter names — all the same
 *       "already defined directly in this scope" check, applied to
 *       whichever kind of name is being bound.</li>
 *   <li>{@link #visitFunctionCall} / {@link #checkArgumentCount} — #3
 *       function call with an incorrect number of arguments.</li>
 *   <li>{@link #visitReturn} — #4 return statement outside a function.</li>
 *   <li>{@link #visitBreak} / {@link #visitContinue} — #5 break/continue
 *       outside loops.</li>
 * </ul>
 *
 * <p>Every check collects into the shared error list via {@link
 * #reportError} and keeps walking — nothing here stops at the first error
 * (see {@code SemanticAnalyzer#getErrors()} for the full collected list).
 *
 * <p>{@link #visitImport} / {@link #visitFromImport} are new this phase
 * too: they are not one of the seven required checks, but defining
 * imported names (e.g. {@code Flask}, {@code render_template}) is what
 * keeps check #1 from flagging every single import as undefined — without
 * it, "use of undefined variable" would be useless on any real Flask
 * source. {@link #visitFunctionCall} also watches specifically for
 * {@code render_template(...)} calls, collecting their keyword-argument
 * names (see {@link #getRenderContextVariableNames()}) so the template
 * side can recognize context variables that never appear anywhere in the
 * Jinja2 AST itself.
 */
public class FlaskSemanticVisitor extends ASTBaseVisitor<Void> {

    /**
     * Names always considered defined, regardless of scope: Python's
     * built-in functions/types/constants and implicit module-level names,
     * none of which any source file ever declares itself. Deliberately a
     * plain, hand-picked set scoped to what a Flask-style script
     * realistically uses — not an attempt at a complete CPython builtins
     * list — so that "use of undefined variable" does not drown in false
     * positives on ordinary, valid code.
     */
    private static final Set<String> PYTHON_BUILTINS = Set.of(
        "__name__", "__file__", "__doc__",
        "print", "len", "range", "str", "int", "float", "bool", "list", "dict", "set", "tuple",
        "True", "False", "None", "type", "isinstance", "issubclass", "super", "object",
        "enumerate", "zip", "map", "filter", "sorted", "reversed", "sum", "min", "max", "abs", "round",
        "open", "input", "iter", "next", "getattr", "setattr", "hasattr", "callable", "id", "repr",
        "Exception", "ValueError", "TypeError", "KeyError", "IndexError", "AttributeError",
        "StopIteration", "RuntimeError", "NotImplementedError"
    );

    private final SymbolTable symbolTable;
    private final List<SemanticError> errors;

    /**
     * Keyword-argument names passed to every {@code render_template(...)}
     * call discovered so far — the bridge to the template side. A Jinja2
     * template rendered from this script may legitimately reference any of
     * these names even though nothing in the template AST itself binds
     * them; see {@code TemplateSemanticVisitor#setExternalContextVariableNames}.
     */
    private final Set<String> renderContextVariableNames = new LinkedHashSet<>();

    public FlaskSemanticVisitor(SymbolTable symbolTable, List<SemanticError> errors) {
        this.symbolTable = symbolTable;
        this.errors = errors;
    }

    /** The union of keyword-argument names passed to every {@code render_template(...)} call seen so far. */
    public Set<String> getRenderContextVariableNames() {
        return renderContextVariableNames;
    }

    @Override
    public Void visitProgram(ProgramNode node) {
        symbolTable.enterScope(ScopeType.GLOBAL);
        try {
            super.visitProgram(node);
        } finally {
            symbolTable.exitScope();
        }
        return null;
    }

    @Override
    public Void visitFunctionDef(FunctionDefNode node) {
        // The function's own name belongs to the ENCLOSING scope (that is
        // what makes it callable from outside), so it is defined before a
        // new scope is pushed for its body. A name already used by
        // something else in this same scope is Python Semantic Error #6.
        defineWithDuplicateCheck(node.getName(), SymbolType.FUNCTION, node,
            "Duplicate function definition: '" + node.getName() + "' is already defined in this scope",
            buildFunctionSignature(node));

        symbolTable.enterScope(ScopeType.FUNCTION);
        try {
            for (Parameter parameter : node.getParameters()) {
                // Parameter (flask.ast.nodes.helpers.Parameter) carries no
                // source position of its own, so the enclosing FunctionDefNode's
                // position is used as the closest available location. Two
                // parameters sharing a name is Python Semantic Error #7.
                defineWithDuplicateCheck(parameter.getName(), SymbolType.PARAMETER, node,
                    "Duplicate parameter name: '" + parameter.getName()
                        + "' appears more than once in the parameter list of '" + node.getName() + "'",
                    null);
            }
            // Decorators, parameter defaults/type hints, the return-type
            // hint, and the body are all still visited, via the inherited
            // traversal — only the scope they are visited in changes here.
            super.visitFunctionDef(node);
        } finally {
            symbolTable.exitScope();
        }
        return null;
    }

    @Override
    public Void visitForStatement(ForStatementNode node) {
        // The iterable is evaluated in the ENCLOSING scope: the loop's own
        // scope (and its loop variable) do not exist until each iteration begins.
        node.getIterable().accept(this);

        symbolTable.enterScope(ScopeType.LOOP);
        try {
            defineTargetNames(node.getTarget(), SymbolType.LOOP_VARIABLE);
            for (Statement statement : node.getBody()) {
                statement.accept(this);
            }
        } finally {
            symbolTable.exitScope();
        }

        // A for-else clause runs after the loop completes normally, in the
        // ENCLOSING scope - it is not part of the loop's own scope.
        if (node.hasElse()) {
            for (Statement statement : node.getElseBody()) {
                statement.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatementNode node) {
        node.getCondition().accept(this);

        symbolTable.enterScope(ScopeType.LOOP);
        try {
            for (Statement statement : node.getBody()) {
                statement.accept(this);
            }
        } finally {
            symbolTable.exitScope();
        }

        if (node.hasElse()) {
            for (Statement statement : node.getElseBody()) {
                statement.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visitAssignment(AssignmentNode node) {
        // The value is a USE, checked (for undefined names) in whatever
        // scope is already active, and evaluated before the target below
        // is bound - so `x = x + 1` checks the right-hand `x` against
        // whatever `x` meant BEFORE this assignment takes effect.
        node.getValue().accept(this);
        defineTargetNames(node.getTarget(), SymbolType.VARIABLE);
        return null;
    }

    /** Python Semantic Error #1: use of undefined variable. */
    @Override
    public Void visitIdentifier(IdentifierNode node) {
        checkVariableIsDefined(node.getName(), node);
        return super.visitIdentifier(node);
    }

    /** Python Semantic Error #4: return statement outside a function. */
    @Override
    public Void visitReturn(ReturnNode node) {
        if (!isInsideFunction()) {
            reportError(SemanticErrorType.INVALID_RETURN,
                "Return statement outside a function",
                node.getLine(), node.getColumn(), node.getNodeName());
        }
        return super.visitReturn(node);
    }

    /** Python Semantic Error #5: break outside a loop. */
    @Override
    public Void visitBreak(BreakNode node) {
        if (!isInsideLoop()) {
            reportError(SemanticErrorType.INVALID_BREAK_OR_CONTINUE,
                "'break' outside a loop",
                node.getLine(), node.getColumn(), node.getNodeName());
        }
        return super.visitBreak(node);
    }

    /** Python Semantic Error #5: continue outside a loop. */
    @Override
    public Void visitContinue(ContinueNode node) {
        if (!isInsideLoop()) {
            reportError(SemanticErrorType.INVALID_BREAK_OR_CONTINUE,
                "'continue' outside a loop",
                node.getLine(), node.getColumn(), node.getNodeName());
        }
        return super.visitContinue(node);
    }

    /**
     * Not one of the seven required checks — see the class-level
     * documentation for why imported names are defined here.
     */
    @Override
    public Void visitImport(ImportNode node) {
        for (String importedName : node.getImports()) {
            defineWithDuplicateCheck(importedName, SymbolType.VARIABLE, node,
                "Duplicate variable declaration: '" + importedName + "' is already defined in this scope", null);
        }
        return super.visitImport(node);
    }

    /** @see #visitImport */
    @Override
    public Void visitFromImport(FromImportNode node) {
        for (String importedName : node.getImports()) {
            defineWithDuplicateCheck(importedName, SymbolType.VARIABLE, node,
                "Duplicate variable declaration: '" + importedName + "' is already defined in this scope", null);
        }
        return super.visitFromImport(node);
    }

    /**
     * Python Semantic Error #3: function call with an incorrect number of
     * arguments (via {@link #checkArgumentCount}). Also watches for
     * {@code render_template(...)} calls to collect context-variable names
     * for the template side — see the class-level documentation.
     */
    @Override
    public Void visitFunctionCall(FunctionCallNode node) {
        if (node.getCallee() instanceof IdentifierNode calleeIdentifier) {
            checkArgumentCount(calleeIdentifier, node);
            if ("render_template".equals(calleeIdentifier.getName())) {
                renderContextVariableNames.addAll(node.getKwargs().keySet());
            }
        }
        return super.visitFunctionCall(node);
    }

    // ------------------------------------------------------------------
    // Target binding (assignment / for-loop targets)
    // ------------------------------------------------------------------

    /**
     * Recursively defines every name bound by an assignment or loop
     * target: a plain identifier defines directly (checked for duplicate
     * declaration in this same scope — Python Semantic Error #2), while a
     * tuple/list target (Python destructuring, e.g. {@code a, b = 1, 2})
     * recurses into each element. Any other target shape (attribute access
     * like {@code obj.attr}, or subscript like {@code d["key"]}) mutates
     * an existing object rather than binding a new name, so it is instead
     * visited normally — checking, for instance, that {@code obj} itself
     * is a defined name — and no symbol is defined for it.
     */
    private void defineTargetNames(Expression target, SymbolType symbolType) {
        if (target instanceof IdentifierNode identifierNode) {
            defineWithDuplicateCheck(identifierNode.getName(), symbolType, identifierNode,
                "Duplicate variable declaration: '" + identifierNode.getName() + "' is already declared in this scope",
                null);
        } else if (target instanceof TupleNode tupleNode) {
            for (Expression element : tupleNode.getElements()) {
                defineTargetNames(element, symbolType);
            }
        } else if (target instanceof ListNode listNode) {
            for (Expression element : listNode.getElements()) {
                defineTargetNames(element, symbolType);
            }
        } else if (target != null) {
            target.accept(this);
        }
    }

    // ------------------------------------------------------------------
    // Checks
    // ------------------------------------------------------------------

    /** Python Semantic Error #1: use of undefined variable. */
    private void checkVariableIsDefined(String name, ASTNode referenceNode) {
        if (PYTHON_BUILTINS.contains(name) || symbolTable.resolve(name) != null) {
            return;
        }
        reportError(SemanticErrorType.UNDEFINED_VARIABLE,
            "Use of undefined variable '" + name + "'",
            referenceNode.getLine(), referenceNode.getColumn(), referenceNode.getNodeName());
    }

    /** Python Semantic Error #3: function call with an incorrect number of arguments. */
    private void checkArgumentCount(IdentifierNode calleeIdentifier, FunctionCallNode node) {
        if (!node.getStarArgs().isEmpty() || node.hasKwargsSpread()) {
            // *args/**kwargs expansion makes the real argument count unknowable statically.
            return;
        }
        Symbol calleeSymbol = symbolTable.resolve(calleeIdentifier.getName());
        if (calleeSymbol == null || calleeSymbol.getType() != SymbolType.FUNCTION) {
            // Unresolved (already reported by the identifier check above),
            // or resolves to something that is not a checkable function -
            // neither is this check's job.
            return;
        }
        FunctionSignature signature = calleeSymbol.getSignature();
        if (signature == null || signature.isVariadic()) {
            // No known signature (e.g. an imported name), or the function
            // itself accepts *args/**kwargs - no fixed count to check against.
            return;
        }
        int provided = node.getArgs().size() + node.getKwargs().size();
        int min = signature.getRequiredParameterCount();
        int max = signature.getParameterCount();
        if (provided < min || provided > max) {
            String expected = (min == max) ? String.valueOf(min) : (min + " to " + max);
            reportError(SemanticErrorType.ARGUMENT_MISMATCH,
                "Function '" + calleeIdentifier.getName() + "' expects " + expected
                    + " argument(s) but was called with " + provided,
                node.getLine(), node.getColumn(), node.getNodeName());
        }
    }

    /**
     * Python Semantic Error #4: whether the point currently being visited
     * is inside a function, for {@code return}. Any scope kind may sit
     * between the current position and that function (a return inside a
     * loop, inside an if-body, ... is still valid), so this walks all the
     * way up the chain.
     */
    private boolean isInsideFunction() {
        for (Scope scope = symbolTable.currentScope(); scope != null; scope = scope.getParent()) {
            if (scope.getType() == ScopeType.FUNCTION) {
                return true;
            }
        }
        return false;
    }

    /**
     * Python Semantic Error #5: whether the point currently being visited
     * is inside a loop, for {@code break}/{@code continue}. Unlike {@link
     * #isInsideFunction()}, this search must stop at a function boundary:
     * a nested function's body cannot break an outer function's loop just
     * because it happens to be lexically nested inside one.
     */
    private boolean isInsideLoop() {
        for (Scope scope = symbolTable.currentScope(); scope != null; scope = scope.getParent()) {
            if (scope.getType() == ScopeType.LOOP) {
                return true;
            }
            if (scope.getType() == ScopeType.FUNCTION) {
                return false;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Symbol definition
    // ------------------------------------------------------------------

    /**
     * Builds a {@link Symbol} from {@code sourceNode}'s own position and
     * node name and defines it in whatever scope is currently active,
     * reporting {@code duplicateMessage} as a {@link
     * SemanticErrorType#DUPLICATE_DEFINITION} if a symbol of that name
     * already existed directly in this scope (see {@link
     * semantic.scope.Scope#define}).
     *
     * @param signature optional {@link FunctionSignature} for a {@link
     *                  SymbolType#FUNCTION} symbol, or {@code null}
     */
    private void defineWithDuplicateCheck(String name, SymbolType symbolType, ASTNode sourceNode,
                                           String duplicateMessage, FunctionSignature signature) {
        Symbol symbol = new Symbol(
            name,
            symbolType,
            sourceNode.getLine(),
            sourceNode.getColumn(),
            sourceNode.getNodeName(),
            symbolTable.currentScope().getType(),
            signature
        );
        boolean isNew = symbolTable.define(symbol);
        if (!isNew) {
            reportError(SemanticErrorType.DUPLICATE_DEFINITION, duplicateMessage,
                sourceNode.getLine(), sourceNode.getColumn(), sourceNode.getNodeName());
        }
    }

    /**
     * Builds a {@link FunctionSignature} from a Python function
     * definition's parameter list, using {@code Parameter#hasDefault()}
     * for the required-parameter count and {@code Parameter#isVarArgs()}/
     * {@code #isKwArgs()} to detect a variadic signature.
     */
    private FunctionSignature buildFunctionSignature(FunctionDefNode node) {
        List<String> names = new ArrayList<>();
        int required = 0;
        boolean variadic = false;
        for (Parameter parameter : node.getParameters()) {
            if (parameter.isVarArgs() || parameter.isKwArgs()) {
                variadic = true;
                continue;
            }
            names.add(parameter.getName());
            if (!parameter.hasDefault()) {
                required++;
            }
        }
        return new FunctionSignature(names, required, variadic);
    }

    /** Records a semantic error against the shared error list. */
    protected void reportError(SemanticErrorType type, String message, int line, int column, String nodeName) {
        errors.add(new SemanticError(type, message, line, column, nodeName));
    }
}
