package template.ast.jinja;

import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Index/subscript access inside a Jinja2 expression tree, e.g. the {@code
 * [0]} in {@code items[0]}. The index itself is a full sub-expression (the
 * grammar allows {@code items[i + 1]}), not just a literal.
 */
public class JinjaSubscriptNode extends JinjaNode {

    private final JinjaNode object;
    private final JinjaNode index;

    public JinjaSubscriptNode(JinjaNode object, JinjaNode index, int line, int column) {
        super(line, column);
        this.object = object;
        this.index = index;
    }

    public JinjaNode getObject() {
        return object;
    }

    public JinjaNode getIndex() {
        return index;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaSubscript(this);
    }

    /** Prints the subscripted object followed by its index expression, e.g. {@code items[0]}. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.child(ind, last, "Object", object),
                (ind, last) -> TreePrinter.child(ind, last, "Index", index));
    }
}
