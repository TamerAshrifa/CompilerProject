package template.ast;

import template.visitor.TemplateVisitor;

public class HtmlAttributeNode extends HtmlAttribute {

    private final String name;
    private final String value;

    public HtmlAttributeNode(String name, String value) {
        super(name, value);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitRule(this);
    }
}