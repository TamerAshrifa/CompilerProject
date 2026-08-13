package flask.ast.nodes.expressions.access;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.TreePrinter;

public class AttributeAccessNode extends Expression {

    private final Expression target;
    private final String attribute;

    public AttributeAccessNode(Expression target, String attribute, int line, int column) {
        super(line, column);
        this.target = target;
        this.attribute = attribute;
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public AttributeAccessNode(Expression target, String attribute) {
        this(target, attribute, 0, 0);
    }

    public Expression getTarget() {
        return target;
    }

    public Expression getObject() {
        return target;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAttributeAccess(this);
    }

    /** Prints the accessed object followed by the attribute name, e.g. {@code obj.attr}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Target", target),
                (ind, last) -> TreePrinter.leaf(ind, last, "Attribute", attribute));
    }
}