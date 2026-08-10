package flask.ast.nodes.helpers;

import printer.Printable;
import printer.TreePrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree-printing support for the small helper types in this package
 * ({@link Decorator}, {@link Parameter}, {@link WithItem}, {@link ExceptClause}).
 *
 * <p>None of these types is itself an AST node: they carry no line/column of
 * their own and exist purely as plain data holders for a list-shaped part of
 * an owning statement (a function's parameter list, a class's decorator
 * list, ...). They therefore cannot implement {@link Printable} or override
 * {@code print(String)} the way real nodes do.
 *
 * <p>Rather than have every owning node (both {@code FunctionDefNode} and
 * {@code ClassDefNode} have decorators; {@code WithStatementNode} has
 * with-items; {@code TryStatementNode} has except-clauses; ...)
 * re-implement the same "how do I draw one of these in the tree" logic by
 * hand, this class wraps each helper object as a small ad-hoc
 * {@link Printable} - a lambda that prints a suitable header line and then
 * its own sub-fields via {@link TreePrinter}, exactly like a real node's
 * {@code print} override would. The result is a plain {@code List<Printable>}
 * that any owning node can pass straight to {@link TreePrinter#children}.
 */
public final class HelperPrinting {

    private HelperPrinting() {
        // Static utility class - never instantiated.
    }

    /** Wraps a function/class decorator list for use with {@link TreePrinter#children}. */
    public static List<Printable> decorators(List<Decorator> decorators) {
        List<Printable> wrapped = new ArrayList<>();
        for (Decorator decorator : decorators) {
            wrapped.add(indent -> printDecorator(indent, decorator));
        }
        return wrapped;
    }

    private static void printDecorator(String indent, Decorator decorator) {
        System.out.println(indent + "Decorator");
        String base = TreePrinter.continuation(indent);
        List<TreePrinter.Field> fields = new ArrayList<>();
        fields.add((ind, last) -> TreePrinter.child(ind, last, "Name", decorator.getName()));
        if (!decorator.getArgs().isEmpty()) {
            fields.add((ind, last) -> TreePrinter.children(ind, last, "Args", decorator.getArgs()));
        }
        if (!decorator.getKwargs().isEmpty()) {
            fields.add((ind, last) -> TreePrinter.entries(ind, last, "Kwargs", decorator.getKwargs()));
        }
        TreePrinter.fields(base, fields);
    }

    /** Wraps a function's parameter list for use with {@link TreePrinter#children}. */
    public static List<Printable> parameters(List<Parameter> parameters) {
        List<Printable> wrapped = new ArrayList<>();
        for (Parameter parameter : parameters) {
            wrapped.add(indent -> printParameter(indent, parameter));
        }
        return wrapped;
    }

    private static void printParameter(String indent, Parameter parameter) {
        String starPrefix = parameter.isVarArgs() ? "*" : parameter.isKwArgs() ? "**" : "";
        System.out.println(indent + "Parameter: " + starPrefix + parameter.getName());
        String base = TreePrinter.continuation(indent);
        List<TreePrinter.Field> fields = new ArrayList<>();
        if (parameter.hasTypeHint()) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "TypeHint", parameter.getTypeHint()));
        }
        if (parameter.hasDefault()) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "Default", parameter.getDefaultValue()));
        }
        TreePrinter.fields(base, fields);
    }

    /** Wraps a with-statement's item list for use with {@link TreePrinter#children}. */
    public static List<Printable> withItems(List<WithItem> items) {
        List<Printable> wrapped = new ArrayList<>();
        for (WithItem item : items) {
            wrapped.add(indent -> printWithItem(indent, item));
        }
        return wrapped;
    }

    private static void printWithItem(String indent, WithItem item) {
        System.out.println(indent + "WithItem");
        String base = TreePrinter.continuation(indent);
        List<TreePrinter.Field> fields = new ArrayList<>();
        fields.add((ind, last) -> TreePrinter.child(ind, last, "Context", item.getContextExpr()));
        if (item.hasAsName()) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "As", item.getAsName()));
        }
        TreePrinter.fields(base, fields);
    }

    /** Wraps a try-statement's except-clause list for use with {@link TreePrinter#children}. */
    public static List<Printable> exceptClauses(List<ExceptClause> clauses) {
        List<Printable> wrapped = new ArrayList<>();
        for (ExceptClause clause : clauses) {
            wrapped.add(indent -> printExceptClause(indent, clause));
        }
        return wrapped;
    }

    private static void printExceptClause(String indent, ExceptClause clause) {
        System.out.println(indent + "ExceptClause");
        String base = TreePrinter.continuation(indent);
        List<TreePrinter.Field> fields = new ArrayList<>();
        if (!clause.isBareExcept()) {
            fields.add((ind, last) -> TreePrinter.child(ind, last, "Type", clause.getExceptionType()));
        }
        if (clause.getName() != null) {
            fields.add((ind, last) -> TreePrinter.leaf(ind, last, "As", clause.getName()));
        }
        fields.add((ind, last) -> TreePrinter.children(ind, last, "Body", clause.getBody()));
        TreePrinter.fields(base, fields);
    }
}
