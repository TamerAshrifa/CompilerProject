package generator;

import java.util.Collection;
import java.util.Map;

/**
 * Implements Python/Jinja2 "truthiness" rules for values produced by the
 * data-flow analyzer (used both for Python {@code if}/{@code while}
 * conditions and for Jinja2 {@code {% if %}} evaluation).
 */
public final class PythonTruth {

    private PythonTruth() {
    }

    public static boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof Number n) {
            return n.doubleValue() != 0.0;
        }
        if (value instanceof String s) {
            return !s.isEmpty();
        }
        if (value instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }
        if (value instanceof Map<?, ?> map) {
            return !map.isEmpty();
        }
        return true;
    }
}
