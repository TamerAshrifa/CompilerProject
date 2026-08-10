package flask.ast.nodes.expressions.comprehensions;

import flask.ast.nodes.Expression;
import flask.ast.visitor.ASTVisitor;
import printer.Printable;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Common base for list/set/dict comprehensions: [expr for x in y if cond],
 * {expr for x in y}, {k: v for x in y}.
 *
 * Python allows chaining several "for ... in ..." clauses in one
 * comprehension (e.g. "for row in matrix for x in row"), each with zero or
 * more trailing "if" filters, so a comprehension carries an ordered list of
 * ForClause rather than a single target/iterable/condition triple.
 */
public abstract class ComprehensionNode extends Expression {

    /**
     * One "for target in iterable [if cond]*" clause of a comprehension.
     */
    public static final class ForClause {
        private final Expression target;
        private final Expression iterable;
        private final List<Expression> conditions;

        public ForClause(Expression target, Expression iterable, List<Expression> conditions) {
            this.target = target;
            this.iterable = iterable;
            this.conditions = new ArrayList<>(conditions);
        }

        public Expression getTarget() {
            return target;
        }

        public Expression getIterable() {
            return iterable;
        }

        public List<Expression> getConditions() {
            return Collections.unmodifiableList(conditions);
        }
    }

    private final List<ForClause> clauses;

    protected ComprehensionNode(List<ForClause> clauses, int line, int column) {
        super(line, column);
        this.clauses = new ArrayList<>(clauses);
    }

    public List<ForClause> getClauses() {
        return Collections.unmodifiableList(clauses);
    }

    @Override
    public abstract <T> T accept(ASTVisitor<T> visitor);

    /**
     * Builds the "Clauses" field shared by every concrete comprehension
     * shape. Every subclass's {@code print} override adds this to its own
     * field list (after its own element/key/value fields), so the one part
     * all four comprehension kinds have in common - the chained
     * "for target in iterable [if cond]*" clauses - is only ever rendered
     * in this one place rather than duplicated in each subclass.
     */
    protected final TreePrinter.Field clausesField() {
        return (ind, last) -> TreePrinter.children(ind, last, "Clauses", wrapClauses(clauses));
    }

    /** Adapts each {@link ForClause} into a {@link Printable} so it can be handed to {@link TreePrinter#children}. */
    private static List<Printable> wrapClauses(List<ForClause> clauses) {
        List<Printable> wrapped = new ArrayList<>();
        for (ForClause clause : clauses) {
            wrapped.add(indent -> {
                System.out.println(indent + "ForClause");
                String base = TreePrinter.continuation(indent);
                List<TreePrinter.Field> fields = new ArrayList<>();
                fields.add((ind, last) -> TreePrinter.child(ind, last, "Target", clause.getTarget()));
                fields.add((ind, last) -> TreePrinter.child(ind, last, "Iterable", clause.getIterable()));
                if (!clause.getConditions().isEmpty()) {
                    fields.add((ind, last) -> TreePrinter.children(ind, last, "Conditions", clause.getConditions()));
                }
                TreePrinter.fields(base, fields);
            });
        }
        return wrapped;
    }
}
