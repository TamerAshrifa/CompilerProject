package generator;

import java.util.Objects;

/**
 * Best-effort simulation of Python's binary operators over the small set of
 * literal value types the analyzer works with (String, Boolean, Integer,
 * Long, Double). Used only when both operands are statically known.
 */
public final class PythonArithmetic {

    private PythonArithmetic() {
    }

    public static Object apply(String operator, Object left, Object right) {
        if (operator == null) {
            return DataFlowAnalyzer.UNKNOWN;
        }
        if ("+".equals(operator) && (left instanceof String || right instanceof String)) {
            return String.valueOf(left) + String.valueOf(right);
        }
        if (left instanceof Number leftNumber && right instanceof Number rightNumber) {
            boolean isDouble = leftNumber instanceof Double || rightNumber instanceof Double;
            double a = leftNumber.doubleValue();
            double b = rightNumber.doubleValue();
            Double result = switch (operator) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> b != 0 ? a / b : null;
                case "//" -> b != 0 ? Math.floor(a / b) : null;
                case "%" -> b != 0 ? a % b : null;
                default -> null;
            };
            if (result == null) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            if ("/".equals(operator)) {
                return result;
            }
            if (!isDouble && result == Math.floor(result) && !result.isInfinite()) {
                return result.intValue();
            }
            return result;
        }
        return DataFlowAnalyzer.UNKNOWN;
    }

    /** Returns null if the comparison could not be evaluated. */
    public static Boolean compare(String operator, Object left, Object right) {
        if (operator == null) {
            return null;
        }
        switch (operator) {
            case "==":
                return Objects.equals(left, right);
            case "!=":
                return !Objects.equals(left, right);
            case "in":
                if (right instanceof java.util.Collection<?> collection) {
                    return collection.contains(left);
                }
                if (right instanceof String s && left instanceof String sub) {
                    return s.contains(sub);
                }
                return null;
            case "is":
                return left == right || Objects.equals(left, right);
            default:
                break;
        }
        if (left instanceof Number leftNumber && right instanceof Number rightNumber) {
            int cmp = Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
            return switch (operator) {
                case "<" -> cmp < 0;
                case ">" -> cmp > 0;
                case "<=" -> cmp <= 0;
                case ">=" -> cmp >= 0;
                default -> null;
            };
        }
        if (left instanceof String leftStr && right instanceof String rightStr) {
            int cmp = leftStr.compareTo(rightStr);
            return switch (operator) {
                case "<" -> cmp < 0;
                case ">" -> cmp > 0;
                case "<=" -> cmp <= 0;
                case ">=" -> cmp >= 0;
                default -> null;
            };
        }
        return null;
    }
}
