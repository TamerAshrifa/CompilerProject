package template.ast.jinja;

import template.visitor.TemplateVisitor;

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
}
