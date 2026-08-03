package template.ast;

import template.visitor.TemplateVisitor;

public class IfNode extends JinjaIf {

    public IfNode(String condition, java.util.List<TemplateNode> thenBody, java.util.List<ElifClause> elifClauses, JinjaElse elseNode, java.util.List<TemplateNode> elseBody) {
        super(condition, thenBody, elifClauses, elseNode, elseBody);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitIf(this);
    }
}