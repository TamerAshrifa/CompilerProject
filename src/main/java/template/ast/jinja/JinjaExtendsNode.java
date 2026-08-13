package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Jinja2 extends node: {% extends "base.html" %}
 * Marks this template as a child of a parent template.
 * Should appear at the beginning of the template.
 */
public class JinjaExtendsNode extends JinjaNode {

    private final String parentTemplatePath;
    private final List<JinjaNode> children;

    public JinjaExtendsNode(String parentTemplatePath, int line, int column) {
        super(line, column);
        this.parentTemplatePath = parentTemplatePath;
        this.children = new ArrayList<>();
    }

    public JinjaExtendsNode(String parentTemplatePath, List<JinjaNode> children, int line, int column) {
        super(line, column);
        this.parentTemplatePath = parentTemplatePath;
        this.children = new ArrayList<>(children);
    }

    public String getParentTemplatePath() {
        return parentTemplatePath;
    }

    public List<JinjaNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaExtends(this);
    }

    /** Prints the parent template path and any accompanying child nodes, e.g. {@code {% extends "base.html" %} }. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.leaf(ind, last, "ParentTemplatePath", parentTemplatePath),
                (ind, last) -> TreePrinter.children(ind, last, "Children", children));
    }
}
