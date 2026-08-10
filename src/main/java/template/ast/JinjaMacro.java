package template.ast;

import java.util.List;

public class JinjaMacro extends TemplateRuleNode {
    public JinjaMacro(String name, List<TemplateNode> children) {
        super(name, children, 0, 0); // legacy, unused
    }
}