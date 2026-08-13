package semantic.visitor;

import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaAttributeAccessNode;
import template.ast.jinja.JinjaCallNode;
import template.ast.jinja.JinjaElifNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaVariableNode;
import template.visitor.TemplateBaseVisitor;
import semantic.error.SemanticError;
import semantic.error.SemanticErrorType;
import semantic.scope.ScopeType;
import semantic.scope.SymbolTable;
import semantic.symbol.Symbol;
import semantic.symbol.SymbolType;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Walks the template AST ({@code TemplateProgramNode} and everything
 * beneath it — both the HTML and Jinja2 sides), building up scopes and
 * symbols as declarations are encountered and reporting semantic errors as
 * violations are found.
 *
 * <p>This class extends {@link TemplateBaseVisitor}, the same base every
 * other template-side visitor in this project builds on. Every node type
 * this visitor does not override — every HTML/CSS node, and most Jinja2
 * nodes (filters, comments, extends/include, ...) — is still fully
 * traversed through the inherited default pass-through.
 *
 * <p>Scope-shaping overrides (unchanged in intent from the previous phase):
 * <ul>
 *   <li>{@link #visitProgram} — pushes the template's root {@link ScopeType#JINJA} scope.</li>
 *   <li>{@link #visitJinjaFor} — pushes a nested {@link ScopeType#JINJA}
 *       scope around the loop body and defines its loop variable.</li>
 *   <li>{@link #visitJinjaMacro} — defines the macro's own name, then
 *       pushes a nested {@link ScopeType#JINJA} scope and defines its parameters.</li>
 * </ul>
 * {@code {% block %}} is still deliberately left un-overridden: real
 * Jinja2 blocks introduce no new variable scope.
 *
 * <p>Checking overrides added this phase, one per Jinja2 semantic error
 * required by the current instructions:
 * <ul>
 *   <li>{@link #checkNameIsDefined} (the shared "is this name known" check,
 *       used by every reference site below) — #1 undefined template
 *       variable, #2 undefined variable inside {@code {{ }}} ({@link
 *       #visitJinjaVariable}, {@link #visitJinjaIdentifier}), #3 undefined
 *       variable inside {@code {% if %}} ({@link #visitJinjaIf}, {@link
 *       #visitJinjaElif}), #4 undefined variable inside {@code {% for %}}
 *       ({@link #visitJinjaFor}'s iterable). These are really the same
 *       check applied in four syntactic positions; overriding the shared
 *       leaf ({@link #visitJinjaIdentifier}) plus the two flattened-string
 *       sites ({@code {{ }}}/{@code {% for %}}/{@code {% if %}} conditions,
 *       which the real {@code TemplateASTBuilder} still hands this visitor
 *       as a structured tree it can walk) is what makes it fire in all four.</li>
 *   <li>{@link #visitJinjaAttributeAccess} / {@link #checkAttributeAccessBase}
 *       — #5 invalid access to object attribute: a {@code .attr} whose
 *       base resolves to a macro rather than a value.</li>
 *   <li>{@link #checkDuplicateLoopVariable} (called from {@link
 *       #visitJinjaFor}) — #6 duplicate loop variable: a {@code {% for %}}
 *       loop variable that shadows an already-active outer loop variable.</li>
 *   <li>{@link #visitJinjaCall} — #7 undefined macro call: a {@code
 *       name(...)} whose callee does not resolve to a defined {@code
 *       JinjaMacroNode}.</li>
 * </ul>
 *
 * <p>Every check collects into the shared error list via {@link
 * #reportError} and keeps walking — nothing here stops at the first error.
 *
 * <p>Template variables are frequently supplied from outside the template
 * entirely — passed in through {@code render_template(name, **context)} on
 * the Python side rather than bound anywhere in the Jinja2 AST itself.
 * {@link #setExternalContextVariableNames} lets {@code SemanticAnalyzer}
 * pass in the keyword-argument names {@link FlaskSemanticVisitor} collected
 * from any {@code render_template(...)} calls it found, so that check
 * #1-#4 recognizes them as legitimately defined instead of flagging every
 * ordinary context variable (e.g. the {@code items} in this project's own
 * demo template) as undefined.
 */
public class TemplateSemanticVisitor extends TemplateBaseVisitor<Void> {

    /**
     * Jinja2's own global callables, always available without any macro
     * definition or import. A small, hand-picked set (not an attempt at a
     * complete list of Jinja2 built-ins/filters) scoped to what templates
     * realistically call directly, so "undefined macro call" and the
     * undefined-variable checks do not flag ordinary, valid usage like
     * {@code {% for i in range(count) %}}.
     */
    private static final Set<String> JINJA_BUILTIN_CALLABLES =
        Set.of("range", "dict", "namespace", "lipsum", "cycler", "joiner");

    private final SymbolTable symbolTable;
    private final List<SemanticError> errors;

    /**
     * Names known to be supplied from outside the template (typically via
     * {@code render_template(name, **context)} on the Python side) — see
     * the class-level documentation. Empty by default, so a
     * template-only analysis (no corresponding Python AST) degrades
     * gracefully to checking only against what the template itself defines.
     */
    private Set<String> externalContextVariableNames = Collections.emptySet();

    public TemplateSemanticVisitor(SymbolTable symbolTable, List<SemanticError> errors) {
        this.symbolTable = symbolTable;
        this.errors = errors;
    }

    /**
     * Supplies the set of names considered externally defined (e.g. from
     * {@code render_template}'s keyword arguments) for every undefined-name
     * check this visitor performs from this point on.
     */
    public void setExternalContextVariableNames(Set<String> names) {
        this.externalContextVariableNames = (names == null) ? Collections.emptySet() : names;
    }

    @Override
    public Void visitProgram(TemplateProgramNode node) {
        symbolTable.enterScope(ScopeType.JINJA);
        try {
            super.visitProgram(node);
        } finally {
            symbolTable.exitScope();
        }
        return null;
    }

    @Override
    public Void visitJinjaFor(JinjaForNode node) {
        // Jinja2 Semantic Error #4: undefined variable inside {% for %} -
        // the iterable is checked in the ENCLOSING scope, before the
        // loop's own scope (and its loop variable) exist.
        if (node.getIterableTree() != null) {
            visit(node.getIterableTree());
        } else {
            checkRootOfChain(node.getIterable(), node);
        }

        symbolTable.enterScope(ScopeType.JINJA);
        try {
            if (node.getLoopVariable() != null) {
                // Jinja2 Semantic Error #6: duplicate loop variable.
                checkDuplicateLoopVariable(node.getLoopVariable(), node);
                defineWithDuplicateCheck(node.getLoopVariable(), SymbolType.LOOP_VARIABLE, node,
                    "Duplicate loop variable: '" + node.getLoopVariable() + "' is already defined in this scope");
            }
            // Jinja2's implicit per-iteration context object (loop.index,
            // loop.first, loop.last, ...), available for the duration of
            // this loop's own body. Defined directly (not through the
            // duplicate-check path above): a fresh "loop" per for-loop is
            // normal and expected, not a redeclaration mistake.
            defineInCurrentScope("loop", SymbolType.LOOP_VARIABLE, node);

            for (JinjaNode child : node.getBody()) {
                visit(child);
            }
        } finally {
            symbolTable.exitScope();
        }

        // A for-else clause runs only when the iterable was empty, in the
        // ENCLOSING scope - it is not part of the loop's own scope.
        if (node.hasElse()) {
            for (JinjaNode child : node.getElseBody()) {
                visit(child);
            }
        }
        return null;
    }

    @Override
    public Void visitJinjaMacro(JinjaMacroNode node) {
        // The macro's own name belongs to the ENCLOSING scope (that is
        // what makes it callable from outside).
        defineWithDuplicateCheck(node.getMacroName(), SymbolType.FUNCTION, node,
            "Duplicate macro definition: '" + node.getMacroName() + "' is already defined in this scope");

        symbolTable.enterScope(ScopeType.JINJA);
        try {
            for (String parameterName : node.getParameters()) {
                // Macro parameters are plain strings with no position of
                // their own, so the enclosing JinjaMacroNode's position is
                // used as the closest available location.
                defineWithDuplicateCheck(parameterName, SymbolType.PARAMETER, node,
                    "Duplicate parameter name: '" + parameterName
                        + "' appears more than once in the parameter list of macro '" + node.getMacroName() + "'");
            }
            for (JinjaNode child : node.getBody()) {
                visit(child);
            }
        } finally {
            symbolTable.exitScope();
        }
        return null;
    }

    /**
     * Jinja2 Semantic Errors #1/#2: undefined template variable / undefined
     * variable inside {@code {{ }}}. {@code JinjaVariableNode} represents
     * any {@code name[.attr|[idx]|(args)]*} chain with no operator (see
     * its class documentation) as a single flattened string, so the root
     * name is extracted for the undefined check, and — when the chain
     * accesses an attribute directly off that root — Semantic Error #5 is
     * checked too.
     */
    @Override
    public Void visitJinjaVariable(JinjaVariableNode node) {
        checkRootOfChain(node.getVariableName(), node);
        return super.visitJinjaVariable(node);
    }

    /**
     * The leaf case within a structured Jinja2 expression tree (built for
     * anything with a real operator — see {@code JinjaExpressionNode}) —
     * covers a bare name found inside {@code {{ }}}, {@code {% if %}}, or
     * {@code {% for %}} alike, since all three hand this visitor a
     * structured tree it walks down to this method.
     */
    @Override
    public Void visitJinjaIdentifier(JinjaIdentifierNode node) {
        checkNameIsDefined(node.getName(), node);
        return super.visitJinjaIdentifier(node);
    }

    /**
     * Jinja2 Semantic Error #5: invalid access to object attribute. Only
     * checked when the object being accessed is DIRECTLY a bare name (not
     * a deeper chain, whose own "kind" this analyzer cannot know) that
     * resolves to a macro: a callable is not a data value, so accessing an
     * arbitrary attribute on it is almost certainly a mistake. The base is
     * still visited normally afterward, so an undefined base is still
     * caught by {@link #visitJinjaIdentifier} either way.
     */
    @Override
    public Void visitJinjaAttributeAccess(JinjaAttributeAccessNode node) {
        if (node.getObject() instanceof JinjaIdentifierNode baseIdentifier) {
            checkAttributeAccessBase(baseIdentifier.getName(), node.getAttributeName(), node);
        }
        return super.visitJinjaAttributeAccess(node);
    }

    /**
     * Jinja2 Semantic Error #3: undefined variable inside {@code {% if %}}.
     * Not overridden by the inherited default at all — see the class-level
     * documentation — so both the {@code if}'s own condition and the body
     * (delegated to {@link #visitJinjaElif} for each {@code elif}, which
     * checks its own condition the same way) are handled here.
     */
    @Override
    public Void visitJinjaIf(JinjaIfNode node) {
        checkCondition(node.getCondition(), node.getConditionTree(), node);

        for (JinjaNode child : node.getThenBody()) {
            visit(child);
        }
        for (JinjaElifNode elif : node.getElifNodes()) {
            visit(elif);
        }
        if (node.hasElse()) {
            visit(node.getElseNode());
        }
        return null;
    }

    /** @see #visitJinjaIf */
    @Override
    public Void visitJinjaElif(JinjaElifNode node) {
        checkCondition(node.getCondition(), node.getConditionTree(), node);
        for (JinjaNode child : node.getBody()) {
            visit(child);
        }
        return null;
    }

    /**
     * Jinja2 Semantic Error #7: undefined macro call. Checked only when
     * the callee is directly a bare name (the overwhelming common case,
     * {@code macro_name(...)}); anything more unusual falls back to normal
     * traversal instead of guessing.
     */
    @Override
    public Void visitJinjaCall(JinjaCallNode node) {
        if (node.getCallee() instanceof JinjaIdentifierNode calleeIdentifier) {
            checkMacroCall(calleeIdentifier.getName(), node);
        } else {
            visit(node.getCallee());
        }
        for (JinjaNode argument : node.getArguments()) {
            visit(argument);
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Checks
    // ------------------------------------------------------------------

    /**
     * The shared "is this name known" check behind Jinja2 Semantic Errors
     * #1-#4: resolvable in the current scope chain, a name supplied
     * externally (see {@link #setExternalContextVariableNames}), or one of
     * Jinja2's own global callables.
     */
    private void checkNameIsDefined(String name, JinjaNode referenceNode) {
        if (symbolTable.resolve(name) != null
                || externalContextVariableNames.contains(name)
                || JINJA_BUILTIN_CALLABLES.contains(name)) {
            return;
        }
        reportError(SemanticErrorType.UNDEFINED_JINJA_VARIABLE,
            "Undefined template variable '" + name + "'",
            referenceNode.getLine(), referenceNode.getColumn(), referenceNode.getNodeName());
    }

    /**
     * Splits a {@code name[.attr|[idx]|(args)]*} flattened chain (as
     * produced by {@code JinjaVariableNode}, and used as a fallback for
     * {@code JinjaForNode#getIterable()}/{@code JinjaIfNode#getCondition()}
     * when no structured tree was built) into its root identifier, checks
     * that root for Semantic Errors #1-#4, and — if the chain accesses an
     * attribute directly off that root — checks Semantic Error #5 too.
     */
    private void checkRootOfChain(String chain, JinjaNode referenceNode) {
        if (chain == null) {
            return;
        }
        String root = extractRootIdentifier(chain);
        if (root.isEmpty()) {
            return;
        }
        checkNameIsDefined(root, referenceNode);
        if (chain.length() > root.length() && chain.charAt(root.length()) == '.') {
            checkAttributeAccessBase(root, extractFirstAttributeAfterDot(chain, root), referenceNode);
        }
    }

    /** Checks a {@code {% if %}}/{@code {% elif %}} condition, preferring the structured tree when available. */
    private void checkCondition(String condition, JinjaNode conditionTree, JinjaNode referenceNode) {
        if (conditionTree != null) {
            visit(conditionTree);
        } else {
            checkRootOfChain(condition, referenceNode);
        }
    }

    /** Jinja2 Semantic Error #5: invalid access to object attribute. */
    private void checkAttributeAccessBase(String rootName, String attributeName, JinjaNode accessNode) {
        Symbol symbol = symbolTable.resolve(rootName);
        if (symbol != null && symbol.getType() == SymbolType.FUNCTION) {
            reportError(SemanticErrorType.INVALID_ATTRIBUTE_ACCESS,
                "Invalid attribute access: '" + rootName + "' is a macro, not a value, so '."
                    + attributeName + "' is not valid",
                accessNode.getLine(), accessNode.getColumn(), accessNode.getNodeName());
        }
    }

    /** Jinja2 Semantic Error #6: duplicate loop variable. */
    private void checkDuplicateLoopVariable(String loopVariableName, JinjaForNode node) {
        Symbol existing = symbolTable.resolve(loopVariableName);
        if (existing != null && existing.getType() == SymbolType.LOOP_VARIABLE) {
            reportError(SemanticErrorType.DUPLICATE_DEFINITION,
                "Duplicate loop variable: '" + loopVariableName + "' shadows an outer '{% for %}' loop variable",
                node.getLine(), node.getColumn(), node.getNodeName());
        }
    }

    /** Jinja2 Semantic Error #7: undefined macro call (only checked "if macros exist in the project"). */
    private void checkMacroCall(String macroName, JinjaCallNode node) {
        if (JINJA_BUILTIN_CALLABLES.contains(macroName)) {
            return;
        }
        Symbol symbol = symbolTable.resolve(macroName);
        if (symbol == null || symbol.getType() != SymbolType.FUNCTION) {
            reportError(SemanticErrorType.UNDEFINED_JINJA_MACRO,
                "Undefined macro call: no macro named '" + macroName + "' is defined",
                node.getLine(), node.getColumn(), node.getNodeName());
        }
    }

    // ------------------------------------------------------------------
    // String-chain helpers
    // ------------------------------------------------------------------

    /** Returns the identifier before the first {@code .}, {@code [}, or {@code (} in {@code chain}, or all of it if none appear. */
    private static String extractRootIdentifier(String chain) {
        int cut = chain.length();
        for (int i = 0; i < chain.length(); i++) {
            char c = chain.charAt(i);
            if (c == '.' || c == '[' || c == '(') {
                cut = i;
                break;
            }
        }
        return chain.substring(0, cut).trim();
    }

    /** Returns the identifier characters immediately after {@code root + "."} in {@code chain}. */
    private static String extractFirstAttributeAfterDot(String chain, String root) {
        int start = root.length() + 1;
        int end = start;
        while (end < chain.length() && (Character.isLetterOrDigit(chain.charAt(end)) || chain.charAt(end) == '_')) {
            end++;
        }
        return (start <= end && end <= chain.length()) ? chain.substring(start, end) : "";
    }

    // ------------------------------------------------------------------
    // Symbol definition
    // ------------------------------------------------------------------

    /**
     * Builds a {@link Symbol} from {@code sourceNode}'s own position and
     * node name and defines it in whatever scope is currently active,
     * reporting {@code duplicateMessage} as a {@link
     * SemanticErrorType#DUPLICATE_DEFINITION} if a symbol of that name
     * already existed directly in this scope.
     */
    private void defineWithDuplicateCheck(String name, SymbolType symbolType, JinjaNode sourceNode, String duplicateMessage) {
        boolean isNew = symbolTable.define(buildSymbol(name, symbolType, sourceNode));
        if (!isNew) {
            reportError(SemanticErrorType.DUPLICATE_DEFINITION, duplicateMessage,
                sourceNode.getLine(), sourceNode.getColumn(), sourceNode.getNodeName());
        }
    }

    /** Defines a symbol without any duplicate-declaration reporting — see {@link #visitJinjaFor}'s "loop" definition. */
    private void defineInCurrentScope(String name, SymbolType symbolType, JinjaNode sourceNode) {
        symbolTable.define(buildSymbol(name, symbolType, sourceNode));
    }

    private Symbol buildSymbol(String name, SymbolType symbolType, JinjaNode sourceNode) {
        return new Symbol(
            name,
            symbolType,
            sourceNode.getLine(),
            sourceNode.getColumn(),
            sourceNode.getNodeName(),
            symbolTable.currentScope().getType()
        );
    }

    /** Records a semantic error against the shared error list. */
    protected void reportError(SemanticErrorType type, String message, int line, int column, String nodeName) {
        errors.add(new SemanticError(type, message, line, column, nodeName));
    }
}
