package template.ast;

import template.visitor.TemplateVisitor;

public class TextNode extends TemplateNode {

    private final String content;

    public TextNode(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitText(this);
    }
}