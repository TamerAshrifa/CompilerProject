package template.ast.html;

import template.visitor.TemplateVisitor;

/**
 * Base class for all HTML AST nodes.
 * Completely independent from Jinja2 nodes.
 * 
 * This hierarchy represents HTML language constructs:
 * - Elements: <div>, <p>, <span>, etc.
 * - Attributes: id, class, style, etc.
 * - Text content: raw text between tags
 * - Comments: <!-- comment -->
 */
public abstract class HtmlNode {

    private final int line;
    private final int column;

    protected HtmlNode() {
        this(0, 0);
    }

    protected HtmlNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Returns the name identifying this node (e.g. "HtmlElementNode",
     * "HtmlTextNode"). Resolved automatically from the node's runtime class,
     * mirroring {@code flask.ast.nodes.ASTNode#getNodeName()},
     * {@code template.ast.css.CssNode#getNodeName()} and
     * {@code template.ast.jinja.JinjaNode#getNodeName()} so every AST
     * hierarchy in this project exposes node identity consistently.
     */
    public String getNodeName() {
        return getClass().getSimpleName();
    }

    /**
     * Accept a visitor for this node.
     * Enables visitor pattern for semantic analysis.
     */
    public abstract <T> T accept(TemplateVisitor<T> visitor);
}
