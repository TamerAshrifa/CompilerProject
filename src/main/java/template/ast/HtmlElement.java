package template.ast;

import java.util.List;

public class HtmlElement extends TemplateRuleNode {

    public HtmlElement(String tagName, List<TemplateNode> children) {
        super(tagName, children, 0, 0); // legacy, unused
    }
}