package template.ast.jinja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

/**
 * Jinja2 macro node: {% macro name(arg1, arg2) %} ... {% endmacro %}
 * Defines a reusable macro (function) that can be called later.
 */
public class JinjaMacroNode extends JinjaNode {

    private final String macroName;
    private final List<String> parameters;
    private final List<JinjaNode> body;

    public JinjaMacroNode(String macroName, List<String> parameters, List<JinjaNode> body, int line, int column) {
        super(line, column);
        this.macroName = macroName;
        this.parameters = new ArrayList<>(parameters);
        this.body = new ArrayList<>(body);
    }

    public String getMacroName() {
        return macroName;
    }

    public List<String> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public List<JinjaNode> getBody() {
        return Collections.unmodifiableList(body);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaMacro(this);
    }
}
