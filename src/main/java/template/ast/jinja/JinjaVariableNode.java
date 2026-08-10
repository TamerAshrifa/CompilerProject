package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Jinja2 variable node: {{ variable }}
 * Represents a variable reference that will be replaced with its value.
 */
public class JinjaVariableNode extends JinjaNode {

    private final String variableName;
    private final List<JinjaFilterNode> filters;

    public JinjaVariableNode(String variableName, int line, int column) {
        super(line, column);
        this.variableName = variableName;
        this.filters = new ArrayList<>();
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public JinjaVariableNode(String variableName) {
        this(variableName, 0, 0);
    }

    public JinjaVariableNode(String variableName, List<JinjaFilterNode> filters, int line, int column) {
        super(line, column);
        this.variableName = variableName;
        this.filters = new ArrayList<>(filters);
    }

    public String getVariableName() {
        return variableName;
    }

    public List<JinjaFilterNode> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    public void addFilter(JinjaFilterNode filter) {
        this.filters.add(filter);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaVariable(this);
    }

    /** Prints the referenced variable name and any filters applied to it, e.g. {@code {{ name|upper }} }. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.leaf(ind, last, "VariableName", variableName),
                (ind, last) -> TreePrinter.children(ind, last, "Filters", filters));
    }
}
