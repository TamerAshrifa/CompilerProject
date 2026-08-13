package flask.ast.nodes.helpers;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExceptClause {

    private final Expression exceptionType;
    private final String name;
    private final List<Statement> body;

    public ExceptClause(Expression exceptionType, String name, List<Statement> body) {
        this.exceptionType = exceptionType;
        this.name = name;
        this.body = new ArrayList<>(body);
    }

    public boolean isBareExcept() {
        return exceptionType == null;
    }

    public Expression getExceptionType() {
        return exceptionType;
    }

    public String getName() {
        return name;
    }

    public List<Statement> getBody() {
        return Collections.unmodifiableList(body);
    }
}