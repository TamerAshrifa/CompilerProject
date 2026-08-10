package template.ast;

import template.visitor.TemplateVisitor;

public class IncludeNode extends JinjaInclude {

    private final String templatePath;
    private final boolean withContext;

    public IncludeNode(String templatePath, boolean withContext) {
        super(templatePath, java.util.List.of());
        this.templatePath = templatePath;
        this.withContext = withContext;
    }

    public String getTemplatePath() { return templatePath; }
    public boolean isWithContext() { return withContext; }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitInclude(this);
    }
}