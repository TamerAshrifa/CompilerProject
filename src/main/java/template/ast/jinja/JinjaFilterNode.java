package template.ast.jinja;

import template.visitor.TemplateVisitor;

/**
 * Jinja2 filter node: {{ variable|filter_name(args) }}
 * Represents a filter that transforms a value in a Jinja2 expression.
 */
public class JinjaFilterNode extends JinjaNode {

    private final String filterName;
    private final String arguments;

    public JinjaFilterNode(String filterName, int line, int column) {
        super(line, column);
        this.filterName = filterName;
        this.arguments = null;
    }

    public JinjaFilterNode(String filterName, String arguments, int line, int column) {
        super(line, column);
        this.filterName = filterName;
        this.arguments = arguments;
    }

    public String getFilterName() {
        return filterName;
    }

    public boolean hasArguments() {
        return arguments != null && !arguments.isEmpty();
    }

    public String getArguments() {
        return arguments;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaFilter(this);
    }
}
