package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

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
}
