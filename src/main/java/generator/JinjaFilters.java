package generator;

import java.util.List;
import java.util.Map;

/**
 * Applies a handful of common Jinja2 filters to a value that the data-flow
 * analyzer was able to resolve at compile time. Unknown/unsupported filters
 * return {@code null} so the caller can decide to keep the filter attached
 * to the (still-unresolved) variable node instead of silently dropping it.
 */
public final class JinjaFilters {

    private static final java.util.Set<String> KNOWN = java.util.Set.of(
            "upper", "lower", "capitalize", "title", "trim", "length", "count",
            "first", "last", "default", "d", "int", "float", "string", "str",
            "abs", "round", "join");

    private JinjaFilters() {
    }

    public static boolean isKnown(String filterName) {
        return KNOWN.contains(filterName);
    }

    /** Returns null if the filter is not one we can statically evaluate. */
    public static Object apply(String filterName, String rawArgument, Object value) {
        if (filterName == null) {
            return null;
        }
        switch (filterName) {
            case "upper":
                return value == null ? null : String.valueOf(value).toUpperCase();
            case "lower":
                return value == null ? null : String.valueOf(value).toLowerCase();
            case "capitalize":
                return capitalize(String.valueOf(value));
            case "title":
                return title(String.valueOf(value));
            case "trim":
                return value == null ? null : String.valueOf(value).trim();
            case "length":
            case "count":
                return length(value);
            case "first":
                if (value instanceof List<?> list && !list.isEmpty()) {
                    return list.get(0);
                }
                if (value instanceof String s && !s.isEmpty()) {
                    return String.valueOf(s.charAt(0));
                }
                return null;
            case "last":
                if (value instanceof List<?> list && !list.isEmpty()) {
                    return list.get(list.size() - 1);
                }
                if (value instanceof String s && !s.isEmpty()) {
                    return String.valueOf(s.charAt(s.length() - 1));
                }
                return null;
            case "default":
            case "d":
                boolean isEmpty = value == null || (value instanceof String s2 && s2.isEmpty());
                return isEmpty ? parseLiteralArgument(rawArgument) : value;
            case "int":
                return toInt(value);
            case "float":
                return toDouble(value);
            case "string":
            case "str":
                return String.valueOf(value);
            case "abs":
                if (value instanceof Number number) {
                    return number instanceof Double ? Math.abs(number.doubleValue()) : Math.abs(number.intValue());
                }
                return null;
            case "round":
                return round(value, rawArgument);
            case "join":
                if (value instanceof List<?> list) {
                    String separator = rawArgument != null ? stripQuotes(rawArgument) : "";
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        if (i > 0) {
                            sb.append(separator);
                        }
                        sb.append(list.get(i));
                    }
                    return sb.toString();
                }
                return null;
            default:
                return null;
        }
    }

    private static Object length(Object value) {
        if (value instanceof List<?> list) {
            return list.size();
        }
        if (value instanceof Map<?, ?> map) {
            return map.size();
        }
        if (value instanceof String s) {
            return s.length();
        }
        return null;
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }

    private static String title(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(' ');
            }
            result.append(capitalize(words[i]));
        }
        return result.toString();
    }

    private static Object toInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static Object toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private static Object round(Object value, String rawArgument) {
        if (!(value instanceof Number number)) {
            return null;
        }
        int digits = 0;
        if (rawArgument != null) {
            try {
                digits = Integer.parseInt(rawArgument.trim());
            } catch (NumberFormatException ignored) {
                digits = 0;
            }
        }
        double factor = Math.pow(10, digits);
        double rounded = Math.round(number.doubleValue() * factor) / factor;
        return digits == 0 ? (Object) (int) rounded : rounded;
    }

    private static Object parseLiteralArgument(String rawArgument) {
        return rawArgument == null ? null : stripQuotes(rawArgument.trim());
    }

    private static String stripQuotes(String text) {
        if (text.length() >= 2 && (text.charAt(0) == '\'' || text.charAt(0) == '"')
                && text.charAt(text.length() - 1) == text.charAt(0)) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }
}
