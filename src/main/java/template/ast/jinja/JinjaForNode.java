package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/**
 * Jinja2 for loop node: {% for var in iterable %} ... {% endfor %}
 * Represents iteration over a sequence with optional else clause.
 */
public class JinjaForNode extends JinjaNode {

    private final String loopVariable;
    private final String iterable;
    private final JinjaNode iterableTree;
    private final List<JinjaNode> body;
    private final List<JinjaNode> elseBody;

    public JinjaForNode(
        String loopVariable,
        String iterable,
        List<JinjaNode> body,
        int line,
        int column
    ) {
        super(line, column);
        this.loopVariable = loopVariable;
        this.iterable = iterable;
        this.iterableTree = null;
        this.body = new ArrayList<>(body);
        this.elseBody = new ArrayList<>();
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public JinjaForNode(String loopVariable, String iterable, List<JinjaNode> body) {
        this(loopVariable, iterable, body, 0, 0);
    }

    /**
     * @param iterableTree the structured expression tree parsed from the
     *                     same source text as {@code iterable}, or {@code null}.
     */
    public JinjaForNode(
        String loopVariable,
        String iterable,
        JinjaNode iterableTree,
        List<JinjaNode> body,
        int line,
        int column
    ) {
        super(line, column);
        this.loopVariable = loopVariable;
        this.iterable = iterable;
        this.iterableTree = iterableTree;
        this.body = new ArrayList<>(body);
        this.elseBody = new ArrayList<>();
    }

    public JinjaForNode(
        String loopVariable,
        String iterable,
        List<JinjaNode> body,
        List<JinjaNode> elseBody,
        int line,
        int column
    ) {
        super(line, column);
        this.loopVariable = loopVariable;
        this.iterable = iterable;
        this.iterableTree = null;
        this.body = new ArrayList<>(body);
        this.elseBody = new ArrayList<>(elseBody);
    }

    public JinjaForNode(
        String loopVariable,
        String iterable,
        JinjaNode iterableTree,
        List<JinjaNode> body,
        List<JinjaNode> elseBody,
        int line,
        int column
    ) {
        super(line, column);
        this.loopVariable = loopVariable;
        this.iterable = iterable;
        this.iterableTree = iterableTree;
        this.body = new ArrayList<>(body);
        this.elseBody = new ArrayList<>(elseBody);
    }

    public String getLoopVariable() {
        return loopVariable;
    }

    public String getIterable() {
        return iterable;
    }

    /**
     * The structured expression tree parsed from the same source text as
     * {@link #getIterable()}, or {@code null} if this node was built
     * without one. Prefer this over re-parsing {@link #getIterable()}
     * whenever it is available.
     */
    public JinjaNode getIterableTree() {
        return iterableTree;
    }

    public List<JinjaNode> getBody() {
        return Collections.unmodifiableList(body);
    }

    public boolean hasElse() {
        return !elseBody.isEmpty();
    }

    public List<JinjaNode> getElseBody() {
        return Collections.unmodifiableList(elseBody);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaFor(this);
    }
}
