package template.ast;

import java.util.List;

public class JinjaExtends extends TemplateRuleNode {
    public JinjaExtends(String templatePath, List<TemplateNode> children) {
        super(templatePath, children, 0, 0); // legacy, unused
    }
}