package template.ast;

import java.util.List;

public class TemplateProgram extends TemplateRuleNode {

    public TemplateProgram(List<TemplateNode> elements) {
        super("TemplateProgram", elements, 0, 0); // legacy, unused
    }
}