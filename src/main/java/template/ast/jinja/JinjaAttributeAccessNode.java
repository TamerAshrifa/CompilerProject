package template.ast.jinja;

import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Attribute access inside a Jinja2 expression tree, e.g. the {@code .name}
 * in {@code user.name}.
 */
public class JinjaAttributeAccessNode extends JinjaNode {

    private final JinjaNode object;
    private final String attributeName;

    public JinjaAttributeAccessNode(JinjaNode object, String attributeName, int line, int column) {
        super(line, column);
        this.object = object;
        this.attributeName = attributeName;
    }

    public JinjaNode getObject() {
        return object;
    }

    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaAttributeAccess(this);
    }

    /** Prints the accessed object followed by the attribute name, e.g. {@code user.name}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Object", object),
                (ind, last) -> TreePrinter.leaf(ind, last, "AttributeName", attributeName));
    }
}
