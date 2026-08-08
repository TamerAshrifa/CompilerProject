package template.ast.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/**
 * HTML comment node: <!-- comment text -->
 * Represents an HTML comment that is not rendered but preserved.
 */
public class HtmlCommentNode extends HtmlNode {

    private final String content;

    public HtmlCommentNode(String content, int line, int column) {
        super(line, column);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitHtmlComment(this);
    }
}
