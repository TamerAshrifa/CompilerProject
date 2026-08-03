package template.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

public class VariableNode extends JinjaVariable {

    private final String variableName;
    private final List<FilterNode> filters;

    public VariableNode(String variableName, List<FilterNode> filters) {
        super(variableName, List.of());
        this.variableName = variableName;
        this.filters = new ArrayList<>(filters);
    }

    public String getVariableName() {
        return variableName;
    }

    public boolean hasFilters() {
        return !filters.isEmpty();
    }

    public List<FilterNode> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitVariable(this);
    }
}