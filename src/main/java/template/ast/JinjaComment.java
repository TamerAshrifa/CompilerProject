package template.ast;

public class JinjaComment extends TemplateRuleNode {
    public JinjaComment(String content) {
        super(content, java.util.List.of(), 0, 0); // legacy, unused
    }
}