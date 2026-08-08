package template.ast.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/**
 * HTML element node: <tagname attributes>...children...</tagname>
 * Represents an HTML element with its tag name, attributes, and child nodes.
 */
public class HtmlElementNode extends HtmlNode {

    private final String tagName;
    private final List<HtmlAttributeNode> attributes;
    private final List<HtmlNode> children;
    private final boolean selfClosing;

    public HtmlElementNode(String tagName, List<HtmlAttributeNode> attributes, List<HtmlNode> children, int line, int column) {
        super(line, column);
        this.tagName = tagName;
        this.attributes = new ArrayList<>(attributes);
        this.children = new ArrayList<>(children);
        this.selfClosing = false;
    }

    public HtmlElementNode(
        String tagName,
        List<HtmlAttributeNode> attributes,
        List<HtmlNode> children,
        boolean selfClosing,
        int line,
        int column
    ) {
        super(line, column);
        this.tagName = tagName;
        this.attributes = new ArrayList<>(attributes);
        this.children = new ArrayList<>(children);
        this.selfClosing = selfClosing;
    }

    public String getTagName() {
        return tagName;
    }

    public List<HtmlAttributeNode> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public List<HtmlNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean isSelfClosing() {
        return selfClosing;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitHtmlElement(this);
    }
}
