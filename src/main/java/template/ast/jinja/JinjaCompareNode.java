package template.ast.jinja;

import template.visitor.TemplateVisitor;

/**
 * A comparison inside a Jinja2 expression tree: {@code left op right}, where
 * {@code op} is one of {@code == != <= >= < >}.
 *
 * Unlike Python's {@code CompareNode}, this only ever holds a single
 * operator/right-hand side: Jinja2's grammar does not allow chained
 * comparisons such as {@code a < b < c}.
 */
public class JinjaCompareNode extends JinjaNode {

    private final JinjaNode left;
    private final String operator;
    private final JinjaNode right;

    public JinjaCompareNode(JinjaNode left, String operator, JinjaNode right, int line, int column) {
        super(line, column);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public JinjaNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public JinjaNode getRight() {
        return right;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaCompare(this);
    }
}
