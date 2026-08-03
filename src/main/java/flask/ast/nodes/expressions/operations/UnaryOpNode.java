package flask.ast.nodes.expressions.operations;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;

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
}