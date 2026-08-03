package template.ast.css;

import template.visitor.TemplateVisitor;

/** One CSS declaration, e.g. {@code color: red;} or {@code margin: 0 auto !important;}. */
public class CssDeclarationNode extends CssNode {
    private final String property;
    private final String value;
    private final boolean important;

    public CssDeclarationNode(String property, String value, boolean important, int line, int column) {
        super(line, column);
        this.property = property;
        this.value = value;
        this.important = important;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public boolean isImportant() {
        return important;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitCssDeclaration(this);
    }
}
