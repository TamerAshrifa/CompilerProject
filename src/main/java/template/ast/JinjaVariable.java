package template.ast;

import java.util.List;

public class JinjaVariable extends TemplateRuleNode {

    public JinjaVariable(String name, List<TemplateNode> children) {
        super(name, children, 0, 0); // legacy, unused: kept only for backward-compatible signature
    }
}