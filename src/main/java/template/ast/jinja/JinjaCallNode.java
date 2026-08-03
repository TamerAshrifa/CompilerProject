package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/**
 * A direct call trailer inside a Jinja2 expression tree, e.g. {@code
 * items.count()} or {@code loop.cycle(a, b)}. Distinct from a filter
 * application ({@code value|filter(args)}), which uses {@link
 * JinjaFilterApplicationNode} instead.
 */
public class JinjaCallNode extends JinjaNode {

    private final JinjaNode callee;
    private final List<JinjaNode> arguments;

    public JinjaCallNode(JinjaNode callee, List<JinjaNode> arguments, int line, int column) {
        super(line, column);
        this.callee = callee;
        this.arguments = new ArrayList<>(arguments);
    }

    public JinjaNode getCallee() {
        return callee;
    }

    public List<JinjaNode> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaCall(this);
    }
}
