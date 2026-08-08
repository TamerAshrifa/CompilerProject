package template.ast;

public class JinjaFilter extends TemplateRuleNode {

    public JinjaFilter(String filterName, java.util.List<TemplateNode> children) {
        super(filterName, children, 0, 0); // legacy, unused
    }
}