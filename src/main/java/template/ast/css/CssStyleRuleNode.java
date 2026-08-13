package template.ast.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/** A qualified CSS rule: one or more comma-separated selectors followed by
 * a block of declarations, e.g. {@code .card, .card-highlight { color: red; } }
 */
public class CssStyleRuleNode extends CssRuleNode {
    private final List<String> selectors;
    private final List<CssDeclarationNode> declarations;

    public CssStyleRuleNode(List<String> selectors, List<CssDeclarationNode> declarations, int line, int column) {
        super(line, column);
        this.selectors = new ArrayList<>(selectors);
        this.declarations = new ArrayList<>(declarations);
    }

    public List<String> getSelectors() {
        return Collections.unmodifiableList(selectors);
    }

    public List<CssDeclarationNode> getDeclarations() {
        return Collections.unmodifiableList(declarations);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitCssStyleRule(this);
    }
}
