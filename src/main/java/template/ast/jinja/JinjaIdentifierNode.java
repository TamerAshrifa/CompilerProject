package template.ast.jinja;

import template.visitor.TemplateVisitor;

/**
 * A bare identifier used inside a Jinja2 expression tree, e.g. the {@code
 * user} in {@code user.name} or the {@code items} in {@code items[0]}.
 *
 * This is the leaf of the structured expression tree built for anything
 * more complex than a plain "name(.attr|[idx])*" chain (see {@link
 * JinjaExpressionNode#getRoot()}). It plays the same role for Jinja2
 * expressions that {@code IdentifierNode} plays for Python expressions.
 */
public class JinjaIdentifierNode extends JinjaNode {

    private final String name;

    public JinjaIdentifierNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaIdentifier(this);
    }
}
