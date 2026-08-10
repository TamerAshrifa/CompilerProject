package template.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JinjaIf extends TemplateRuleNode {

    public static class ElifClause {
        public final JinjaElif node;
        public final String condition;
        public final List<TemplateNode> body;

        public ElifClause(JinjaElif node, String condition, List<TemplateNode> body) {
            this.node = node;
            this.condition = condition;
            this.body = new ArrayList<>(body);
        }
    }

    private final String condition;
    private final List<TemplateNode> thenBody;
    private final List<ElifClause> elifClauses;
    private final JinjaElse elseNode;
    private final List<TemplateNode> elseBody;

    public JinjaIf(String condition, List<TemplateNode> thenBody, List<ElifClause> elifClauses, JinjaElse elseNode, List<TemplateNode> elseBody) {
        super(condition, thenBody, 0, 0); // legacy, unused
        this.condition = condition;
        this.thenBody = new ArrayList<>(thenBody);
        this.elifClauses = new ArrayList<>(elifClauses);
        this.elseNode = elseNode;
        this.elseBody = new ArrayList<>(elseBody);
    }

    public String getCondition() { return condition; }
    public List<TemplateNode> getThenBody() { return Collections.unmodifiableList(thenBody); }
    public List<ElifClause> getElifClauses() { return Collections.unmodifiableList(elifClauses); }
    public boolean hasElse() { return elseNode != null || !elseBody.isEmpty(); }
    public List<TemplateNode> getElseBody() { return Collections.unmodifiableList(elseBody); }
    public JinjaElse getElseNode() { return elseNode; }
}