package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IfStatementNode extends Statement {

    public static class ElifClause {
        private final Expression condition;
        private final List<Statement> body;

        public ElifClause(Expression condition, List<Statement> body) {
            this.condition = condition;
            this.body = new ArrayList<>(body);
        }

        public Expression getCondition() { return condition; }
        public List<Statement> getBody() { return Collections.unmodifiableList(body); }
    }

    private final Expression condition;
    private final List<Statement> thenBody;
    private final List<ElifClause> elifClauses;
    private final List<Statement> elseBody;

    public IfStatementNode(Expression condition, List<Statement> thenBody, List<ElifClause> elifClauses,
                            List<Statement> elseBody, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.thenBody = new ArrayList<>(thenBody);
        this.elifClauses = new ArrayList<>(elifClauses);
        this.elseBody = new ArrayList<>(elseBody);
    }

    public Expression getCondition() { return condition; }
    public List<Statement> getThenBody() { return Collections.unmodifiableList(thenBody); }
    public List<ElifClause> getElifClauses() { return Collections.unmodifiableList(elifClauses); }
    public boolean hasElse() { return !elseBody.isEmpty(); }
    public List<Statement> getElseBody() { return Collections.unmodifiableList(elseBody); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIfStatement(this);
    }
}