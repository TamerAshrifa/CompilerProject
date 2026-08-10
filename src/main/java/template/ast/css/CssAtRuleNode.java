package template.ast.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/** An at-rule, e.g. {@code @media screen { ... } } or {@code @import "x.css"; }.
 * Its block (if any) may itself contain declarations (as in
 * {@code @font-face}), nested rules (as in {@code @media}), or both -
 * the grammar is permissive about this, so both lists are kept.
 */
public class CssAtRuleNode extends CssRuleNode {
    private final String name;
    private final String prelude;
    private final List<CssDeclarationNode> declarations;
    private final List<CssRuleNode> nestedRules;

    public CssAtRuleNode(
        String name,
        String prelude,
        List<CssDeclarationNode> declarations,
        List<CssRuleNode> nestedRules,
        int line,
        int column
    ) {
        super(line, column);
        this.name = name;
        this.prelude = prelude;
        this.declarations = new ArrayList<>(declarations);
        this.nestedRules = new ArrayList<>(nestedRules);
    }

    public String getName() {
        return name;
    }

    public String getPrelude() {
        return prelude;
    }

    public List<CssDeclarationNode> getDeclarations() {
        return Collections.unmodifiableList(declarations);
    }

    public List<CssRuleNode> getNestedRules() {
        return Collections.unmodifiableList(nestedRules);
    }

    public boolean hasBlock() {
        return !declarations.isEmpty() || !nestedRules.isEmpty();
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitCssAtRule(this);
    }
}
