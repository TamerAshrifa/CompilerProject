package generator;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.expressions.access.AttributeAccessNode;
import flask.ast.nodes.expressions.access.FunctionCallNode;
import flask.ast.nodes.expressions.access.SubscriptNode;
import flask.ast.nodes.expressions.atoms.DictNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.ListNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.expressions.atoms.SetNode;
import flask.ast.nodes.expressions.atoms.TupleNode;
import flask.ast.nodes.expressions.comprehensions.ComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.DictComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.GeneratorExpressionNode;
import flask.ast.nodes.expressions.comprehensions.ListComprehensionNode;
import flask.ast.nodes.expressions.comprehensions.SetComprehensionNode;
import flask.ast.nodes.expressions.operations.BinaryOpNode;
import flask.ast.nodes.expressions.operations.CompareNode;
import flask.ast.nodes.expressions.operations.LambdaNode;
import flask.ast.nodes.expressions.operations.UnaryOpNode;
import flask.ast.nodes.helpers.Decorator;
import flask.ast.nodes.helpers.ExceptClause;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.nodes.helpers.WithItem;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.ClassDefNode;
import flask.ast.nodes.statements.compound.ForStatementNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.IfStatementNode;
import flask.ast.nodes.statements.compound.TryStatementNode;
import flask.ast.nodes.statements.compound.WhileStatementNode;
import flask.ast.nodes.statements.compound.WithStatementNode;
import flask.ast.nodes.statements.imports.FromImportNode;
import flask.ast.nodes.statements.imports.ImportNode;
import flask.ast.nodes.statements.simple.AssertNode;
import flask.ast.nodes.statements.simple.AssignmentNode;
import flask.ast.nodes.statements.simple.BreakNode;
import flask.ast.nodes.statements.simple.ContinueNode;
import flask.ast.nodes.statements.simple.DelNode;
import flask.ast.nodes.statements.simple.ExpressionStatementNode;
import flask.ast.nodes.statements.simple.GlobalNode;
import flask.ast.nodes.statements.simple.NonlocalNode;
import flask.ast.nodes.statements.simple.PassNode;
import flask.ast.nodes.statements.simple.RaiseNode;
import flask.ast.nodes.statements.simple.ReturnNode;
import flask.ast.visitor.ASTVisitor;

import java.util.List;
import java.util.Map;

/**
 * Regenerates Python source text from the existing Flask/Python AST
 * ({@code flask.ast.nodes.*}).
 *
 * <p>This is a {@code CodeGenerator}-family class (see {@link CodeGenerator}
 * for how it fits into the overall Requirement 5 architecture): it implements
 * the project's existing {@link ASTVisitor} directly, so every node type
 * generates its own text through the standard double-dispatch
 * {@code node.accept(this)} call — there is no {@code instanceof}/switch
 * ladder deciding "what kind of node is this and how do I print it". Two
 * narrow, deliberate exceptions are documented inline where they occur:
 * comprehension sub-type dispatch (four sibling node types share a single
 * {@link ASTVisitor#visitComprehension} entry point by design of the
 * existing interface) and operator-precedence-aware parenthesization (an
 * inherent property of unparsing expression trees — knowing whether a child
 * needs parentheses requires comparing the child's own operator precedence
 * against the parent's, which is a relationship between two nodes, not a
 * fact about either one alone). Neither replaces the generation dispatch
 * itself, which remains 100% {@code accept()}-driven.
 *
 * <p>This class does not depend on the {@code semantic} package in any way,
 * consistent with keeping code generation independent of semantic analysis.
 * It also does not depend on the existing {@link Generator}/{@link Context}/
 * {@link TemplateTransformer} machinery — this class's only job is turning a
 * Python AST into Python text. Deriving the "Intermediate Generation Data"
 * used to resolve template variables is handled by the already-existing
 * {@link PythonContextExtractor} (see {@link CodeGenerator} for how the two
 * are combined).
 *
 * <p><b>Known, honest limitations</b> (inherited from what the existing
 * parser/AST already does — nothing here tries to work around them):
 * <ul>
 *   <li>Augmented assignment ({@code x += 1}) is desugared by
 *       {@code FlaskASTBuilder} into {@code AssignmentNode(x, BinaryOpNode(x, "+", 1))},
 *       so it regenerates as {@code x = x + 1}: semantically identical,
 *       textually different.</li>
 *   <li>f-strings are tokenized as plain strings (the {@code f} prefix is
 *       discarded during parsing, before this class ever sees the node), so
 *       they regenerate as a normal string literal.</li>
 *   <li>{@code import x as y} / {@code from m import x as y} store the
 *       aliased name as a single source-text slice that ANTLR's
 *       {@code getText()} joins with no separating spaces; this class emits
 *       that text exactly as stored rather than guessing at spacing.</li>
 * </ul>
 * None of these are new gaps introduced by this class — they already exist
 * one layer down, in the AST this class faithfully reads.
 */
public class PythonGenerator implements ASTVisitor<String> {

    /** Operator precedence used only to decide whether a nested operand needs parentheses. */
    private static final int OR_PRECEDENCE = 1;
    private static final int AND_PRECEDENCE = 2;
    private static final int NOT_PRECEDENCE = 3;
    private static final int COMPARE_PRECEDENCE = 4;
    private static final int ADDITIVE_PRECEDENCE = 5;
    private static final int MULTIPLICATIVE_PRECEDENCE = 6;
    private static final int UNARY_SIGN_PRECEDENCE = 7;
    private static final int POWER_PRECEDENCE = 8;
    /** Calls, subscripts, attribute access, literals, and bracketed collections never need parens. */
    private static final int ATOM_PRECEDENCE = 9;

    private final GenerationSupport support = new GenerationSupport();

    /**
     * Generates Python source for an entire program (the AST root produced by
     * {@code FlaskASTBuilder} / exposed as {@code CompilerPipeline.Result#getPythonAst()}).
     */
    public String generate(ProgramNode program) {
        support.reset();
        String generated = program.accept(this);
        // A trailing newline is both conventional for a source file and
        // required by this grammar's statement rule (each statement,
        // including the last, must be NEWLINE-terminated); without it a
        // round-trip re-parse of the last line can trigger avoidable parser
        // error recovery.
        return generated.isEmpty() || generated.endsWith("\n") ? generated : generated + "\n";
    }

    /** The recorded (node name, source line/column, emission order) trail from the last {@link #generate}. */
    public List<SourceMapping> getSourceMap() {
        return support.getSourceMap();
    }

    // ------------------------------------------------------------------
    // Program / top level
    // ------------------------------------------------------------------

    @Override
    public String visitProgram(ProgramNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return joinStatements(node.getStatements());
    }

    // ------------------------------------------------------------------
    // Simple statements
    // ------------------------------------------------------------------

    @Override
    public String visitAssignment(AssignmentNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + node.getTarget().accept(this) + " = " + node.getValue().accept(this);
    }

    @Override
    public String visitExpressionStatement(ExpressionStatementNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + node.getExpression().accept(this);
    }

    @Override
    public String visitReturn(ReturnNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        List<Expression> values = node.getValues();
        if (values.isEmpty()) {
            return support.indent() + "return";
        }
        return support.indent() + "return " + joinExpressions(values);
    }

    @Override
    public String visitPass(PassNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "pass";
    }

    @Override
    public String visitBreak(BreakNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "break";
    }

    @Override
    public String visitContinue(ContinueNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "continue";
    }

    @Override
    public String visitDel(DelNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "del " + joinExpressions(node.getTargets());
    }

    @Override
    public String visitAssert(AssertNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String text = support.indent() + "assert " + node.getTest().accept(this);
        if (node.hasMessage()) {
            text += ", " + node.getMessage().accept(this);
        }
        return text;
    }

    @Override
    public String visitGlobal(GlobalNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "global " + String.join(", ", node.getNames());
    }

    @Override
    public String visitNonlocal(NonlocalNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "nonlocal " + String.join(", ", node.getNames());
    }

    @Override
    public String visitRaise(RaiseNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        if (node.isBareRaise()) {
            return support.indent() + "raise";
        }
        String text = support.indent() + "raise " + node.getException().accept(this);
        if (node.hasCause()) {
            text += " from " + node.getCause().accept(this);
        }
        return text;
    }

    @Override
    public String visitImport(ImportNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "import " + String.join(", ", node.getImports());
    }

    @Override
    public String visitFromImport(FromImportNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "from " + node.getModuleName() + " import " + String.join(", ", node.getImports());
    }

    // ------------------------------------------------------------------
    // Compound statements
    // ------------------------------------------------------------------

    @Override
    public String visitFunctionDef(FunctionDefNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(renderDecorators(node.getDecorators()));
        sb.append(support.indent()).append("def ").append(node.getName()).append('(');
        sb.append(renderParameters(node.getParameters()));
        sb.append(')');
        if (node.hasReturnType()) {
            sb.append(" -> ").append(node.getReturnType().accept(this));
        }
        sb.append(":\n");
        sb.append(renderBody(node.getBody()));
        return sb.toString();
    }

    @Override
    public String visitClassDef(ClassDefNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(renderDecorators(node.getDecorators()));
        sb.append(support.indent()).append("class ").append(node.getName());
        if (!node.getBases().isEmpty()) {
            sb.append('(').append(joinExpressions(node.getBases())).append(')');
        }
        sb.append(":\n");
        sb.append(renderBody(node.getBody()));
        return sb.toString();
    }

    @Override
    public String visitIfStatement(IfStatementNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("if ").append(node.getCondition().accept(this)).append(":\n");
        sb.append(renderBody(node.getThenBody()));
        for (IfStatementNode.ElifClause elif : node.getElifClauses()) {
            sb.append('\n').append(support.indent()).append("elif ")
                    .append(elif.getCondition().accept(this)).append(":\n");
            sb.append(renderBody(elif.getBody()));
        }
        if (node.hasElse()) {
            sb.append('\n').append(support.indent()).append("else:\n");
            sb.append(renderBody(node.getElseBody()));
        }
        return sb.toString();
    }

    @Override
    public String visitForStatement(ForStatementNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("for ").append(node.getTarget().accept(this))
                .append(" in ").append(node.getIterable().accept(this)).append(":\n");
        sb.append(renderBody(node.getBody()));
        if (node.hasElse()) {
            sb.append('\n').append(support.indent()).append("else:\n");
            sb.append(renderBody(node.getElseBody()));
        }
        return sb.toString();
    }

    @Override
    public String visitWhileStatement(WhileStatementNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("while ").append(node.getCondition().accept(this)).append(":\n");
        sb.append(renderBody(node.getBody()));
        if (node.hasElse()) {
            sb.append('\n').append(support.indent()).append("else:\n");
            sb.append(renderBody(node.getElseBody()));
        }
        return sb.toString();
    }

    @Override
    public String visitWithStatement(WithStatementNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder items = new StringBuilder();
        List<WithItem> withItems = node.getItems();
        for (int i = 0; i < withItems.size(); i++) {
            if (i > 0) {
                items.append(", ");
            }
            WithItem item = withItems.get(i);
            items.append(item.getContextExpr().accept(this));
            if (item.hasAsName()) {
                items.append(" as ").append(item.getAsName().accept(this));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("with ").append(items).append(":\n");
        sb.append(renderBody(node.getBody()));
        return sb.toString();
    }

    @Override
    public String visitTryStatement(TryStatementNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("try:\n");
        sb.append(renderBody(node.getTryBody()));
        for (ExceptClause clause : node.getExceptClauses()) {
            sb.append('\n').append(support.indent()).append("except");
            if (!clause.isBareExcept()) {
                sb.append(' ').append(clause.getExceptionType().accept(this));
                if (clause.getName() != null) {
                    sb.append(" as ").append(clause.getName());
                }
            }
            sb.append(":\n");
            sb.append(renderBody(clause.getBody()));
        }
        if (node.hasElse()) {
            sb.append('\n').append(support.indent()).append("else:\n");
            sb.append(renderBody(node.getElseBody()));
        }
        if (node.hasFinally()) {
            sb.append('\n').append(support.indent()).append("finally:\n");
            sb.append(renderBody(node.getFinallyBody()));
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Operators
    // ------------------------------------------------------------------

    @Override
    public String visitBinaryOp(BinaryOpNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        int precedence = operatorPrecedence(node.getOperator());
        // "**" is the sole right-associative operator this grammar produces:
        // its right operand may repeat at the same precedence without parens
        // (2 ** 3 ** 2 means 2 ** (3 ** 2) already), but a *left*-nested "**"
        // needs parens to keep that meaning ((a ** b) ** c is otherwise
        // indistinguishable from, and would silently change into, a ** b ** c).
        // Every other operator here is left-associative, so it is the mirror
        // image: the left operand may repeat at the same precedence, but a
        // right-nested one needs parens (a - (b - c) must not collapse into
        // the different expression a - b - c).
        boolean rightAssociative = "**".equals(node.getOperator());
        int leftThreshold = rightAssociative ? precedence + 1 : precedence;
        int rightThreshold = rightAssociative ? precedence : precedence + 1;
        String left = renderOperand(node.getLeft(), leftThreshold);
        String right = renderOperand(node.getRight(), rightThreshold);
        return left + " " + node.getOperator() + " " + right;
    }

    @Override
    public String visitUnaryOp(UnaryOpNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        boolean isNot = "not".equals(node.getOperator());
        int precedence = isNot ? NOT_PRECEDENCE : UNARY_SIGN_PRECEDENCE;
        String operand = renderOperand(node.getOperand(), precedence);
        return isNot ? "not " + operand : node.getOperator() + operand;
    }

    @Override
    public String visitCompare(CompareNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(renderOperand(node.getLeft(), COMPARE_PRECEDENCE + 1));
        List<String> operators = node.getOperators();
        List<Expression> comparators = node.getComparators();
        for (int i = 0; i < operators.size(); i++) {
            sb.append(' ').append(operators.get(i)).append(' ');
            sb.append(renderOperand(comparators.get(i), COMPARE_PRECEDENCE + 1));
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Atoms
    // ------------------------------------------------------------------

    @Override
    public String visitIdentifier(IdentifierNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return node.getName();
    }

    @Override
    public String visitLiteral(LiteralNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return renderLiteralValue(node.getValue());
    }

    @Override
    public String visitList(ListNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return "[" + joinExpressions(node.getElements()) + "]";
    }

    @Override
    public String visitDict(DictNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder("{");
        List<DictNode.DictItem> items = node.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            DictNode.DictItem item = items.get(i);
            sb.append(item.getKey().accept(this)).append(": ").append(item.getValue().accept(this));
        }
        return sb.append('}').toString();
    }

    @Override
    public String visitTuple(TupleNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        List<Expression> elements = node.getElements();
        if (elements.isEmpty()) {
            return "()";
        }
        if (elements.size() == 1) {
            // A single-element tuple requires a trailing comma to distinguish
            // it from a merely-parenthesized expression: (x,) is a tuple,
            // (x) is just x.
            return "(" + elements.get(0).accept(this) + ",)";
        }
        return "(" + joinExpressions(elements) + ")";
    }

    @Override
    public String visitSet(SetNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return "{" + joinExpressions(node.getElements()) + "}";
    }

    // ------------------------------------------------------------------
    // Access / calls
    // ------------------------------------------------------------------

    @Override
    public String visitAttributeAccess(AttributeAccessNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return renderOperand(node.getTarget(), ATOM_PRECEDENCE) + "." + node.getAttribute();
    }

    @Override
    public String visitFunctionCall(FunctionCallNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder(renderOperand(node.getCallee(), ATOM_PRECEDENCE));
        sb.append('(');
        boolean first = true;
        for (Expression arg : node.getArgs()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(arg.accept(this));
            first = false;
        }
        for (Expression starArg : node.getStarArgs()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append('*').append(starArg.accept(this));
            first = false;
        }
        for (Map.Entry<String, Expression> kwarg : node.getKwargs().entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(kwarg.getKey()).append('=').append(kwarg.getValue().accept(this));
            first = false;
        }
        for (Expression spread : node.getKwargsSpread()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("**").append(spread.accept(this));
            first = false;
        }
        return sb.append(')').toString();
    }

    @Override
    public String visitSubscript(SubscriptNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return renderOperand(node.getTarget(), ATOM_PRECEDENCE) + "[" + joinExpressions(node.getSlices()) + "]";
    }

    // ------------------------------------------------------------------
    // Lambda / comprehensions
    // ------------------------------------------------------------------

    @Override
    public String visitLambda(LambdaNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String params = String.join(", ", node.getParameters());
        String body = node.getBody().accept(this);
        return params.isEmpty() ? "lambda: " + body : "lambda " + params + ": " + body;
    }

    /**
     * List/set/dict comprehensions and bare generator expressions all funnel
     * through this single {@link ASTVisitor#visitComprehension} entry point —
     * that shared dispatch point is a design choice of the existing
     * {@code ASTVisitor} interface (which this class must not change), not
     * something introduced here. Distinguishing the four sibling node types
     * is therefore necessarily done once, locally, via {@code instanceof};
     * each branch then generates through the normal {@code accept()}
     * polymorphism for every nested expression it contains.
     */
    @Override
    public String visitComprehension(ComprehensionNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String clauses = renderForClauses(node.getClauses());
        if (node instanceof ListComprehensionNode) {
            Expression element = ((ListComprehensionNode) node).getElement();
            return "[" + element.accept(this) + clauses + "]";
        }
        if (node instanceof SetComprehensionNode) {
            Expression element = ((SetComprehensionNode) node).getElement();
            return "{" + element.accept(this) + clauses + "}";
        }
        if (node instanceof DictComprehensionNode) {
            DictComprehensionNode dictComp = (DictComprehensionNode) node;
            return "{" + dictComp.getKey().accept(this) + ": " + dictComp.getValue().accept(this) + clauses + "}";
        }
        if (node instanceof GeneratorExpressionNode) {
            // FlaskASTBuilder constructs GeneratorExpressionNode in exactly one
            // place: as a call argument (buildCallArguments), matching its own
            // Javadoc ("written WITHOUT surrounding brackets as the sole
            // argument of a call"). The enclosing call's own parens already
            // delimit it, so no extra parens are added here.
            Expression element = ((GeneratorExpressionNode) node).getElement();
            return element.accept(this) + clauses;
        }
        // Defensive fallback for any future ComprehensionNode subtype that
        // has not been wired in here yet; keeps generation total rather than
        // throwing, while remaining obviously visible if it is ever hit.
        return clauses;
    }

    private String renderForClauses(List<ComprehensionNode.ForClause> clauses) {
        StringBuilder sb = new StringBuilder();
        for (ComprehensionNode.ForClause clause : clauses) {
            sb.append(" for ").append(clause.getTarget().accept(this))
                    .append(" in ").append(clause.getIterable().accept(this));
            for (Expression condition : clause.getConditions()) {
                sb.append(" if ").append(condition.accept(this));
            }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Shared rendering helpers
    // ------------------------------------------------------------------

    private String joinStatements(List<Statement> statements) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < statements.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(statements.get(i).accept(this));
        }
        return sb.toString();
    }

    /** Renders an indented statement block, defensively emitting {@code pass} for an empty body. */
    private String renderBody(List<Statement> body) {
        support.increaseIndent();
        String result = body.isEmpty() ? support.indent() + "pass" : joinStatements(body);
        support.decreaseIndent();
        return result;
    }

    private String renderDecorators(List<Decorator> decorators) {
        StringBuilder sb = new StringBuilder();
        for (Decorator decorator : decorators) {
            sb.append(support.indent()).append('@').append(decorator.getName().accept(this));
            if (!decorator.getArgs().isEmpty() || !decorator.getKwargs().isEmpty()) {
                sb.append('(');
                boolean first = true;
                for (Expression arg : decorator.getArgs()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(arg.accept(this));
                    first = false;
                }
                for (Map.Entry<String, Expression> kwarg : decorator.getKwargs().entrySet()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(kwarg.getKey()).append('=').append(kwarg.getValue().accept(this));
                    first = false;
                }
                sb.append(')');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private String renderParameters(List<Parameter> parameters) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Parameter parameter = parameters.get(i);
            if (parameter.isVarArgs()) {
                sb.append('*');
            } else if (parameter.isKwArgs()) {
                sb.append("**");
            }
            sb.append(parameter.getName());
            if (parameter.hasTypeHint()) {
                sb.append(": ").append(parameter.getTypeHint().accept(this));
            }
            if (parameter.hasDefault()) {
                sb.append(parameter.hasTypeHint() ? " = " : "=").append(parameter.getDefaultValue().accept(this));
            }
        }
        return sb.toString();
    }

    private String joinExpressions(List<Expression> expressions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expressions.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(expressions.get(i).accept(this));
        }
        return sb.toString();
    }

    /**
     * Generates {@code operand} and wraps it in parentheses if its own
     * operator binds more loosely than {@code minimumPrecedence} requires.
     * Atomic expressions (identifiers, literals, calls, subscripts,
     * attribute access, bracketed collections, comprehensions) always report
     * {@link #ATOM_PRECEDENCE} and so are never wrapped.
     */
    private String renderOperand(Expression operand, int minimumPrecedence) {
        String text = operand.accept(this);
        if (operandPrecedence(operand) < minimumPrecedence) {
            return "(" + text + ")";
        }
        return text;
    }

    /**
     * The precedence an already-generated expression binds at, used solely to
     * decide parenthesization in {@link #renderOperand}. See the class
     * Javadoc for why this is a narrow, standard exception to
     * accept()-driven dispatch rather than a replacement for it.
     */
    private int operandPrecedence(Expression expression) {
        if (expression instanceof BinaryOpNode) {
            return operatorPrecedence(((BinaryOpNode) expression).getOperator());
        }
        if (expression instanceof CompareNode) {
            return COMPARE_PRECEDENCE;
        }
        if (expression instanceof UnaryOpNode) {
            return "not".equals(((UnaryOpNode) expression).getOperator()) ? NOT_PRECEDENCE : UNARY_SIGN_PRECEDENCE;
        }
        if (expression instanceof LambdaNode) {
            // A bare lambda reads ambiguously almost anywhere it is not the
            // entire expression, so it is conservatively always parenthesized
            // when nested under another operator.
            return 0;
        }
        return ATOM_PRECEDENCE;
    }

    private int operatorPrecedence(String operator) {
        switch (operator) {
            case "or":
                return OR_PRECEDENCE;
            case "and":
                return AND_PRECEDENCE;
            case "==":
            case "!=":
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "in":
            case "is":
                return COMPARE_PRECEDENCE;
            case "+":
            case "-":
                return ADDITIVE_PRECEDENCE;
            case "*":
            case "/":
                return MULTIPLICATIVE_PRECEDENCE;
            case "**":
                return POWER_PRECEDENCE;
            default:
                // Unrecognized operator (none of the ones FlaskASTBuilder
                // currently produces): a mid-table default keeps output
                // syntactically safe without this class having to guess.
                return MULTIPLICATIVE_PRECEDENCE;
        }
    }

    private String renderLiteralValue(Object value) {
        if (value == null) {
            return "None";
        }
        if (value instanceof Boolean) {
            return ((Boolean) value) ? "True" : "False";
        }
        if (value instanceof String) {
            return "'" + escapePythonString((String) value) + "'";
        }
        return String.valueOf(value);
    }

    private String escapePythonString(String raw) {
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
