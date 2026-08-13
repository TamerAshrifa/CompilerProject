package template.ast;

import java.util.List;
import template.visitor.TemplateVisitor;

public class ExpressionNode extends JinjaExpression {

    private final String expression;

    public ExpressionNode(String expression) {
        super(expression, List.of());
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitExpression(this);
    }
}