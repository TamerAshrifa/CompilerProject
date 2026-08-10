package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForStatementNode extends Statement {

    private final Expression target;
    private final Expression iterable;
    private final List<Statement> body;
    private final List<Statement> elseBody;

    public ForStatementNode(Expression target, Expression iterable, List<Statement> body, List<Statement> elseBody,
                             int line, int column) {
        super(line, column);
        this.target = target;
        this.iterable = iterable;
        this.body = new ArrayList<>(body);
        this.elseBody = new ArrayList<>(elseBody);
    }

    public Expression getTarget() { return target; }
    public Expression getIterable() { return iterable; }
    public List<Statement> getBody() { return Collections.unmodifiableList(body); }
    public boolean hasElse() { return !elseBody.isEmpty(); }
    public List<Statement> getElseBody() { return Collections.unmodifiableList(elseBody); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitForStatement(this);
    }

    /** Prints the loop target, iterable, body, and - when present - the else body. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        List<TreePrinter.Field> fields = new ArrayList<>();
        fields.add((ind, last) -> TreePrinter.child(ind, last, "Target", target));
        fields.add((ind, last) -> TreePrinter.child(ind, last, "Iterable", iterable));
        fields.add((ind, last) -> TreePrinter.children(ind, last, "Body", body));
        if (hasElse()) {
            fields.add((ind, last) -> TreePrinter.children(ind, last, "ElseBody", elseBody));
        }
        TreePrinter.fields(TreePrinter.continuation(indent), fields);
    }
}