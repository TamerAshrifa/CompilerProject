package generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runtime context for Jinja2 template generation.
 * 
 * Stores variable bindings extracted from Python code.
 * Maps variable names to their runtime values.
 */
public class Context {
    
    private final Map<String, Object> variables;
    
    /**
     * Create an empty context.
     */
    public Context() {
        this.variables = new HashMap<>();
    }
    
    /**
     * Create a context with initial variables.
     */
    public Context(Map<String, Object> initialVariables) {
        Objects.requireNonNull(initialVariables, "initialVariables cannot be null");
        this.variables = new HashMap<>(initialVariables);
    }
    
    /**
     * Set a variable in the context.
     * 
     * @param name Variable name (e.g., "name", "age")
     * @param value Variable value (String, Integer, Boolean, etc.)
     */
    public void set(String name, Object value) {
        Objects.requireNonNull(name, "Variable name cannot be null");
        variables.put(name, value);
    }
    
    /**
     * Get a variable from the context.
     * 
     * @param name Variable name
     * @return Variable value, or null if not found
     */
    public Object get(String name) {
        return resolve(name);
    }

    /**
     * Resolve a variable reference that may include nested access such as
     * user.name, config.title, or items[0].
     */
    public Object resolve(String expression) {
        if (expression == null) {
            return null;
        }

        String trimmed = expression.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        if (variables.containsKey(trimmed)) {
            return variables.get(trimmed);
        }

        Pattern pattern = Pattern.compile("([^.\\[\\]]+)|\\[([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(trimmed);
        List<String> parts = new ArrayList<>();
        List<Boolean> isBracketAccess = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
                isBracketAccess.add(false);
            } else {
                parts.add(matcher.group(2));
                isBracketAccess.add(true);
            }
        }

        if (parts.isEmpty()) {
            return null;
        }

        Object current = variables.get(parts.get(0));
        if (current == null && !variables.containsKey(parts.get(0))) {
            return null;
        }

        for (int i = 1; i < parts.size(); i++) {
            String part = parts.get(i);
            boolean bracket = isBracketAccess.get(i);
            if (bracket) {
                current = resolveIndex(current, part);
            } else {
                current = resolveAttribute(current, part);
            }
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    /**
     * Like {@link #resolve(String)} but also reports whether the expression
     * could actually be resolved (as opposed to evaluating to {@code null}
     * because it is unknown/unresolved).
     *
     * This matters because a Python variable can legitimately hold the value
     * {@code None}; in that case the Jinja variable should still be replaced
     * with a literal. A variable that data-flow analysis could not determine
     * a value for should instead be left untouched in the template.
     */
    public boolean isResolvable(String expression) {
        if (expression == null) {
            return false;
        }
        String trimmed = expression.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        if (variables.containsKey(trimmed)) {
            return true;
        }

        Pattern pattern = Pattern.compile("([^.\\[\\]]+)|\\[([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(trimmed);
        List<String> parts = new ArrayList<>();
        List<Boolean> isBracketAccess = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
                isBracketAccess.add(false);
            } else {
                parts.add(matcher.group(2));
                isBracketAccess.add(true);
            }
        }

        if (parts.isEmpty() || !variables.containsKey(parts.get(0))) {
            return false;
        }

        Object current = variables.get(parts.get(0));
        for (int i = 1; i < parts.size(); i++) {
            String part = parts.get(i);
            boolean bracket = isBracketAccess.get(i);
            if (current == null) {
                return false;
            }
            if (bracket) {
                if (current instanceof List<?> values) {
                    try {
                        int index = Integer.parseInt(part);
                        if (index < 0 || index >= values.size()) {
                            return false;
                        }
                        current = values.get(index);
                        continue;
                    } catch (NumberFormatException ignored) {
                        // fall through to map lookup below
                    }
                }
                if (current instanceof Map<?, ?> map) {
                    if (!map.containsKey(part)) {
                        return false;
                    }
                    current = map.get(part);
                } else {
                    return false;
                }
            } else {
                if (current instanceof Map<?, ?> map) {
                    if (!map.containsKey(part)) {
                        return false;
                    }
                    current = map.get(part);
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private Object resolveAttribute(Object current, String attribute) {
        if (current == null) {
            return null;
        }
        if (current instanceof Map<?, ?> map) {
            return map.get(attribute);
        }
        return null;
    }

    private Object resolveIndex(Object current, String indexToken) {
        if (current == null) {
            return null;
        }
        if (current instanceof List<?> values) {
            try {
                int index = Integer.parseInt(indexToken);
                if (index >= 0 && index < values.size()) {
                    return values.get(index);
                }
            } catch (NumberFormatException ignored) {
                // Ignore and fall through to map lookup
            }
        }
        if (current instanceof Map<?, ?> map) {
            return map.get(indexToken);
        }
        return null;
    }
    
    /**
     * Check if a variable exists in the context.
     * 
     * @param name Variable name
     * @return true if variable exists
     */
    /**
     * Returns a new Context equal to this one but with {@code name} bound to
     * {@code value}, without mutating this instance. Used to give each
     * unrolled {@code {% for %}} iteration its own loop-variable binding.
     */
    public Context withOverride(String name, Object value) {
        Map<String, Object> copy = new HashMap<>(getAll());
        copy.put(name, value);
        return new Context(copy);
    }

    public boolean has(String name) {
        return variables.containsKey(name);
    }
    
    /**
     * Get all variables in the context (unmodifiable).
     * 
     * @return Map of variable names to values
     */
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(variables);
    }
    
    /**
     * Get the number of variables in the context.
     * 
     * @return Number of variables
     */
    public int size() {
        return variables.size();
    }
    
    /**
     * Clear all variables from the context.
     */
    public void clear() {
        variables.clear();
    }
    
    @Override
    public String toString() {
        return "Context{" + variables + "}";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Context)) return false;
        Context context = (Context) o;
        return Objects.equals(variables, context.variables);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }
}
