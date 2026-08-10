package template.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import template.visitor.TemplateVisitor;

public class BlockNode extends JinjaBlock {

    private final String blockName;
    private final List<TemplateNode> body;

    public BlockNode(String blockName, List<TemplateNode> body) {
        super(blockName, body);
        this.blockName = blockName;
        this.body = new ArrayList<>(body);
    }

    public String getBlockName() { return blockName; }
    public boolean hasBody() { return !body.isEmpty(); }
    public List<TemplateNode> getBody() { return Collections.unmodifiableList(body); }

    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitBlock(this);
    }
}