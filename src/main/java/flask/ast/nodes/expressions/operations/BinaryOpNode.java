package flask.ast.nodes.expressions.operations;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

public class BinaryOpNode extends Expression {

    private final Expression left;
    private final String operator;
    private final Expression right;

    public BinaryOpNode(Expression left, String operator, Expression right, int line, int column) {
        super(line, column);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() { return left; }
    public String getOperator() { return operator; }
    public Expression getRight() { return right; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryOp(this);
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