package template.ast.html;

import template.visitor.TemplateVisitor;

/**
 * HTML text node: raw text content between tags.
 * Represents non-tag content like "Hello World" or whitespace.
 */
public class HtmlTextNode extends HtmlNode {

    private final String content;

    public HtmlTextNode(String content, int line, int column) {
        super(line, column);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitHtmlText(this);
    }
}
