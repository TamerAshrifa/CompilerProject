package printer;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Shared, reusable tree-drawing engine behind every AST node's {@code print}
 * override in this project.
 *
 * <p>Individual node classes stay tiny and declarative: each one builds an
 * ordered list of its own "fields" (attributes and children) and hands it to
 * {@link #fields}, describing each field once as either a plain value, a
 * single nested node, a list of nested nodes, or a label -&gt; node map. This
 * class does the actual work of drawing the tree - the correct choice of
 * {@code ├──} vs {@code └──}, the correct continuation of {@code │} down
 * through the levels above, and recursing into children via {@link Printable#print}
 * - so that logic exists in exactly one place instead of being re-implemented
 * by hand in every one of the (many) node classes across the Python and
 * Jinja2 ASTs.
 *
 * <p>All output goes to {@code System.out}, matching the simple, synchronous
 * "print the tree" usage this project needs (see the usage examples in
 * {@code printer.PrintDemo}).
 */
public final class TreePrinter {

    static {
        // The box-drawing characters this engine prints (├──, └──, │) must
        // reach the console as UTF-8. file.encoding is guaranteed UTF-8 by
        // the JDK (JEP 400), but stdout/stderr specifically still follow the
        // host platform's native console encoding by default, which on a
        // system with no locale configured can be a plain 7-bit charset that
        // silently mangles every non-ASCII character into "?". Re-wrapping
        // System.out once, the first time this printing engine is actually
        // used, makes tree output correct everywhere without depending on
        // how the JVM happens to be launched.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    private TreePrinter() {
        // Static utility class - never instantiated.
    }

    private static final String BRANCH_MID  = "├── ";
    private static final String BRANCH_LAST = "└── ";
    private static final String BAR_GAP     = "│   ";
    private static final String BLANK_GAP   = "    ";

    /** The "├── " or "└── " glyph for a node at the given sibling position. */
    public static String branch(boolean isLast) {
        return isLast ? BRANCH_LAST : BRANCH_MID;
    }

    /**
     * Converts the indent a node was itself printed with (which ends in a
     * branch glyph, or is {@code ""} for a root node) into the base indent
     * that node's OWN fields should line up under: {@code │   } continues a
     * parent's branch downward past this node, while {@code "    "} (blank)
     * is used once nothing more follows at that level.
     */
    public static String continuation(String indent) {
        if (indent.endsWith(BRANCH_MID)) {
            return indent.substring(0, indent.length() - BRANCH_MID.length()) + BAR_GAP;
        }
        if (indent.endsWith(BRANCH_LAST)) {
            return indent.substring(0, indent.length() - BRANCH_LAST.length()) + BLANK_GAP;
        }
        return indent; // Root call: "" straight in, "" straight out.
    }

    /** The indent that a field's OWN children line up under. */
    private static String extend(String indent, boolean isLast) {
        return indent + (isLast ? BLANK_GAP : BAR_GAP);
    }

    private static String describe(Object value) {
        return value == null ? "(none)" : String.valueOf(value);
    }

    /**
     * One "label: value" leaf line, for plain scalar attributes such as a
     * variable's name, an operator, or a literal's value.
     */
    public static void leaf(String indent, boolean isLast, String label, Object value) {
        System.out.println(indent + branch(isLast) + label + ": " + describe(value));
    }

    /**
     * A single nested node field - e.g. an if-statement's condition.
     * Renders {@code "label:"} then the node as the sole item beneath it,
     * recursing via the node's own (polymorphic) {@link Printable#print}.
     * A {@code null} child is skipped entirely: a missing optional node
     * (an else-less if's else-branch, a bare raise's exception, ...) means
     * the field simply does not apply here, so nothing is printed for it.
     */
    public static void child(String indent, boolean isLast, String label, Printable node) {
        if (node == null) {
            return;
        }
        System.out.println(indent + branch(isLast) + label + ":");
        node.print(extend(indent, isLast) + BRANCH_LAST);
    }

    /**
     * A list of nested node children under one label - e.g. a function
     * body's statements. Renders {@code "label:"} then each element with
     * proper {@code ├──}/{@code └──} branching, recursing polymorphically
     * into every one. An empty (but present) list still renders as a
     * {@code "(none)"} leaf, so the field's emptiness stays visible instead
     * of silently disappearing from the tree.
     */
    public static void children(String indent, boolean isLast, String label, List<? extends Printable> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            leaf(indent, isLast, label, "(none)");
            return;
        }
        System.out.println(indent + branch(isLast) + label + ":");
        String base = extend(indent, isLast);
        int n = nodes.size();
        for (int i = 0; i < n; i++) {
            Printable node = nodes.get(i);
            if (node == null) {
                continue;
            }
            node.print(base + branch(i == n - 1));
        }
    }

    /**
     * A list of plain (non-node) values under one label - e.g. an import's
     * module names or a lambda's parameter names. Laid out just like
     * {@link #children}, but each element is rendered with its own
     * {@code toString()} rather than recursed into.
     */
    public static void values(String indent, boolean isLast, String label, List<?> values) {
        if (values == null || values.isEmpty()) {
            leaf(indent, isLast, label, "(none)");
            return;
        }
        System.out.println(indent + branch(isLast) + label + ":");
        String base = extend(indent, isLast);
        int n = values.size();
        for (int i = 0; i < n; i++) {
            System.out.println(base + branch(i == n - 1) + describe(values.get(i)));
        }
    }

    /**
     * A label -&gt; node map field - e.g. a call's keyword arguments. Each
     * entry renders as {@code "key:"} followed by its value node, the same
     * way a single {@link #child} does.
     */
    public static void entries(String indent, boolean isLast, String label, Map<String, ? extends Printable> map) {
        if (map == null || map.isEmpty()) {
            leaf(indent, isLast, label, "(none)");
            return;
        }
        System.out.println(indent + branch(isLast) + label + ":");
        String base = extend(indent, isLast);
        int i = 0;
        int n = map.size();
        for (Map.Entry<String, ? extends Printable> entry : map.entrySet()) {
            i++;
            child(base, i == n, entry.getKey(), entry.getValue());
        }
    }

    /**
     * One entry in the ordered field list passed to {@link #fields}. Pairs a
     * rendering action with automatic last-of-siblings detection, so a
     * node's {@code print()} override can build its field list once, in
     * declaration order, including optional fields conditionally, without
     * ever having to hand-track which one ends up last.
     */
    @FunctionalInterface
    public interface Field {
        void render(String indent, boolean isLast);
    }

    /** Convenience for a node whose fields are always all present. */
    public static void fields(String indent, Field... fields) {
        fields(indent, Arrays.asList(fields));
    }

    /**
     * Renders an ordered list of fields already assembled by the caller
     * (who may have conditionally left optional ones out), computing
     * "is this the last visible field" automatically from the final list.
     */
    public static void fields(String indent, List<Field> fields) {
        int n = fields.size();
        for (int i = 0; i < n; i++) {
            fields.get(i).render(indent, i == n - 1);
        }
    }
}
