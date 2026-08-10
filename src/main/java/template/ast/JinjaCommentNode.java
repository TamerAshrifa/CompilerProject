package template.ast;

import template.visitor.TemplateVisitor;

public class JinjaCommentNode extends JinjaComment {

    private final String content;

    public JinjaCommentNode(String content) {
        super(content);
        this.content = content;
    }

    public String getContent() { return content; }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitRule(this);
    }
}