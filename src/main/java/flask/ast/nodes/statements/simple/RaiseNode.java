package flask.ast.nodes.statements.simple;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Prints the raised exception and, when present, its {@code from} cause.
     * A bare {@code raise} (re-raising the current exception) has neither, so
     * it prints with no fields at all.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        List<TreePrinter.Field> fields = new ArrayList<>();
        if (!isBareRaise()) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "Exception", exception));
        }
        if (hasCause()) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "Cause", cause));
        }
        TreePrinter.fields(TreePrinter.continuation(indent), fields);
    }
}