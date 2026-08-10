package flask.ast.nodes.expressions.atoms;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

public class IdentifierNode extends Expression {

    private final String name;

    public IdentifierNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public IdentifierNode(String name) {
        this(name, 0, 0);
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIdentifier(this);
    }

    /** Prints this identifier's name. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.leaf(TreePrinter.continuation(indent), true, "Name", name);
    }
}