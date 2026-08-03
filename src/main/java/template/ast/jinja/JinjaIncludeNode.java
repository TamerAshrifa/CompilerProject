package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/**
 * Jinja2 include node: {% include "template.html" %}
 * Includes another template, optionally with context variables.
 */
public class JinjaIncludeNode extends JinjaNode {

    private final String templatePath;
    private final boolean withContext;
    private final List<JinjaNode> children;

    public JinjaIncludeNode(String templatePath, int line, int column) {
        super(line, column);
        this.templatePath = templatePath;
        this.withContext = true;
        this.children = new ArrayList<>();
    }

    public JinjaIncludeNode(String templatePath, boolean withContext, int line, int column) {
        super(line, column);
        this.templatePath = templatePath;
        this.withContext = withContext;
        this.children = new ArrayList<>();
    }

    public JinjaIncludeNode(String templatePath, boolean withContext, List<JinjaNode> children, int line, int column) {
        super(line, column);
        this.templatePath = templatePath;
        this.withContext = withContext;
        this.children = new ArrayList<>(children);
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public boolean isWithContext() {
        return withContext;
    }

    public List<JinjaNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaInclude(this);
    }
}
