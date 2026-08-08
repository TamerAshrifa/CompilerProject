package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;

public class RaiseNode extends Statement {

    private final Expression exception;
    private final Expression cause;

    public RaiseNode(Expression exception, Expression cause, int line, int column) {
        super(line, column);
        this.exception = exception;
        this.cause = cause;
    }

    public boolean isBareRaise() {
        return exception == null;
    }

    public Expression getException() {
        return exception;
    }

    public boolean hasCause() {
        return cause != null;
    }

    public Expression getCause() {
        return cause;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitRaise(this);
    }
}