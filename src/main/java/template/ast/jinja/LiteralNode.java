package template.ast.jinja;

import template.visitor.TemplateVisitor;

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
}
