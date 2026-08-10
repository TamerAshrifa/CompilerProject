package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Statement;
import flask.ast.nodes.helpers.ExceptClause;
import flask.ast.nodes.helpers.HelperPrinting;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TryStatementNode extends Statement {

    private final List<Statement> tryBody;
    private final List<ExceptClause> exceptClauses;
    private final List<Statement> elseBody;
    private final List<Statement> finallyBody;

    public TryStatementNode(List<Statement> tryBody, List<ExceptClause> exceptClauses, List<Statement> elseBody,
                             List<Statement> finallyBody, int line, int column) {
        super(line, column);
        this.tryBody = new ArrayList<>(tryBody);
        this.exceptClauses = new ArrayList<>(exceptClauses);
        this.elseBody = new ArrayList<>(elseBody);
        this.finallyBody = new ArrayList<>(finallyBody);
    }

    public List<Statement> getTryBody() { return Collections.unmodifiableList(tryBody); }
    public List<ExceptClause> getExceptClauses() { return Collections.unmodifiableList(exceptClauses); }
    public boolean hasElse() { return !elseBody.isEmpty(); }
    public List<Statement> getElseBody() { return Collections.unmodifiableList(elseBody); }
    public boolean hasFinally() { return !finallyBody.isEmpty(); }
    public List<Statement> getFinallyBody() { return Collections.unmodifiableList(finallyBody); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTryStatement(this);
    }

    /**
     * Prints the try-body, every except-clause, and - when present - the
     * else and finally bodies.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        List<TreePrinter.Field> fields = new ArrayList<>();
        fields.add((ind, last) -> TreePrinter.children(ind, last, "TryBody", tryBody));
        fields.add((ind, last) -> TreePrinter.children(ind, last, "ExceptClauses", HelperPrinting.exceptClauses(exceptClauses)));
        if (hasElse()) {
            fields.add((ind, last) -> TreePrinter.children(ind, last, "ElseBody", elseBody));
        }
        if (hasFinally()) {
            fields.add((ind, last) -> TreePrinter.children(ind, last, "FinallyBody", finallyBody));
        }
        TreePrinter.fields(TreePrinter.continuation(indent), fields);
    }
}