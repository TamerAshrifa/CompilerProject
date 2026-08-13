package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Jinja2 block node: {% block block_name %} ... {% endblock %}
 * Defines a named block that can be overridden in child templates.
 * Used with template inheritance.
 */
public class JinjaBlockNode extends JinjaNode {

    private final String blockName;
    private final List<JinjaNode> body;

    public JinjaBlockNode(String blockName, List<JinjaNode> body, int line, int column) {
        super(line, column);
        this.blockName = blockName;
        this.body = new ArrayList<>(body);
    }

    public String getBlockName() {
        return blockName;
    }

    public List<JinjaNode> getBody() {
        return Collections.unmodifiableList(body);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaBlock(this);
    }

    /** Prints the block's name and its body, e.g. {@code {% block content %} ... {% endblock %} }. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.leaf(ind, last, "BlockName", blockName),
                (ind, last) -> TreePrinter.children(ind, last, "Body", body));
    }
}
