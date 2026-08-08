package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;

public class ExpressionStatementNode extends Statement {

    private final Expression expression;

    public ExpressionStatementNode(Expression expression, int line, int column) {
        super(line, column);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }
}