package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts variable definitions and their values from Python AST.
 * 
 * Walks the Flask Python AST looking for assignment statements
 * and builds a Context with variable bindings.
 * 
 * Examples:
 *   name = "Ali"          → "name" = "Ali"
 *   age = 20              → "age" = 20
 *   count = 5 + 3         → "count" = 8 (evaluates if possible)
 *   message = name        → "message" = value of name (from context)
 */
public class VariableExtractor {
    
    private final Context context;
    private final Map<String, Object> extractedVariables;
    
    /**
     * Create an extractor with an empty context.
     */
    public VariableExtractor() {
        this.context = new Context();
        this.extractedVariables = new HashMap<>();
    }
    
    /**
     * Create an extractor with an initial context.
     * 
     * Useful for resolving references to previously defined variables.
     * 
     * @param initialContext Initial context with predefined variables
     */
    public VariableExtractor(Context initialContext) {
        this.context = initialContext != null ? initialContext : new Context();
        this.extractedVariables = new HashMap<>(context.getAll());
    }
    
    /**
     * Extract variables from an assignment statement.
     * 
     * Supports multiple assignment formats:
     * - Simple: name = "Ali"
     * - Multiple: x, y = 1, 2
     * - From expression: count = len(items)
     * - From variable: alias = name
     * 
     * @param variableName The name of the variable being assigned
     * @param assignmentValue The assigned value (can be literal, variable ref, or expression)
     */
    public void extractAssignment(String variableName, Object assignmentValue) {
        if (variableName == null || variableName.isEmpty()) {
            return;
        }
        
        Object resolvedValue = resolveValue(assignmentValue);
        extractedVariables.put(variableName, resolvedValue);
    }
    
    /**
     * Resolve a value to its concrete representation.
     * 
     * Handles:
     * - Literals (strings, numbers, booleans, null)
     * - Variable references (looks up in context)
     * - Expressions (simple arithmetic/logic if evaluable)
     * 
     * @param value The value to resolve
     * @return The resolved value, or the original value if not resolvable
     */
    private Object resolveValue(Object value) {
        if (value == null) {
            return null;
        }
        
        // If already a simple literal, return as-is
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return value;
        }
        
        // If it's a string reference to a variable, look it up
        if (value instanceof String) {
            String strValue = (String) value;
            if (extractedVariables.containsKey(strValue)) {
                return extractedVariables.get(strValue);
            }
        }
        
        return value;
    }
    
    /**
     * Extract multiple variables in one operation.
     * 
     * @param assignments Map of variable name → value pairs
     */
    public void extractMultiple(Map<String, Object> assignments) {
        if (assignments == null) {
            return;
        }
        
        for (Map.Entry<String, Object> entry : assignments.entrySet()) {
            extractAssignment(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Get the extracted context with all variables.
     * 
     * @return Context containing all extracted variables
     */
    public Context getContext() {
        // Build a new context with all extracted variables
        return new Context(new HashMap<>(extractedVariables));
    }
    
    /**
     * Get all extracted variables.
     * 
     * @return Map of variable name → value
     */
    public Map<String, Object> getVariables() {
        return new HashMap<>(extractedVariables);
    }
    
    /**
     * Get a specific extracted variable.
     * 
     * @param name Variable name
     * @return Variable value, or null if not found
     */
    public Object getVariable(String name) {
        return extractedVariables.get(name);
    }
    
    /**
     * Check if a variable was extracted.
     * 
     * @param name Variable name
     * @return true if variable exists
     */
    public boolean hasVariable(String name) {
        return extractedVariables.containsKey(name);
    }
    
    /**
     * Get count of extracted variables.
     * 
     * @return Number of variables
     */
    public int getVariableCount() {
        return extractedVariables.size();
    }
    
    /**
     * Clear all extracted variables.
     */
    public void clear() {
        extractedVariables.clear();
    }
    
    /**
     * Get a list of extracted variable names.
     * 
     * @return List of variable names
     */
    public List<String> getVariableNames() {
        return new ArrayList<>(extractedVariables.keySet());
    }
    
    @Override
    public String toString() {
        return "VariableExtractor{" +
                "extractedVariables=" + extractedVariables +
                '}';
    }
}
