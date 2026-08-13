package flask.ast.nodes.helpers;

import flask.ast.nodes.Expression;

public class WithItem {

    private final Expression contextExpr;
    private final Expression asName;

    public WithItem(Expression contextExpr, Expression asName) {
        this.contextExpr = contextExpr;
        this.asName = asName;
    }

    public Expression getContextExpr() {
        return contextExpr;
    }

    public boolean hasAsName() {
        return asName != null;
    }

    public Expression getAsName() {
        return asName;
    }
}