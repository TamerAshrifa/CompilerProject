package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/**
 * Jinja2 elif node: {% elif condition %} ... 
 * Part of an if statement's conditional chain.
 */
public class JinjaElifNode extends JinjaNode {

    private final String condition;
    private final JinjaNode conditionTree;
    private final List<JinjaNode> body;

    public JinjaElifNode(String condition, List<JinjaNode> body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.conditionTree = null;
        this.body = new ArrayList<>(body);
    }

    /**
     * @param conditionTree the structured expression tree parsed from the
     *                      same source text as {@code condition}, or {@code null}.
     */
    public JinjaElifNode(String condition, JinjaNode conditionTree, List<JinjaNode> body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.conditionTree = conditionTree;
        this.body = new ArrayList<>(body);
    }

    public String getCondition() {
        return condition;
    }

    /**
     * The structured expression tree parsed from the same source text as
     * {@link #getCondition()}, or {@code null} if this node was built
     * without one.
     */
    public JinjaNode getConditionTree() {
        return conditionTree;
    }

    public List<JinjaNode> getBody() {
        return Collections.unmodifiableList(body);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaElif(this);
    }
}
