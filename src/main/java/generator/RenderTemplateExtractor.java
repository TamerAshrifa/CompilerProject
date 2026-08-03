package generator;

import java.util.HashMap;
import java.util.Map;

/**
 * Extracts render_template() call arguments from Python code.
 * 
 * Identifies function calls to render_template() and extracts
 * the template name and keyword arguments that provide context variables.
 * 
 * Example Python code:
 *   return render_template(
 *       "index.html",
 *       name=name,
 *       age=age,
 *       items=get_items()
 *   )
 * 
 * Extracted:
 *   - templateName: "index.html"
 *   - arguments: { "name": <value>, "age": <value>, "items": <value> }
 */
public class RenderTemplateExtractor {
    
    private String templateName;
    private final Map<String, Object> arguments;
    private final Context context;
    
    /**
     * Create an extractor with an empty context.
     */
    public RenderTemplateExtractor() {
        this.context = new Context();
        this.arguments = new HashMap<>();
        this.templateName = null;
    }
    
    /**
     * Create an extractor with a context for resolving variable references.
     * 
     * @param context Context containing known variables
     */
    public RenderTemplateExtractor(Context context) {
        this.context = context != null ? context : new Context();
        this.arguments = new HashMap<>();
        this.templateName = null;
    }
    
    /**
     * Register the template name from a render_template() call.
     * 
     * The first positional argument is typically the template file name.
     * 
     * @param name The template file name (e.g., "index.html")
     */
    public void setTemplateName(String name) {
        this.templateName = name;
    }
    
    /**
     * Register a keyword argument from render_template() call.
     * 
     * Example: name=name, age=age, items=get_items()
     * 
     * @param paramName The parameter name (e.g., "name", "age", "items")
     * @param paramValue The parameter value (variable reference or evaluated expression)
     */
    public void addArgument(String paramName, Object paramValue) {
        if (paramName == null || paramName.isEmpty()) {
            return;
        }
        
        Object resolvedValue = resolveValue(paramValue);
        arguments.put(paramName, resolvedValue);
    }
    
    /**
     * Resolve a parameter value.
     * 
     * If the value is a variable reference, looks it up in the context.
     * Otherwise returns the value as-is.
     * 
     * @param value The parameter value
     * @return The resolved value
     */
    private Object resolveValue(Object value) {
        if (value == null) {
            return null;
        }
        
        // If it's a string that matches a variable in context, resolve it
        if (value instanceof String) {
            String strValue = (String) value;
            if (context.has(strValue)) {
                return context.get(strValue);
            }
        }
        
        // Return value as-is (literal or expression result)
        return value;
    }
    
    /**
     * Add multiple keyword arguments at once.
     * 
     * @param args Map of parameter name → value
     */
    public void addArguments(Map<String, Object> args) {
        if (args == null) {
            return;
        }
        
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            addArgument(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Get the extracted template name.
     * 
     * @return Template file name (e.g., "index.html")
     */
    public String getTemplateName() {
        return templateName;
    }
    
    /**
     * Get all extracted arguments.
     * 
     * @return Map of parameter name → value
     */
    public Map<String, Object> getArguments() {
        return new HashMap<>(arguments);
    }
    
    /**
     * Get a specific argument.
     * 
     * @param paramName The parameter name
     * @return The parameter value, or null if not found
     */
    public Object getArgument(String paramName) {
        return arguments.get(paramName);
    }
    
    /**
     * Check if an argument was provided.
     * 
     * @param paramName The parameter name
     * @return true if argument exists
     */
    public boolean hasArgument(String paramName) {
        return arguments.containsKey(paramName);
    }
    
    /**
     * Get the number of arguments.
     * 
     * @return Number of keyword arguments
     */
    public int getArgumentCount() {
        return arguments.size();
    }
    
    /**
     * Build a Context from the extracted arguments.
     * 
     * This context can be used by the Template AST transformer
     * to substitute Jinja2 variables with their concrete values.
     * 
     * @return Context with all extracted arguments
     */
    public Context buildContext() {
        return new Context(new HashMap<>(arguments));
    }
    
    /**
     * Clear all extracted data.
     */
    public void clear() {
        templateName = null;
        arguments.clear();
    }
    
    /**
     * Check if template and arguments have been extracted.
     * 
     * @return true if both template name and at least one argument exist
     */
    public boolean hasCompleteRenderCall() {
        return templateName != null && !arguments.isEmpty();
    }
    
    @Override
    public String toString() {
        return "RenderTemplateExtractor{" +
                "templateName='" + templateName + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
