package template.visitor;

import template.ast.BlockNode;
import template.ast.ExpressionNode;
import template.ast.ExtendsNode;
import template.ast.FilterNode;
import template.ast.ForNode;
import template.ast.IfNode;
import template.ast.IncludeNode;
import template.ast.TemplateProgramNode;
import template.ast.TemplateRuleNode;
import template.ast.TextNode;
import template.ast.VariableNode;
import template.ast.css.CssAtRuleNode;
import template.ast.css.CssDeclarationNode;
import template.ast.css.CssStyleRuleNode;
import template.ast.css.CssStylesheetNode;
import template.ast.html.HtmlAttributeNode;
import template.ast.html.HtmlCommentNode;
import template.ast.html.HtmlElementNode;
import template.ast.html.HtmlTextNode;
import template.ast.html.JinjaHostNode;
import template.ast.html.StyleElementNode;
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
import template.ast.jinja.JinjaHtmlRefNode;
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaIncludeNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaProgramNode;
import template.ast.jinja.JinjaSubscriptNode;
import template.ast.jinja.JinjaUnaryOpNode;
import template.ast.jinja.JinjaVariableNode;
import template.ast.jinja.LiteralNode;

/**
 * Visitor interface for Template AST nodes (HTML + Jinja2).
 * Defines visit methods for all template node types.
 * 
 * This visitor supports both independent AST hierarchies:
 * - HTML nodes (template.ast.html.*)
 * - Jinja2 nodes (template.ast.jinja.*)
 */
public interface TemplateVisitor<T> {

    // ========================================
    // Program Root (1 method)
    // ========================================

    /**
     * Visit the root program node (contains HTML + Jinja2)
     */
    T visitProgram(TemplateProgramNode node);

    /**
     * Visit a generic parser-rule node (legacy support)
     */
    T visitRule(TemplateRuleNode node);

    // ========================================
    // HTML Nodes (5 methods)
    // ========================================

    /**
     * Visit HTML element: <tagname ...>...</tagname>
     */
    T visitHtmlElement(HtmlElementNode node);

    /**
     * Visit HTML attribute: name="value"
     */
    T visitHtmlAttribute(HtmlAttributeNode node);

    /**
     * Visit HTML text: raw text content between tags
     */
    T visitHtmlText(HtmlTextNode node);

    /**
     * Visit HTML comment: <!-- comment -->
     */
    T visitHtmlComment(HtmlCommentNode node);

    /**
     * Visit a JinjaHostNode: a marker left in the HTML tree at the exact
     * position a Jinja2 construct was hoisted out of. See {@link
     * template.ast.html.JinjaHostNode} for why this exists.
     */
    T visitJinjaHostNode(JinjaHostNode node);

    // ========================================
    // CSS Nodes (5 methods)
    // ========================================

    /**
     * Visit a <style> element, bridging into the independent CSS AST.
     */
    T visitStyleElement(StyleElementNode node);

    /**
     * Visit the root of a parsed CSS stylesheet.
     */
    T visitCssStylesheet(CssStylesheetNode node);

    /**
     * Visit a qualified CSS rule: selector(s) + declaration block.
     */
    T visitCssStyleRule(CssStyleRuleNode node);

    /**
     * Visit a CSS at-rule, e.g. @media, @import, @font-face.
     */
    T visitCssAtRule(CssAtRuleNode node);

    /**
     * Visit a single CSS declaration, e.g. "color: red;".
     */
    T visitCssDeclaration(CssDeclarationNode node);

    // ========================================
    // Jinja2 Program & Root (1 method)
    // ========================================

    /**
     * Visit Jinja2 program root
     */
    T visitJinjaProgram(JinjaProgramNode node);

    // ========================================
    // Jinja2 Variables & Expressions (3 methods)
    // ========================================

    /**
     * Visit Jinja2 variable: {{ variable }}
     */
    T visitJinjaVariable(JinjaVariableNode node);

    /**
     * Visit Jinja2 expression: {{ expression }}, {{ func() }}
     */
    T visitJinjaExpression(JinjaExpressionNode node);

    /**
     * Visit Jinja2 filter: {{ var|filter(args) }}
     */
    T visitJinjaFilter(JinjaFilterNode node);

    // ========================================
    // Jinja2 structured expression tree (8 methods)
    //
    // Populated for any expression more complex than a plain
    // "name(.attr|[idx])*" chain (see JinjaExpressionNode#getRoot(),
    // JinjaIfNode#getConditionTree(), JinjaForNode#getIterableTree()), so
    // arithmetic/comparisons/attribute-access are real nested nodes instead
    // of only being recoverable from flattened source text.
    // ========================================

    /**
     * Visit a bare identifier inside an expression tree, e.g. "user" in
     * "user.name".
     */
    T visitJinjaIdentifier(JinjaIdentifierNode node);

    /**
     * Visit a binary operation inside an expression tree: or, and, +, -, ~, *, /
     */
    T visitJinjaBinaryOp(JinjaBinaryOpNode node);

    /**
     * Visit a comparison inside an expression tree: ==, !=, <=, >=, <, >
     */
    T visitJinjaCompare(JinjaCompareNode node);

    /**
     * Visit a unary operation inside an expression tree: not, unary -
     */
    T visitJinjaUnaryOp(JinjaUnaryOpNode node);

    /**
     * Visit attribute access inside an expression tree: the ".name" in "user.name"
     */
    T visitJinjaAttributeAccess(JinjaAttributeAccessNode node);

    /**
     * Visit subscript/index access inside an expression tree: the "[0]" in "items[0]"
     */
    T visitJinjaSubscript(JinjaSubscriptNode node);

    /**
     * Visit a direct call trailer inside an expression tree: "items.count()"
     */
    T visitJinjaCall(JinjaCallNode node);

    /**
     * Visit a filter application at any nesting level inside an expression
     * tree: "(a + b)|round"
     */
    T visitJinjaFilterApplication(JinjaFilterApplicationNode node);

    /**
     * Visit Jinja2 literal: A concrete value substituted for a variable.
     * Created by the Generator phase when transforming variables to literals.
     * Example: {{ name }} → LiteralNode("Ali")
     */
    T visitJinjaLiteral(LiteralNode node);

    // ========================================
    // Jinja2 Control Flow (5 methods)
    // ========================================

    /**
     * Visit Jinja2 if statement: {% if condition %}...{% endif %}
     */
    T visitJinjaIf(JinjaIfNode node);

    /**
     * Visit Jinja2 elif clause: {% elif condition %}...
     */
    T visitJinjaElif(JinjaElifNode node);

    /**
     * Visit Jinja2 else clause: {% else %}...
     */
    T visitJinjaElse(JinjaElseNode node);

    /**
     * Visit Jinja2 for loop: {% for var in iterable %}...{% endfor %}
     */
    T visitJinjaFor(JinjaForNode node);

    /**
     * Visit Jinja2 block: {% block name %}...{% endblock %}
     */
    T visitJinjaBlock(JinjaBlockNode node);

    // ========================================
    // Jinja2 Template Inheritance (3 methods)
    // ========================================

    /**
     * Visit Jinja2 extends: {% extends "template.html" %}
     */
    T visitJinjaExtends(JinjaExtendsNode node);

    /**
     * Visit Jinja2 include: {% include "template.html" %}
     */
    T visitJinjaInclude(JinjaIncludeNode node);

    /**
     * Visit Jinja2 macro: {% macro name(args) %}...{% endmacro %}
     */
    T visitJinjaMacro(JinjaMacroNode node);

    // ========================================
    // Jinja2 Comments (1 method)
    // ========================================

    /**
     * Visit Jinja2 comment: {# comment text #}
     */
    T visitJinjaComment(JinjaCommentNode node);

    /**
     * Visit a JinjaHtmlRefNode: a bookkeeping-only marker recording that an
     * HTML node textually appeared inside this {@code {% if %}}/
     * {@code {% elif %}}/{@code {% else %}} branch. See {@link
     * template.ast.jinja.JinjaHtmlRefNode} for why this exists. Never
     * meaningfully rendered - implementations should treat this as a
     * no-op leaf, the same as a comment.
     */
    T visitJinjaHtmlRef(JinjaHtmlRefNode node);

    // ========================================
    // Legacy Compatibility (deprecated)
    // ========================================

    /**
     * @deprecated Use visitJinjaVariable or visitJinjaExpression instead
     */
    @Deprecated
    T visitVariable(VariableNode node);

    /**
     * @deprecated Use visitJinjaFilter instead
     */
    @Deprecated
    T visitFilter(FilterNode node);

    /**
     * @deprecated Use visitJinjaIf instead
     */
    @Deprecated
    T visitIf(IfNode node);

    /**
     * @deprecated Use visitJinjaFor instead
     */
    @Deprecated
    T visitFor(ForNode node);

    /**
     * @deprecated Use visitJinjaBlock instead
     */
    @Deprecated
    T visitBlock(BlockNode node);

    /**
     * @deprecated Use visitJinjaExtends instead
     */
    @Deprecated
    T visitExtends(ExtendsNode node);

    /**
     * @deprecated Use visitJinjaInclude instead
     */
    @Deprecated
    T visitInclude(IncludeNode node);

    /**
     * @deprecated Use visitHtmlText instead
     */
    @Deprecated
    T visitText(TextNode node);

    /**
     * @deprecated Use visitJinjaExpression instead
     */
    @Deprecated
    T visitExpression(ExpressionNode node);
}
