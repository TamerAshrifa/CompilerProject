package template.ast;

import java.util.List;

public class JinjaBlock extends TemplateRuleNode {

    public JinjaBlock(String name, List<TemplateNode> children) {
        super(name, children, 0, 0); // legacy, unused
    }
}