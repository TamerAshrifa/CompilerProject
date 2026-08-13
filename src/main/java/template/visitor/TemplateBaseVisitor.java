package template.visitor;

// HTML node imports
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
import template.ast.css.CssRuleNode;
import template.ast.css.CssStyleRuleNode;
import template.ast.css.CssStylesheetNode;
import template.ast.html.HtmlAttributeNode;
import template.ast.html.HtmlCommentNode;
import template.ast.html.HtmlElementNode;
import template.ast.html.HtmlNode;
import template.ast.html.HtmlTextNode;
<<<<<<< HEAD
import template.ast.html.JinjaHostNode;
=======
>>>>>>> 4fabbeaaabb9951a448de85aab6f329d73690904
import template.ast.html.StyleElementNode;

// Jinja2 node imports
import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaCommentNode;
import template.ast.jinja.JinjaElifNode;
import template.ast.jinja.JinjaElseNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaExtendsNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaForNode;
<<<<<<< HEAD
import template.ast.jinja.JinjaHtmlRefNode;
=======
>>>>>>> 4fabbeaaabb9951a448de85aab6f329d73690904
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaIncludeNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaProgramNode;
import template.ast.jinja.JinjaVariableNode;
import template.ast.jinja.LiteralNode;

// Jinja2 structured-expression-tree node imports (previously missing --
// caused 24 "cannot find symbol" compile errors, since TemplateBaseVisitor
// referenced these types below without importing them)
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaBinaryOpNode;
import template.ast.jinja.JinjaCompareNode;
import template.ast.jinja.JinjaUnaryOpNode;
import template.ast.jinja.JinjaAttributeAccessNode;
import template.ast.jinja.JinjaSubscriptNode;
import template.ast.jinja.JinjaCallNode;
import template.ast.jinja.JinjaFilterApplicationNode;


/**
 * Base visitor for Template AST with default implementations.
 * 
 * Each method provides a default visit pattern: visit all children
 * and return a default value. Subclasses can override specific visit
 * methods to implement custom behavior.
 * 
 * Implements the visitor pattern for the complete AST hierarchy:
 * - HTML nodes (HtmlElementNode, HtmlAttributeNode, HtmlTextNode, HtmlCommentNode)
 * - Jinja2 nodes (variable, expression, filter, control flow, inheritance)
 * - Program root (TemplateProgramNode)
 */
public class TemplateBaseVisitor<T> implements TemplateVisitor<T> {

    /**
     * Default visit method for unknown object types.
     * Returns null by default.
     */
    public T visit(Object obj) {
        if (obj instanceof TemplateProgramNode) {
            return visitProgram((TemplateProgramNode) obj);
        }
        if (obj instanceof HtmlElementNode) {
            return visitHtmlElement((HtmlElementNode) obj);
        }
        if (obj instanceof HtmlAttributeNode) {
            return visitHtmlAttribute((HtmlAttributeNode) obj);
        }
        if (obj instanceof HtmlTextNode) {
            return visitHtmlText((HtmlTextNode) obj);
        }
        if (obj instanceof HtmlCommentNode) {
            return visitHtmlComment((HtmlCommentNode) obj);
        }
        if (obj instanceof StyleElementNode) {
            return visitStyleElement((StyleElementNode) obj);
        }
        if (obj instanceof CssStylesheetNode) {
            return visitCssStylesheet((CssStylesheetNode) obj);
        }
        if (obj instanceof CssStyleRuleNode) {
            return visitCssStyleRule((CssStyleRuleNode) obj);
        }
        if (obj instanceof CssAtRuleNode) {
            return visitCssAtRule((CssAtRuleNode) obj);
        }
        if (obj instanceof CssDeclarationNode) {
            return visitCssDeclaration((CssDeclarationNode) obj);
        }
        if (obj instanceof JinjaProgramNode) {
            return visitJinjaProgram((JinjaProgramNode) obj);
        }
        if (obj instanceof JinjaVariableNode) {
            return visitJinjaVariable((JinjaVariableNode) obj);
        }
        if (obj instanceof JinjaExpressionNode) {
            return visitJinjaExpression((JinjaExpressionNode) obj);
        }
        if (obj instanceof JinjaFilterNode) {
            return visitJinjaFilter((JinjaFilterNode) obj);
        }
        if (obj instanceof JinjaIdentifierNode) {
            return visitJinjaIdentifier((JinjaIdentifierNode) obj);
        }
        if (obj instanceof JinjaBinaryOpNode) {
            return visitJinjaBinaryOp((JinjaBinaryOpNode) obj);
        }
        if (obj instanceof JinjaCompareNode) {
            return visitJinjaCompare((JinjaCompareNode) obj);
        }
        if (obj instanceof JinjaUnaryOpNode) {
            return visitJinjaUnaryOp((JinjaUnaryOpNode) obj);
        }
        if (obj instanceof JinjaAttributeAccessNode) {
            return visitJinjaAttributeAccess((JinjaAttributeAccessNode) obj);
        }
        if (obj instanceof JinjaSubscriptNode) {
            return visitJinjaSubscript((JinjaSubscriptNode) obj);
        }
        if (obj instanceof JinjaCallNode) {
            return visitJinjaCall((JinjaCallNode) obj);
        }
        if (obj instanceof JinjaFilterApplicationNode) {
            return visitJinjaFilterApplication((JinjaFilterApplicationNode) obj);
        }
        if (obj instanceof LiteralNode) {
            return visitJinjaLiteral((LiteralNode) obj);
        }
        if (obj instanceof JinjaIfNode) {
            return visitJinjaIf((JinjaIfNode) obj);
        }
        if (obj instanceof JinjaElifNode) {
            return visitJinjaElif((JinjaElifNode) obj);
        }
        if (obj instanceof JinjaElseNode) {
            return visitJinjaElse((JinjaElseNode) obj);
        }
        if (obj instanceof JinjaForNode) {
            return visitJinjaFor((JinjaForNode) obj);
        }
        if (obj instanceof JinjaBlockNode) {
            return visitJinjaBlock((JinjaBlockNode) obj);
        }
        if (obj instanceof JinjaMacroNode) {
            return visitJinjaMacro((JinjaMacroNode) obj);
        }
        if (obj instanceof JinjaCommentNode) {
            return visitJinjaComment((JinjaCommentNode) obj);
        }
        if (obj instanceof JinjaExtendsNode) {
            return visitJinjaExtends((JinjaExtendsNode) obj);
        }
        if (obj instanceof JinjaIncludeNode) {
            return visitJinjaInclude((JinjaIncludeNode) obj);
        }
        return defaultResult();
    }

    // ========================================
    // Root Program Nodes
    // ========================================

    @Override
    public T visitProgram(TemplateProgramNode node) {
        // Visit both HTML and Jinja2 elements
        T aggregate = null;
        
        for (HtmlNode htmlNode : node.getHtmlElements()) {
            aggregate = aggregateResult(aggregate, visit(htmlNode));
        }
        
        for (JinjaNode jinjaNode : node.getJinjaElements()) {
            aggregate = aggregateResult(aggregate, visit(jinjaNode));
        }
        
        return aggregate;
    }

    @Override
    public T visitRule(TemplateRuleNode node) {
        // Generic rule node support (legacy)
        return defaultResult();
    }

    @Override
    public T visitJinjaProgram(JinjaProgramNode node) {
        // Visit all Jinja2 elements within a program
        T aggregate = null;
        
        for (JinjaNode element : node.getElements()) {
            aggregate = aggregateResult(aggregate, visit(element));
        }
        
        return aggregate;
    }

    // ========================================
    // HTML Nodes
    // ========================================

    @Override
    public T visitHtmlElement(HtmlElementNode node) {
        // Visit attributes and children
        T aggregate = null;
        
        for (HtmlAttributeNode attr : node.getAttributes()) {
            aggregate = aggregateResult(aggregate, visit(attr));
        }
        
        for (HtmlNode child : node.getChildren()) {
            aggregate = aggregateResult(aggregate, visit(child));
        }
        
        return aggregate;
    }

    @Override
    public T visitHtmlAttribute(HtmlAttributeNode node) {
        // Attributes are leaf nodes
        return defaultResult();
    }

    @Override
    public T visitHtmlText(HtmlTextNode node) {
        // Text nodes are leaf nodes
        return defaultResult();
    }

    @Override
    public T visitHtmlComment(HtmlCommentNode node) {
        // Comments are leaf nodes
        return defaultResult();
    }

<<<<<<< HEAD
    @Override
    public T visitJinjaHostNode(JinjaHostNode node) {
        // Bare default: no positional resolution/fallback rendering
        // available without more context. HtmlGenerator overrides this to
        // actually resolve and inline the hosted node's content - see
        // template.ast.html.JinjaHostNode for why this node exists.
        return defaultResult();
    }

=======
>>>>>>> 4fabbeaaabb9951a448de85aab6f329d73690904
    // ========================================
    // CSS Nodes
    // ========================================

    @Override
    public T visitStyleElement(StyleElementNode node) {
        return visitCssStylesheet(node.getStylesheet());
    }

    @Override
    public T visitCssStylesheet(CssStylesheetNode node) {
        T aggregate = null;
        for (CssRuleNode rule : node.getRules()) {
            aggregate = aggregateResult(aggregate, visit(rule));
        }
        return aggregate;
    }

    @Override
    public T visitCssStyleRule(CssStyleRuleNode node) {
        T aggregate = null;
        for (CssDeclarationNode declaration : node.getDeclarations()) {
            aggregate = aggregateResult(aggregate, visit(declaration));
        }
        return aggregate;
    }

    @Override
    public T visitCssAtRule(CssAtRuleNode node) {
        T aggregate = null;
        for (CssDeclarationNode declaration : node.getDeclarations()) {
            aggregate = aggregateResult(aggregate, visit(declaration));
        }
        for (CssRuleNode rule : node.getNestedRules()) {
            aggregate = aggregateResult(aggregate, visit(rule));
        }
        return aggregate;
    }

    @Override
    public T visitCssDeclaration(CssDeclarationNode node) {
        // Declarations are leaves here (their value is kept as text).
        return defaultResult();
    }

    // ========================================
    // Jinja2 Variables & Expressions
    // ========================================

    @Override
    public T visitJinjaVariable(JinjaVariableNode node) {
        // Visit filters
        T aggregate = null;
        
        for (JinjaFilterNode filter : node.getFilters()) {
            aggregate = aggregateResult(aggregate, visit(filter));
        }
        
        return aggregate;
    }

    @Override
    public T visitJinjaExpression(JinjaExpressionNode node) {
        // Leaf node unless a structured expression tree was built for it
        // (see JinjaExpressionNode#getRoot()), in which case recurse into it.
        return node.getRoot() != null ? visit(node.getRoot()) : defaultResult();
    }

    @Override
    public T visitJinjaFilter(JinjaFilterNode node) {
        // Filters are leaf nodes
        return defaultResult();
    }

    // ========================================
    // Jinja2 structured expression tree
    // ========================================

    @Override
    public T visitJinjaIdentifier(JinjaIdentifierNode node) {
        // Identifiers are leaf nodes
        return defaultResult();
    }

    @Override
    public T visitJinjaBinaryOp(JinjaBinaryOpNode node) {
        T aggregate = visit(node.getLeft());
        aggregate = aggregateResult(aggregate, visit(node.getRight()));
        return aggregate;
    }

    @Override
    public T visitJinjaCompare(JinjaCompareNode node) {
        T aggregate = visit(node.getLeft());
        aggregate = aggregateResult(aggregate, visit(node.getRight()));
        return aggregate;
    }

    @Override
    public T visitJinjaUnaryOp(JinjaUnaryOpNode node) {
        return visit(node.getOperand());
    }

    @Override
    public T visitJinjaAttributeAccess(JinjaAttributeAccessNode node) {
        return visit(node.getObject());
    }

    @Override
    public T visitJinjaSubscript(JinjaSubscriptNode node) {
        T aggregate = visit(node.getObject());
        aggregate = aggregateResult(aggregate, visit(node.getIndex()));
        return aggregate;
    }

    @Override
    public T visitJinjaCall(JinjaCallNode node) {
        T aggregate = visit(node.getCallee());
        for (JinjaNode argument : node.getArguments()) {
            aggregate = aggregateResult(aggregate, visit(argument));
        }
        return aggregate;
    }

    @Override
    public T visitJinjaFilterApplication(JinjaFilterApplicationNode node) {
        T aggregate = visit(node.getTarget());
        for (JinjaFilterNode filter : node.getFilters()) {
            aggregate = aggregateResult(aggregate, visit(filter));
        }
        return aggregate;
    }

    @Override
    public T visitJinjaLiteral(LiteralNode node) {
        // Literal nodes are leaf nodes (represent concrete values)
        return defaultResult();
    }

    // ========================================
    // Jinja2 Control Flow
    // ========================================

    @Override
    public T visitJinjaIf(JinjaIfNode node) {
        T aggregate = null;

        for (JinjaNode child : node.getThenBody()) {
            aggregate = aggregateResult(aggregate, visit(child));
        }

        for (JinjaElifNode elif : node.getElifNodes()) {
            aggregate = aggregateResult(aggregate, visit(elif));
        }

        if (node.hasElse()) {
            aggregate = aggregateResult(aggregate, visit(node.getElseNode()));
        }

        return aggregate;
    }

    @Override
    public T visitJinjaElif(JinjaElifNode node) {
        T aggregate = null;

        for (JinjaNode child : node.getBody()) {
            aggregate = aggregateResult(aggregate, visit(child));
        }

        return aggregate;
    }

    @Override
    public T visitJinjaElse(JinjaElseNode node) {
        T aggregate = null;

        for (JinjaNode child : node.getBody()) {
            aggregate = aggregateResult(aggregate, visit(child));
        }

        return aggregate;
    }

    @Override
    public T visitJinjaFor(JinjaForNode node) {
        T aggregate = null;

        for (JinjaNode child : node.getBody()) {
            aggregate = aggregateResult(aggregate, visit(child));
        }

        if (node.hasElse()) {
            for (JinjaNode child : node.getElseBody()) {
                aggregate = aggregateResult(aggregate, visit(child));
            }
        }

        return aggregate;
    }

    // ========================================
    // Jinja2 Template Inheritance & Composition
    // ========================================

    @Override
    public T visitJinjaBlock(JinjaBlockNode node) {
        T aggregate = null;

        for (JinjaNode child : node.getBody()) {
            aggregate = aggregateResult(aggregate, visit(child));
        }

        return aggregate;
    }

    @Override
    public T visitJinjaExtends(JinjaExtendsNode node) {
        // Extends is a leaf node (directive only)
        return defaultResult();
    }

    @Override
    public T visitJinjaInclude(JinjaIncludeNode node) {
        // Include is a leaf node (directive only)
        return defaultResult();
    }

    // ========================================
    // Jinja2 Macros & Comments
    // ========================================

    @Override
    public T visitJinjaMacro(JinjaMacroNode node) {
        // Visit macro body
        T aggregate = null;
        
        for (JinjaNode child : node.getBody()) {
            aggregate = aggregateResult(aggregate, visit(child));
        }
        
        return aggregate;
    }

    @Override
    public T visitJinjaComment(JinjaCommentNode node) {
        // Comments are leaf nodes (content is not visited)
        return defaultResult();
    }

<<<<<<< HEAD
    @Override
    public T visitJinjaHtmlRef(JinjaHtmlRefNode node) {
        // Bookkeeping-only marker; see template.ast.jinja.JinjaHtmlRefNode.
        // Generator consumes these via direct instanceof checks while
        // resolving {% if %} bodies (it does not use the visitor dispatch
        // at all - see Generator.transformJinjaNode), and strips them out
        // of every transformed body it produces, so a fully-resolved tree
        // handed to a *_generator should never actually contain one.
        return defaultResult();
    }

=======
>>>>>>> 4fabbeaaabb9951a448de85aab6f329d73690904
    // ========================================
    // Aggregation & Default Results
    // ========================================

    /**
     * Aggregates results from visiting multiple children.
     * Default implementation returns the next result, effectively
     * using the last visited child's result.
     * 
     * Subclasses can override to implement different aggregation strategies.
     */
    protected T aggregateResult(T aggregate, T nextResult) {
        return nextResult != null ? nextResult : aggregate;
    }

    /**
     * Returns the default result when visiting a node or when
     * no children are available.
     * 
     * Default implementation returns null. Subclasses can override
     * to provide different default values (e.g., empty collections).
     */
    protected T defaultResult() {
        return null;
    }

    // ========================================
    // Legacy Compatibility (deprecated methods)
    // ========================================

    @Deprecated
    @Override
    public T visitVariable(VariableNode node) {
        return defaultResult();
    }

    @Deprecated
    @Override
    public T visitFilter(FilterNode node) {
        return defaultResult();
    }

    @Deprecated
    @Override
    public T visitIf(IfNode node) {
        return defaultResult();
    }

    @Deprecated
    @Override
    public T visitFor(ForNode node) {
        return defaultResult();
    }

    @Deprecated
    @Override
    public T visitBlock(BlockNode node) {
        return defaultResult();
    }

    @Deprecated
    @Override
    public T visitExtends(ExtendsNode node) {
        return defaultResult();
    }

    @Deprecated
    @Override
    public T visitInclude(IncludeNode node) {
        return defaultResult();
    }

    @Deprecated
    @Override
    public T visitText(TextNode node) {
        return defaultResult();
    }

    @Deprecated
    @Override
    public T visitExpression(ExpressionNode node) {
        return defaultResult();
    }
}
