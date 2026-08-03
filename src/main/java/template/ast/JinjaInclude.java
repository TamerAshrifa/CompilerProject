package template.ast;

import java.util.List;

public class JinjaInclude extends TemplateRuleNode {
    public JinjaInclude(String templatePath, List<TemplateNode> children) {
        super(templatePath, children, 0, 0); // legacy, unused
    }
}