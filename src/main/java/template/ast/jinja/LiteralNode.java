package template.ast.jinja;

import template.visitor.TemplateVisitor;
import printer.TreePrinter;

/**
 * Represents a literal value in Jinja2 AST.
 *
 * This node is created by the Generator when substituting
 * Jinja2 variable nodes with their concrete runtime values.
 *
 * Example: {{ name }} → LiteralNode("Ali")
 */
public class LiteralNode extends JinjaNode {

    private final Object value;
    private final String literalType;

    /**
     * Create a literal node with a value.
     *
     * @param value The literal value (String, Integer, Boolean, etc.)
     */
    public LiteralNode(Object value, int line, int column) {
        super(line, column);
        this.value = value;
        this.literalType = getLiteralType(value);
    }

    /**
     * Get the literal value.
     *
     * @return The literal value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get the type of the literal.
     *
     * @return "string", "integer", "boolean", "null", etc.
     */
    public String getLiteralType() {
        return literalType;
    }

    /**
     * Get the literal value as a string representation.
     *
     * @return String representation suitable for template substitution
     */
    public String getStringValue() {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    /**
     * Accept a visitor.
     */
    @Override
    public <T> T accept(TemplateVisitor<T> visitor) {
        return visitor.visitJinjaLiteral(this);
    }

    /**
     * Determine the type of a literal value.
     */
    private static String getLiteralType(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "string";
        }
        if (value instanceof Integer) {
            return "integer";
        }
        if (value instanceof Long) {
            return "long";
        }
        if (value instanceof Double) {
            return "double";
        }
        if (value instanceof Float) {
            return "float";
        }
        if (value instanceof Boolean) {
            return "boolean";
        }
        return "object";
    }

    @Override
    public String toString() {
        return "LiteralNode{" +
                "value=" + value +
                ", literalType='" + literalType + '\'' +
                '}';
    }

    /**
     * Prints this literal's value and its resolved {@link #getLiteralType()},
     * e.g. a value of {@code "Ali"} with type {@code string}. Formatted
     * specially (rather than via the generic {@link TreePrinter#leaf})
     * so a genuine {@code null} value is shown as the literal word
     * {@code null} - matching this class's own {@link #getLiteralType()}
     * terminology - rather than being confused with {@code TreePrinter}'s
     * unrelated "(none)" marker for an absent field.
     */
    @Override
    public void print(String indent) {
        System.out.println(indent + selfDescription());
        String display = (value == null) ? "null" : (value instanceof String) ? "\"" + value + "\"" : String.valueOf(value);
        String base = TreePrinter.continuation(indent);
        TreePrinter.fields(base,
                (ind, last) -> TreePrinter.leaf(ind, last, "Value", display),
                (ind, last) -> TreePrinter.leaf(ind, last, "Type", literalType));
    }
}
