package flask.ast.nodes.statements.compound;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.helpers.Decorator;
import flask.ast.nodes.helpers.HelperPrinting;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassDefNode extends Statement {

    private final String name;
    private final List<Decorator> decorators;
    private final List<Expression> bases;
    private final List<Statement> body;

    public ClassDefNode(String name, List<Decorator> decorators, List<Expression> bases, List<Statement> body,
                        int line, int column) {
        super(line, column);
        this.name = name;
        this.decorators = new ArrayList<>(decorators);
        this.bases = new ArrayList<>(bases);
        this.body = new ArrayList<>(body);
    }

    public String getName() { return name; }
    public List<Decorator> getDecorators() { return Collections.unmodifiableList(decorators); }
    public List<Expression> getBases() { return Collections.unmodifiableList(bases); }
    public List<Statement> getBody() { return Collections.unmodifiableList(body); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitClassDef(this);
    }

    /** Prints the class's name, decorators, base classes, and body. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        List<TreePrinter.Field> fields = new ArrayList<>();
        fields.add((ind, last) -> TreePrinter.leaf(ind, last, "Name", name));
        fields.add((ind, last) -> TreePrinter.children(ind, last, "Decorators", HelperPrinting.decorators(decorators)));
        fields.add((ind, last) -> TreePrinter.children(ind, last, "Bases", bases));
        fields.add((ind, last) -> TreePrinter.children(ind, last, "Body", body));
        TreePrinter.fields(base, fields);
    }
}