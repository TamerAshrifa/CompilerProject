package flask.ast.nodes.expressions.atoms;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DictNode extends Expression {

    public static class DictItem {
        private final Expression key;
        private final Expression value;

        public DictItem(Expression key, Expression value) {
            this.key = key;
            this.value = value;
        }

        public Expression getKey() { return key; }
        public Expression getValue() { return value; }
    }

    private final List<DictItem> entries;

    public DictNode(List<DictItem> entries, int line, int column) {
        super(line, column);
        this.entries = new ArrayList<>(entries);
    }

    /** Convenience constructor for hand-built ASTs with no real source position (e.g. tests). */
    public DictNode(List<DictItem> entries) {
        this(entries, 0, 0);
    }

    public List<DictItem> getItems() {
        return Collections.unmodifiableList(entries);
    }

    public List<DictItem> getEntries() {
        return getItems();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDict(this);
    }
}