package template.ast;

import template.visitor.TemplateVisitor;

public class ForNode extends JinjaFor {

    public ForNode(String loopVariable, String iterable, java.util.List<TemplateNode> body, java.util.List<TemplateNode> elseBody) {
        super(loopVariable, iterable, body, elseBody);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitFor(this);
    }
}