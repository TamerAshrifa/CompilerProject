package template.ast;

public class JinjaExpression extends TemplateRuleNode {

    public JinjaExpression(String expression, java.util.List<TemplateNode> children) {
        super(expression, children, 0, 0); // legacy, unused
    }
}