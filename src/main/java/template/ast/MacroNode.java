package template.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

public class MacroNode extends JinjaMacro {

    private final String macroName;
    private final List<String> parameters;

    public MacroNode(String macroName, List<String> parameters) {
        super(macroName, java.util.List.of());
        this.macroName = macroName;
        this.parameters = new ArrayList<>(parameters);
    }

    public String getMacroName() { return macroName; }
    public List<String> getParameters() { return Collections.unmodifiableList(parameters); }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitRule(this);
    }
}