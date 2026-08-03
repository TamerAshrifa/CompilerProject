package template.ast.jinja;

import template.visitor.TemplateVisitor;

/**
 * Jinja2 comment node: {# comment text #}
 * Represents a comment that is not rendered in the output.
 */
public class JinjaCommentNode extends JinjaNode {

    private final String content;

    public JinjaCommentNode(String content, int line, int column) {
        super(line, column);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaComment(this);
    }
}
