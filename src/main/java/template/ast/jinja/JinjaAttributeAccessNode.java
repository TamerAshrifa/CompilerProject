package template.ast.jinja;

import template.visitor.TemplateVisitor;

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
}
