package template.ast.jinja;

import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * A unary operation inside a Jinja2 expression tree: {@code op operand}.
 * Covers {@code not} (from {@code jinjaNotExpr}) and unary minus (from
 * {@code jinjaPrimary}).
 */
public class JinjaUnaryOpNode extends JinjaNode {

    private final String operator;
    private final JinjaNode operand;

    public JinjaUnaryOpNode(String operator, JinjaNode operand, int line, int column) {
        super(line, column);
        this.operator = operator;
        this.operand = operand;
    }

    public String getOperator() {
        return operator;
    }

    public JinjaNode getOperand() {
        return operand;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaUnaryOp(this);
    }

    /** Prints the operator followed by its operand, e.g. {@code not x}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.leaf(ind, last, "Operator", operator),
                (ind, last) -> TreePrinter.child(ind, last, "Operand", operand));
    }
}
