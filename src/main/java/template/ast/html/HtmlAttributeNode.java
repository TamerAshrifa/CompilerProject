package template.ast.html;

import template.visitor.TemplateVisitor;

/**
 * HTML attribute node: name="value"
 * Represents an attribute on an HTML element.
 */
public class HtmlAttributeNode extends HtmlNode {

    private final String name;
    private final String value;
    private final boolean hasValue;

    public HtmlAttributeNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
        this.value = null;
        this.hasValue = false;
    }

    public HtmlAttributeNode(String name, String value, int line, int column) {
        super(line, column);
        this.name = name;
        this.value = value;
        this.hasValue = true;
    }

    public String getName() {
        return name;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public String getValue() {
        return value;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitHtmlAttribute(this);
    }
}
