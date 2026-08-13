package template.ast;

import template.visitor.TemplateVisitor;

public class FilterNode extends JinjaFilter {

    private final String filterName;
    private final String arguments;

    public FilterNode(String filterName, String arguments) {
        super(filterName, java.util.List.of());
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
        return visitor.visitFilter(this);
    }
}