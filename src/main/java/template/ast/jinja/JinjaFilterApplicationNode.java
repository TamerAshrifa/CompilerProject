package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Applies one or more filters to a sub-expression inside a Jinja2 expression
 * tree, e.g. {@code (a + b)|round} or {@code name|upper}.
 *
 * This exists so filters occurring at any nesting level of an expression
 * (not just at the very outermost level, which {@link JinjaVariableNode} and
 * {@link JinjaExpressionNode} already track via their own {@code filters}
 * list for backward compatibility) are represented structurally instead of
 * only being recoverable from the flattened source text.
 */
public class JinjaFilterApplicationNode extends JinjaNode {

    private final JinjaNode target;
    private final List<JinjaFilterNode> filters;

    public JinjaFilterApplicationNode(JinjaNode target, List<JinjaFilterNode> filters, int line, int column) {
        super(line, column);
        this.target = target;
        this.filters = new ArrayList<>(filters);
    }

    public JinjaNode getTarget() {
        return target;
    }

    public List<JinjaFilterNode> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaFilterApplication(this);
    }

    /** Prints the filtered target followed by the chain of filters applied to it, e.g. {@code name|upper|trim}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Target", target),
                (ind, last) -> TreePrinter.children(ind, last, "Filters", filters));
    }
}
