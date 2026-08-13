package template.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JinjaFor extends TemplateRuleNode {

    private final String loopVariable;
    private final String iterable;
    private final List<TemplateNode> body;
    private final List<TemplateNode> elseBody;

    public JinjaFor(String loopVariable, String iterable, List<TemplateNode> body, List<TemplateNode> elseBody) {
        super(loopVariable, body, 0, 0); // legacy, unused
        this.loopVariable = loopVariable;
        this.iterable = iterable;
        this.body = new ArrayList<>(body);
        this.elseBody = new ArrayList<>(elseBody);
    }

    public String getLoopVariable() { return loopVariable; }
    public String getIterable() { return iterable; }
    public List<TemplateNode> getBody() { return Collections.unmodifiableList(body); }
    public boolean hasElse() { return !elseBody.isEmpty(); }
    public List<TemplateNode> getElseBody() { return Collections.unmodifiableList(elseBody); }
}