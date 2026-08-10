package template.ast;

import java.util.List;
import template.visitor.TemplateVisitor;

public class HtmlElementNode extends HtmlElement {

    private final String tagName;
    private final List<HtmlAttributeNode> attributes;

    public HtmlElementNode(String tagName, List<HtmlAttributeNode> attributes, List<TemplateNode> children) {
        super(tagName, children);
        this.tagName = tagName;
        this.attributes = new java.util.ArrayList<>(attributes);
    }

    public String getTagName() {
        return tagName;
    }

    public List<HtmlAttributeNode> getAttributes() {
        return java.util.Collections.unmodifiableList(attributes);
    }

    public List<TemplateNode> getChildren() {
        return getRuleChildren();
    }

    private List<TemplateNode> getRuleChildren() {
        return super.getChildren();
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitRule(this);
    }
}