package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Jinja2 if statement node: {% if condition %} ... {% endif %}
 * Represents conditional rendering with optional elif and else branches.
 */
public class JinjaIfNode extends JinjaNode {

    private final String condition;
    private final JinjaNode conditionTree;
    private final List<JinjaNode> thenBody;
    private final List<JinjaElifNode> elifNodes;
    private final JinjaElseNode elseNode;

    public JinjaIfNode(String condition, List<JinjaNode> thenBody, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.conditionTree = null;
        this.thenBody = new ArrayList<>(thenBody);
        this.elifNodes = new ArrayList<>();
        this.elseNode = null;
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public JinjaIfNode(String condition, List<JinjaNode> thenBody) {
        this(condition, thenBody, 0, 0);
    }

    /**
     * @param conditionTree the structured expression tree parsed from the
     *                      same source text as {@code condition} (see
     *                      {@link #getConditionTree()}), or {@code null}.
     */
    public JinjaIfNode(String condition, JinjaNode conditionTree, List<JinjaNode> thenBody, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.conditionTree = conditionTree;
        this.thenBody = new ArrayList<>(thenBody);
        this.elifNodes = new ArrayList<>();
        this.elseNode = null;
    }

    public JinjaIfNode(
        String condition,
        List<JinjaNode> thenBody,
        List<JinjaElifNode> elifNodes,
        JinjaElseNode elseNode,
        int line,
        int column
    ) {
        super(line, column);
        this.condition = condition;
        this.conditionTree = null;
        this.thenBody = new ArrayList<>(thenBody);
        this.elifNodes = new ArrayList<>(elifNodes);
        this.elseNode = elseNode;
    }

    public JinjaIfNode(
        String condition,
        JinjaNode conditionTree,
        List<JinjaNode> thenBody,
        List<JinjaElifNode> elifNodes,
        JinjaElseNode elseNode,
        int line,
        int column
    ) {
        super(line, column);
        this.condition = condition;
        this.conditionTree = conditionTree;
        this.thenBody = new ArrayList<>(thenBody);
        this.elifNodes = new ArrayList<>(elifNodes);
        this.elseNode = elseNode;
    }

    public String getCondition() {
        return condition;
    }

    /**
     * The structured expression tree parsed from the same source text as
     * {@link #getCondition()}, or {@code null} if this node was built
     * without one. Prefer this over re-parsing {@link #getCondition()}
     * whenever it is available.
     */
    public JinjaNode getConditionTree() {
        return conditionTree;
    }

    public List<JinjaNode> getThenBody() {
        return Collections.unmodifiableList(thenBody);
    }

    public List<JinjaElifNode> getElifNodes() {
        return Collections.unmodifiableList(elifNodes);
    }

    public boolean hasElse() {
        return elseNode != null;
    }

    public JinjaElseNode getElseNode() {
        return elseNode;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaIf(this);
    }

    /**
     * Prints the condition (raw text, plus its structured tree when one was
     * built), the then-body, every {@code elif} node, and - when present -
     * the final {@code else} node.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        List<TreePrinter.Field> fields = new ArrayList<>();
        fields.add((ind, last) -> TreePrinter.leaf(ind, last, "Condition", condition));
        if (conditionTree != null) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "ConditionTree", conditionTree));
        }
        fields.add((ind, last) -> TreePrinter.children(ind, last, "ThenBody", thenBody));
        fields.add((ind, last) -> TreePrinter.children(ind, last, "ElifNodes", elifNodes));
        if (hasElse()) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "Else", elseNode));
        }
        TreePrinter.fields(TreePrinter.continuation(indent), fields);
    }
}
