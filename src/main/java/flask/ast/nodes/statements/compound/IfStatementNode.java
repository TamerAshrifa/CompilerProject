package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.Printable;
import printer.TreePrinter;

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

    /**
     * Prints the if-condition, the then-body, every {@code elif} clause
     * (each with its own condition and body), and - when present - the
     * final {@code else} body.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        List<TreePrinter.Field> fields = new ArrayList<>();
        fields.add((ind, last) -> TreePrinter.child(ind, last, "Condition", condition));
        fields.add((ind, last) -> TreePrinter.children(ind, last, "ThenBody", thenBody));
        fields.add((ind, last) -> TreePrinter.children(ind, last, "ElifClauses", wrapElifClauses(elifClauses)));
        if (hasElse()) {
            fields.add((ind, last) -> TreePrinter.children(ind, last, "ElseBody", elseBody));
        }
        TreePrinter.fields(TreePrinter.continuation(indent), fields);
    }

    /** Adapts each {@link ElifClause} into a {@link Printable} so it can be handed to {@link TreePrinter#children}. */
    private static List<Printable> wrapElifClauses(List<ElifClause> clauses) {
        List<Printable> wrapped = new ArrayList<>();
        for (ElifClause clause : clauses) {
            wrapped.add(indent -> {
                System.out.println(indent + "ElifClause");
                String base = TreePrinter.continuation(indent);
                TreePrinter.fields(base,
                        (ind, last) -> TreePrinter.child(ind, last, "Condition", clause.getCondition()),
                        (ind, last) -> TreePrinter.children(ind, last, "Body", clause.getBody()));
            });
        }
        return wrapped;
    }
}