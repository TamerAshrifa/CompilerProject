package template.ast.css;

import template.visitor.TemplateVisitor;

/**
 * Base class for the CSS AST (content of a &lt;style&gt; block). Kept
 * independent from the HTML and Jinja2 node hierarchies, matching how this
 * project keeps each language's tree self-contained; a single
 * StyleElementNode (in template.ast.html) bridges it into the HTML tree at
 * the point a &lt;style&gt; tag actually appears.
 */
public abstract class CssNode {
    private final int line;
    private final int column;

    protected CssNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getNodeName() {
        return getClass().getSimpleName();
    }

    public abstract <T> T accept(TemplateVisitor<T> visitor);
}
