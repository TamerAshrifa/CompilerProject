package template.ast.html;

import template.ast.css.CssStylesheetNode;
import template.visitor.TemplateVisitor;

/**
 * A &lt;style&gt; element. Bridges the independent CSS AST (template.ast.css)
 * into the HTML tree at the point a &lt;style&gt; tag actually appears,
 * instead of collapsing its content into plain HtmlTextNode text.
 */
public class StyleElementNode extends HtmlNode {
    private final CssStylesheetNode stylesheet;

    public StyleElementNode(CssStylesheetNode stylesheet, int line, int column) {
        super(line, column);
        this.stylesheet = stylesheet;
    }

    public CssStylesheetNode getStylesheet() {
        return stylesheet;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitStyleElement(this);
    }
}
