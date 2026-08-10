package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Root node for a Jinja2 template program.
 * Contains all top-level Jinja2 elements.
 */
public class JinjaProgramNode extends JinjaNode {

    private final List<JinjaNode> elements;

    public JinjaProgramNode(List<JinjaNode> elements) {
        this(elements, 1, 1);
    }

    public JinjaProgramNode(List<JinjaNode> elements, int line, int column) {
        super(line, column);
        this.elements = new ArrayList<>(elements);
    }

    public List<JinjaNode> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaProgram(this);
    }

    /** Prints this program's top-level Jinja2 elements. */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        TreePrinter.children(TreePrinter.continuation(indent), true, "Elements", elements);
    }
}
