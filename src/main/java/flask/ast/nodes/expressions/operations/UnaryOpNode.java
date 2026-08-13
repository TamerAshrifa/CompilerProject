package flask.ast.nodes.expressions.operations;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

public class UnaryOpNode extends Expression {

    private final String operator;
    private final Expression operand;

    public UnaryOpNode(String operator, Expression operand, int line, int column) {
        super(line, column);
        this.operator = operator;
        this.operand = operand;
    }

    public String getOperator() { return operator; }
    public Expression getOperand() { return operand; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitUnaryOp(this);
    }

    /** Prints the operator followed by its operand, e.g. {@code -x} or {@code not x}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.leaf(ind, last, "Operator", operator),
                (ind, last) -> TreePrinter.child(ind, last, "Operand", operand));
    }
}