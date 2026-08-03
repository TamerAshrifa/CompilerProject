package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhileStatementNode extends Statement {

    private final Expression condition;
    private final List<Statement> body;
    private final List<Statement> elseBody;

    public WhileStatementNode(Expression condition, List<Statement> body, List<Statement> elseBody,
                               int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = new ArrayList<>(body);
        this.elseBody = new ArrayList<>(elseBody);
    }

    public Expression getCondition() { return condition; }
    public List<Statement> getBody() { return Collections.unmodifiableList(body); }
    public boolean hasElse() { return !elseBody.isEmpty(); }
    public List<Statement> getElseBody() { return Collections.unmodifiableList(elseBody); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitWhileStatement(this);
    }
}