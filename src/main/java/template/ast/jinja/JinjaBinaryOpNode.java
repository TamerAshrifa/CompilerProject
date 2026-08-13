package template.ast.jinja;

import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * A binary operation inside a Jinja2 expression tree: {@code left op right}.
 *
 * Covers the logical operators ({@code or}, {@code and}), the additive
 * operators ({@code +}, {@code -} and Jinja2's string-concatenation {@code
 * ~}) and the multiplicative operators ({@code *}, {@code /}) - i.e.
 * everything except comparisons, which get their own {@link
 * JinjaCompareNode} to mirror the grammar's (and Python side's) distinction
 * between arithmetic/logical operators and comparisons.
 */
public class JinjaBinaryOpNode extends JinjaNode {

    private final JinjaNode left;
    private final String operator;
    private final JinjaNode right;

    public JinjaBinaryOpNode(JinjaNode left, String operator, JinjaNode right, int line, int column) {
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
        return visitor.visitJinjaBinaryOp(this);
    }

    /** Prints the left operand, the operator, and the right operand, e.g. {@code left + right}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Left", left),
                (ind, last) -> TreePrinter.leaf(ind, last, "Operator", operator),
                (ind, last) -> TreePrinter.child(ind, last, "Right", right));
    }
}
