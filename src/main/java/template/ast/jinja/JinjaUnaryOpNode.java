package template.ast.jinja;

import template.visitor.TemplateVisitor;

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
}
