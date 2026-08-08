package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/**
 * Jinja2 else node: {% else %} ...
 * The final else branch in an if statement's conditional chain.
 */
public class JinjaElseNode extends JinjaNode {

    private final List<JinjaNode> body;

    public JinjaElseNode(List<JinjaNode> body, int line, int column) {
        super(line, column);
        this.body = new ArrayList<>(body);
    }

    public List<JinjaNode> getBody() {
        return Collections.unmodifiableList(body);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaElse(this);
    }
}
