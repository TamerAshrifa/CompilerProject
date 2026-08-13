package template.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.ast.html.HtmlNode;
import template.ast.jinja.JinjaNode;
import template.visitor.TemplateVisitor;

/**
 * Root node for a complete template document.
 * Contains both HTML structure and Jinja2 logic as separate, independent ASTs.
 */
public class TemplateProgramNode extends TemplateNode {

    private final List<HtmlNode> htmlElements;
    private final List<JinjaNode> jinjaElements;
    private final List<Object> allElements;

    public TemplateProgramNode() {
        super(1, 1);
        this.htmlElements = new ArrayList<>();
        this.jinjaElements = new ArrayList<>();
        this.allElements = new ArrayList<>();
    }

    public TemplateProgramNode(List<?> elements) {
        this(elements, 1, 1);
    }

    public TemplateProgramNode(List<?> elements, int line, int column) {
        super(line, column);
        this.allElements = new ArrayList<>(elements);
        this.htmlElements = new ArrayList<>();
        this.jinjaElements = new ArrayList<>();

        for (Object elem : elements) {
            if (elem instanceof HtmlNode htmlNode) {
                this.htmlElements.add(htmlNode);
            } else if (elem instanceof JinjaNode jinjaNode) {
                this.jinjaElements.add(jinjaNode);
            }
        }
    }

    public void addHtmlElement(HtmlNode element) {
        htmlElements.add(element);
        allElements.add(element);
    }

    public void addJinjaElement(JinjaNode element) {
        jinjaElements.add(element);
        allElements.add(element);
    }

    public void addElement(Object element) {
        if (element instanceof HtmlNode htmlNode) {
            addHtmlElement(htmlNode);
        } else if (element instanceof JinjaNode jinjaNode) {
            addJinjaElement(jinjaNode);
        } else {
            allElements.add(element);
        }
    }

    public List<HtmlNode> getHtmlElements() {
        return Collections.unmodifiableList(htmlElements);
    }

    public List<JinjaNode> getJinjaElements() {
        return Collections.unmodifiableList(jinjaElements);
    }

    public List<Object> getAllElements() {
        return Collections.unmodifiableList(allElements);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}