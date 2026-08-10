package template.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

public class TemplateRuleNode extends TemplateNode {

    private final String ruleName;
    private final List<TemplateNode> children;

    public TemplateRuleNode(String ruleName, List<TemplateNode> children, int line, int column) {
        super(line, column);
        this.ruleName = ruleName;
        this.children = new ArrayList<>(children);
    }

    public String getRuleName() {
        return ruleName;
    }

    public List<TemplateNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitRule(this);
    }
}