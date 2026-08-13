package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Statement;
import flask.ast.nodes.helpers.HelperPrinting;
import flask.ast.nodes.helpers.WithItem;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WithStatementNode extends Statement {

    private final List<WithItem> items;
    private final List<Statement> body;

    public WithStatementNode(List<WithItem> items, List<Statement> body, int line, int column) {
        super(line, column);
        this.items = new ArrayList<>(items);
        this.body = new ArrayList<>(body);
    }

    public List<WithItem> getItems() { return Collections.unmodifiableList(items); }
    public List<Statement> getBody() { return Collections.unmodifiableList(body); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitWithStatement(this);
    }

    /** Prints the with-items (context expression + optional "as" target) and the body. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.children(ind, last, "Items", HelperPrinting.withItems(items)),
                (ind, last) -> TreePrinter.children(ind, last, "Body", body));
    }
}