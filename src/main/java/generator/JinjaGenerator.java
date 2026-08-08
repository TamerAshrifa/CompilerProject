package generator;

import template.ast.TemplateProgramNode;
import template.ast.TemplateRuleNode;
import template.ast.jinja.JinjaAttributeAccessNode;
import template.ast.jinja.JinjaBinaryOpNode;
import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaCallNode;
import template.ast.jinja.JinjaCommentNode;
import template.ast.jinja.JinjaCompareNode;
import template.ast.jinja.JinjaElifNode;
import template.ast.jinja.JinjaElseNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaExtendsNode;
import template.ast.jinja.JinjaFilterApplicationNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaIncludeNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaProgramNode;
import template.ast.jinja.JinjaSubscriptNode;
import template.ast.jinja.JinjaUnaryOpNode;
import template.ast.jinja.JinjaVariableNode;
import template.ast.jinja.LiteralNode;
import template.visitor.TemplateBaseVisitor;

import java.util.List;

/**
 * Regenerates Jinja2 template source text from the existing Jinja AST
 * ({@code template.ast.jinja.*}).
 *
 * <p>Extends {@link TemplateBaseVisitor} and overrides only the 22
 * Jinja-related {@code visitXxx} methods (see class list in
 * {@link template.visitor.TemplateVisitor}); the HTML/CSS/legacy methods are
 * left to their inherited defaults since this class is never invoked on
 * those node types. This is a pure serializer: it does not evaluate or
 * substitute anything itself. If a tree has already been run through the
 * existing {@link TemplateTransformer} (which replaces resolvable
 * {@link JinjaVariableNode}s with concrete {@link LiteralNode}s using a
 * {@link Context} derived from the Python side), this class will happily
 * print the result of that substitution — see {@link CodeGenerator} for how
 * the two are combined. Given an untransformed tree, it reproduces the
 * template's original Jinja2 constructs.
 *
 * <p><b>Two field/tree duality</b>: several Jinja nodes
 * ({@link JinjaIfNode}, {@link JinjaElifNode}, {@link JinjaForNode},
 * {@link JinjaExpressionNode}) carry both the original raw condition/iterable
 * text <em>and</em>, when the parser built one, a structured expression tree
 * covering the same source ({@code getConditionTree()} / {@code getIterableTree()}
 * / {@code getRoot()}). This class always prefers the structured tree
 * (generating it polymorphically through {@code accept()}) and falls back to
 * the raw text only when no tree is present (e.g. some hand-built test ASTs) —
 * there is nothing to "generate" from a plain string beyond printing it.
 *
 * <p><b>Bare vs. delimited rendering</b>: {@link JinjaVariableNode} and
 * {@link JinjaExpressionNode} are the only two node kinds that represent a
 * complete {@code {{ ... }}} output unit, so only their visit methods add the
 * {@code {{ }}} delimiters. Every other expression-tree node type
 * (identifiers, operators, attribute/subscript/call/filter-application) is,
 * by construction, always found nested inside one of those two, so their
 * visit methods emit bare expression syntax. {@link LiteralNode} is the one
 * exception worth calling out explicitly: {@code TemplateTransformer} only
 * ever produces it as a full replacement for what was a top-level
 * {@link JinjaVariableNode}/{@link JinjaExpressionNode} (never nested inside
 * an operator), so it is rendered bare too — it already stands for a fully
 * resolved value ready to print, not a variable reference needing {{ }}.
 */
public class JinjaGenerator extends TemplateBaseVisitor<String> {

    private static final int OR_PRECEDENCE = 1;
    private static final int AND_PRECEDENCE = 2;
    private static final int NOT_PRECEDENCE = 3;
    private static final int COMPARE_PRECEDENCE = 4;
    private static final int ADDITIVE_PRECEDENCE = 5;
    private static final int MULTIPLICATIVE_PRECEDENCE = 6;
    private static final int UNARY_MINUS_PRECEDENCE = 7;
    private static final int ATOM_PRECEDENCE = 8;

    private final GenerationSupport support = new GenerationSupport("  ");

    /** Generates Jinja2 text for every top-level Jinja construct in a parsed template. */
    public String generate(TemplateProgramNode program) {
        support.reset();
        return program.accept(this);
    }

    /** The recorded (node name, source line/column, emission order) trail from the last {@link #generate}. */
    public List<SourceMapping> getSourceMap() {
        return support.getSourceMap();
    }

    // ------------------------------------------------------------------
    // Roots
    // ------------------------------------------------------------------

    @Override
    public String visitProgram(TemplateProgramNode node) {
        return renderJinjaElements(node.getJinjaElements());
    }

    @Override
    public String visitRule(TemplateRuleNode node) {
        // Generic escape hatch node (mirrors flask.ast.nodes.ASTRuleNode); not
        // produced by TemplateASTBuilder for well-formed input, handled
        // defensively so generation stays total rather than throwing.
        StringBuilder sb = new StringBuilder();
        List<template.ast.TemplateNode> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(children.get(i).accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitJinjaProgram(JinjaProgramNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return renderJinjaElements(node.getElements());
    }

    // ------------------------------------------------------------------
    // Output units: the only two node kinds that add {{ }}
    // ------------------------------------------------------------------

    @Override
    public String visitJinjaVariable(JinjaVariableNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return "{{ " + node.getVariableName() + renderFilters(node.getFilters()) + " }}";
    }

    @Override
    public String visitJinjaExpression(JinjaExpressionNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String body = node.getRoot() != null ? node.getRoot().accept(this) : node.getExpression();
        return "{{ " + body + renderFilters(node.getFilters()) + " }}";
    }

    @Override
    public String visitJinjaLiteral(LiteralNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return node.getStringValue();
    }

    // ------------------------------------------------------------------
    // Bare expression-tree nodes (always nested; never add {{ }})
    // ------------------------------------------------------------------

    @Override
    public String visitJinjaIdentifier(JinjaIdentifierNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return node.getName();
    }

    @Override
    public String visitJinjaBinaryOp(JinjaBinaryOpNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        int precedence = jinjaOperatorPrecedence(node.getOperator());
        String left = renderJinjaOperand(node.getLeft(), precedence);
        String right = renderJinjaOperand(node.getRight(), precedence + 1);
        return left + " " + node.getOperator() + " " + right;
    }

    @Override
    public String visitJinjaCompare(JinjaCompareNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String left = renderJinjaOperand(node.getLeft(), COMPARE_PRECEDENCE + 1);
        String right = renderJinjaOperand(node.getRight(), COMPARE_PRECEDENCE + 1);
        return left + " " + node.getOperator() + " " + right;
    }

    @Override
    public String visitJinjaUnaryOp(JinjaUnaryOpNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        boolean isNot = "not".equals(node.getOperator());
        int precedence = isNot ? NOT_PRECEDENCE : UNARY_MINUS_PRECEDENCE;
        String operand = renderJinjaOperand(node.getOperand(), precedence);
        return isNot ? "not " + operand : node.getOperator() + operand;
    }

    @Override
    public String visitJinjaAttributeAccess(JinjaAttributeAccessNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return renderJinjaOperand(node.getObject(), ATOM_PRECEDENCE) + "." + node.getAttributeName();
    }

    @Override
    public String visitJinjaSubscript(JinjaSubscriptNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return renderJinjaOperand(node.getObject(), ATOM_PRECEDENCE) + "[" + node.getIndex().accept(this) + "]";
    }

    @Override
    public String visitJinjaCall(JinjaCallNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder(renderJinjaOperand(node.getCallee(), ATOM_PRECEDENCE));
        sb.append('(');
        List<JinjaNode> arguments = node.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(arguments.get(i).accept(this));
        }
        return sb.append(')').toString();
    }

    @Override
    public String visitJinjaFilterApplication(JinjaFilterApplicationNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return renderJinjaOperand(node.getTarget(), ATOM_PRECEDENCE) + renderFilters(node.getFilters());
    }

    @Override
    public String visitJinjaFilter(JinjaFilterNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return node.hasArguments()
                ? "|" + node.getFilterName() + "(" + node.getArguments() + ")"
                : "|" + node.getFilterName();
    }

    // ------------------------------------------------------------------
    // Control flow / inheritance tags
    // ------------------------------------------------------------------

    @Override
    public String visitJinjaIf(JinjaIfNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("{% if ").append(renderCondition(node.getCondition(), node.getConditionTree()))
                .append(" %}\n");
        sb.append(renderJinjaBody(node.getThenBody()));
        for (JinjaElifNode elif : node.getElifNodes()) {
            sb.append('\n').append(elif.accept(this));
        }
        if (node.hasElse()) {
            sb.append('\n').append(node.getElseNode().accept(this));
        }
        sb.append('\n').append(support.indent()).append("{% endif %}");
        return sb.toString();
    }

    @Override
    public String visitJinjaElif(JinjaElifNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("{% elif ").append(renderCondition(node.getCondition(), node.getConditionTree()))
                .append(" %}\n");
        sb.append(renderJinjaBody(node.getBody()));
        return sb.toString();
    }

    @Override
    public String visitJinjaElse(JinjaElseNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("{% else %}\n");
        sb.append(renderJinjaBody(node.getBody()));
        return sb.toString();
    }

    @Override
    public String visitJinjaFor(JinjaForNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        String iterable = node.getIterableTree() != null ? node.getIterableTree().accept(this) : node.getIterable();
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("{% for ").append(node.getLoopVariable())
                .append(" in ").append(iterable).append(" %}\n");
        sb.append(renderJinjaBody(node.getBody()));
        if (node.hasElse()) {
            sb.append('\n').append(support.indent()).append("{% else %}\n");
            sb.append(renderJinjaBody(node.getElseBody()));
        }
        sb.append('\n').append(support.indent()).append("{% endfor %}");
        return sb.toString();
    }

    @Override
    public String visitJinjaBlock(JinjaBlockNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("{% block ").append(node.getBlockName()).append(" %}\n");
        sb.append(renderJinjaBody(node.getBody()));
        sb.append('\n').append(support.indent()).append("{% endblock %}");
        return sb.toString();
    }

    @Override
    public String visitJinjaMacro(JinjaMacroNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("{% macro ").append(node.getMacroName())
                .append('(').append(String.join(", ", node.getParameters())).append(") %}\n");
        sb.append(renderJinjaBody(node.getBody()));
        sb.append('\n').append(support.indent()).append("{% endmacro %}");
        return sb.toString();
    }

    @Override
    public String visitJinjaExtends(JinjaExtendsNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("{% extends \"").append(node.getParentTemplatePath()).append("\" %}");
        if (!node.getChildren().isEmpty()) {
            sb.append('\n').append(renderJinjaElements(node.getChildren()));
        }
        return sb.toString();
    }

    @Override
    public String visitJinjaInclude(JinjaIncludeNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        StringBuilder sb = new StringBuilder();
        sb.append(support.indent()).append("{% include \"").append(node.getTemplatePath()).append('"');
        if (!node.isWithContext()) {
            sb.append(" without context");
        }
        sb.append(" %}");
        if (!node.getChildren().isEmpty()) {
            sb.append('\n').append(renderJinjaElements(node.getChildren()));
        }
        return sb.toString();
    }

    @Override
    public String visitJinjaComment(JinjaCommentNode node) {
        support.mark(node.getNodeName(), node.getLine(), node.getColumn());
        return support.indent() + "{# " + node.getContent() + " #}";
    }

    // ------------------------------------------------------------------
    // Shared rendering helpers
    // ------------------------------------------------------------------

    private String renderJinjaElements(List<JinjaNode> elements) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(elements.get(i).accept(this));
        }
        return sb.toString();
    }

    private String renderJinjaBody(List<JinjaNode> body) {
        support.increaseIndent();
        String result = renderJinjaElements(body);
        support.decreaseIndent();
        return result;
    }

    private String renderFilters(List<JinjaFilterNode> filters) {
        StringBuilder sb = new StringBuilder();
        for (JinjaFilterNode filter : filters) {
            sb.append(filter.accept(this));
        }
        return sb.toString();
    }

    private String renderCondition(String rawCondition, JinjaNode conditionTree) {
        return conditionTree != null ? conditionTree.accept(this) : rawCondition;
    }

    /**
     * Generates {@code operand} and wraps it in parentheses if its own
     * operator binds more loosely than {@code minimumPrecedence} requires —
     * the Jinja-expression-tree counterpart of the same technique used by
     * {@link PythonGenerator#renderOperand}; see that method's Javadoc for
     * why this narrow use of node-type inspection is not in tension with
     * accept()-driven generation.
     */
    private String renderJinjaOperand(JinjaNode operand, int minimumPrecedence) {
        String text = operand.accept(this);
        if (jinjaOperandPrecedence(operand) < minimumPrecedence) {
            return "(" + text + ")";
        }
        return text;
    }

    private int jinjaOperandPrecedence(JinjaNode node) {
        if (node instanceof JinjaBinaryOpNode) {
            return jinjaOperatorPrecedence(((JinjaBinaryOpNode) node).getOperator());
        }
        if (node instanceof JinjaCompareNode) {
            return COMPARE_PRECEDENCE;
        }
        if (node instanceof JinjaUnaryOpNode) {
            return "not".equals(((JinjaUnaryOpNode) node).getOperator()) ? NOT_PRECEDENCE : UNARY_MINUS_PRECEDENCE;
        }
        return ATOM_PRECEDENCE;
    }

    private int jinjaOperatorPrecedence(String operator) {
        switch (operator) {
            case "or":
                return OR_PRECEDENCE;
            case "and":
                return AND_PRECEDENCE;
            case "+":
            case "-":
            case "~":
                return ADDITIVE_PRECEDENCE;
            case "*":
            case "/":
                return MULTIPLICATIVE_PRECEDENCE;
            default:
                return MULTIPLICATIVE_PRECEDENCE;
        }
    }
}
