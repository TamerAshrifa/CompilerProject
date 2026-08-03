package template.ast.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/** Root of a parsed CSS stylesheet: an ordered list of rules. */
public class CssStylesheetNode extends CssNode {
    private final List<CssRuleNode> rules;

    public CssStylesheetNode(List<CssRuleNode> rules, int line, int column) {
        super(line, column);
        this.rules = new ArrayList<>(rules);
    }

    public List<CssRuleNode> getRules() {
        return Collections.unmodifiableList(rules);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitCssStylesheet(this);
    }
}
