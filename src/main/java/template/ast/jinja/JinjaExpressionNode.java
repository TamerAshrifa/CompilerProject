package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Jinja2 expression node.
 * Represents a generic Jinja2 expression that is not a simple variable reference.
 * Examples: {{ 5 + 3 }}, {{ func() }}, {{ obj.attr }}
 */
public class JinjaExpressionNode extends JinjaNode {

    private final String expression;
    private final List<JinjaFilterNode> filters;
    private final JinjaNode root;

    public JinjaExpressionNode(String expression, int line, int column) {
        super(line, column);
        this.expression = expression;
        this.filters = new ArrayList<>();
        this.root = null;
    }

    public JinjaExpressionNode(String expression, List<JinjaFilterNode> filters, int line, int column) {
        super(line, column);
        this.expression = expression;
        this.filters = new ArrayList<>(filters);
        this.root = null;
    }

    /**
     * @param root the structured expression tree parsed from the same
     *             source text as {@code expression} (see {@link #getRoot()}),
     *             or {@code null} if none was built (e.g. hand-built test ASTs).
     */
    public JinjaExpressionNode(String expression, JinjaNode root, int line, int column) {
        super(line, column);
        this.expression = expression;
        this.filters = new ArrayList<>();
        this.root = root;
    }

    public JinjaExpressionNode(String expression, JinjaNode root, List<JinjaFilterNode> filters, int line, int column) {
        super(line, column);
        this.expression = expression;
        this.filters = new ArrayList<>(filters);
        this.root = root;
    }

    public String getExpression() {
        return expression;
    }

    public List<JinjaFilterNode> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    public void addFilter(JinjaFilterNode filter) {
        this.filters.add(filter);
    }

    /**
     * The structured expression tree (real nested nodes: binary/unary ops,
     * comparisons, attribute/subscript access, filter applications, etc.)
     * parsed from the same source text as {@link #getExpression()}, or
     * {@code null} if this node was built without one (e.g. by hand in a
     * test rather than by {@code TemplateASTBuilder}). Prefer this over
     * re-parsing {@link #getExpression()} whenever it is available.
     */
    public JinjaNode getRoot() {
        return root;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaExpression(this);
    }

    /**
     * Prints the raw expression source text, the structured expression tree
     * (when one was built - see {@link #getRoot()}), and any filters applied
     * to the expression as a whole.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.leaf(ind, last, "Expression", expression),
                (ind, last) -> TreePrinter.child(ind, last, "Root", root),
                (ind, last) -> TreePrinter.children(ind, last, "Filters", filters));
    }
}
