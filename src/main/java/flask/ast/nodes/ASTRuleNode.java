package flask.ast.nodes;

import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic node used to wrap an arbitrary grammar rule that does not (yet)
 * have a dedicated typed node class.
 *
 * <p>This class is a good example of the polymorphism {@link ASTNode}
 * enables: rather than accepting the default "node name == runtime class
 * name" behavior (which would make every instance report the unhelpful
 * name {@code "ASTRuleNode"}), it overrides {@link #getNodeName()} to
 * report the specific grammar rule it represents instead. Calling code
 * that only knows about {@code ASTNode.getNodeName()} still gets the more
 * useful, node-specific answer automatically.
 */
public class ASTRuleNode extends ASTNode {

    private final String ruleName;
    private final List<ASTNode> children;

    public ASTRuleNode(String ruleName, List<ASTNode> children, int line, int column) {
        super(line, column);
        this.ruleName = ruleName;
        this.children = new ArrayList<>(children);
    }

    public String getRuleName() {
        return ruleName;
    }

    public List<ASTNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Overrides the default runtime-class-name behavior from {@link ASTNode}
     * to report the wrapped grammar rule's name instead, since that is the
     * more meaningful identity for a generic rule-wrapper node.
     */
    @Override
    public String getNodeName() {
        return ruleName;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}