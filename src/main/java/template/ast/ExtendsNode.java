package template.ast;

import template.visitor.TemplateVisitor;

public class ExtendsNode extends JinjaExtends {

    private final String parentTemplatePath;

    public ExtendsNode(String parentTemplatePath) {
        super(parentTemplatePath, java.util.List.of());
        this.parentTemplatePath = parentTemplatePath;
    }

    public String getParentTemplatePath() { return parentTemplatePath; }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitExtends(this);
    }
}