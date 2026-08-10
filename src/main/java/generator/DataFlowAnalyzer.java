package generator;

import flask.ast.nodes.Expression;
import flask.ast.nodes.Statement;
import flask.ast.nodes.expressions.access.AttributeAccessNode;
import flask.ast.nodes.expressions.access.FunctionCallNode;
import flask.ast.nodes.expressions.access.SubscriptNode;
import flask.ast.nodes.expressions.atoms.DictNode;
import flask.ast.nodes.expressions.atoms.IdentifierNode;
import flask.ast.nodes.expressions.atoms.LiteralNode;
import flask.ast.nodes.expressions.atoms.ListNode;
import flask.ast.nodes.expressions.atoms.SetNode;
import flask.ast.nodes.expressions.atoms.TupleNode;
import flask.ast.nodes.expressions.operations.BinaryOpNode;
import flask.ast.nodes.expressions.operations.CompareNode;
import flask.ast.nodes.expressions.operations.UnaryOpNode;
import flask.ast.nodes.helpers.Parameter;
import flask.ast.nodes.statements.ProgramNode;
import flask.ast.nodes.statements.compound.ForStatementNode;
import flask.ast.nodes.statements.compound.FunctionDefNode;
import flask.ast.nodes.statements.compound.IfStatementNode;
import flask.ast.nodes.statements.simple.AssignmentNode;
import flask.ast.visitor.ASTBaseVisitor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Flow-sensitive analysis of the Python AST.
 *
 * <p>Unlike a plain top-to-bottom scan of top-level statements, this visitor:</p>
 * <ul>
 *   <li>Recurses into function bodies, if/elif/else branches, for/while/with/try
 *       blocks, so it finds assignments and {@code render_template()} calls no
 *       matter how deeply they are nested (e.g. inside a Flask view function).</li>
 *   <li>Tracks a variable's value as it is threaded through simple assignment,
 *       dict/list/attribute mutation, and (for a handful of well-known builtins)
 *       function calls.</li>
 *   <li>Merges values conservatively across branches: if two branches disagree
 *       on a variable's value the variable becomes {@link #UNKNOWN} rather than
 *       silently keeping a stale value.</li>
 *   <li>Records every {@code render_template(...)} call site together with the
 *       context ({@code Map<String,Object>}) that reaches it at that point in
 *       the program, so multiple routes/templates are all captured.</li>
 * </ul>
 */
public class DataFlowAnalyzer extends ASTBaseVisitor<Object> {

    /** Sentinel for a value that could not be statically determined. */
    public static final Object UNKNOWN = new Object() {
        @Override
        public String toString() {
            return "<unresolved>";
        }
    };

    /** One {@code render_template(...)} call site plus the context reaching it. */
    public static final class RenderCall {
        private final String templateName;
        private final Map<String, Object> arguments;

        public RenderCall(String templateName, Map<String, Object> arguments) {
            this.templateName = templateName;
            this.arguments = arguments;
        }

        public String getTemplateName() {
            return templateName;
        }

        public Map<String, Object> getArguments() {
            return arguments;
        }
    }

    private final Map<String, Object> globals = new LinkedHashMap<>();
    private Map<String, Object> locals; // null while at module scope
    private final List<RenderCall> renderCalls = new ArrayList<>();

    public void analyze(ProgramNode program) {
        if (program == null) {
            return;
        }
        program.accept(this);
    }

    public List<RenderCall> getRenderCalls() {
        return renderCalls;
    }

    public Map<String, Object> getModuleVariables() {
        return exportable(globals);
    }

    /**
     * Drops UNKNOWN entries (recursively, inside nested dicts/lists too);
     * None (Java null) values are kept as legitimate values.
     */
    public static Map<String, Object> exportable(Map<String, Object> scope) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : scope.entrySet()) {
            Object sanitized = sanitizeValue(entry.getValue());
            if (sanitized != UNKNOWN) {
                result.put(entry.getKey(), sanitized);
            }
        }
        return result;
    }

    /** Recursively strips {@link #UNKNOWN} markers out of dict/list values. */
    public static Object sanitizeValue(Object value) {
        if (value == UNKNOWN) {
            return UNKNOWN;
        }
        if (value instanceof Map<?, ?> rawMap) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                Object sanitized = sanitizeValue(entry.getValue());
                if (sanitized != UNKNOWN) {
                    result.put(String.valueOf(entry.getKey()), sanitized);
                }
            }
            return result;
        }
        if (value instanceof List<?> rawList) {
            List<Object> result = new ArrayList<>();
            for (Object item : rawList) {
                Object sanitized = sanitizeValue(item);
                result.add(sanitized == UNKNOWN ? null : sanitized);
            }
            return result;
        }
        return value;
    }

    // ------------------------------------------------------------------
    // Scope helpers
    // ------------------------------------------------------------------

    private Map<String, Object> currentScope() {
        return locals != null ? locals : globals;
    }

    private Object lookup(String name) {
        if (locals != null && locals.containsKey(name)) {
            return locals.get(name);
        }
        if (globals.containsKey(name)) {
            return globals.get(name);
        }
        return UNKNOWN;
    }

    private void assignSimple(String name, Object value) {
        currentScope().put(name, value);
    }

    private void visitStatements(List<Statement> statements) {
        for (Statement statement : statements) {
            statement.accept(this);
        }
    }

    // ------------------------------------------------------------------
    // Compound statements needing custom flow handling
    // ------------------------------------------------------------------

    @Override
    public Object visitFunctionDef(FunctionDefNode node) {
        Map<String, Object> previousLocals = locals;
        Map<String, Object> newLocals = new LinkedHashMap<>();
        for (Parameter parameter : node.getParameters()) {
            Object value = UNKNOWN;
            if (parameter.hasDefault()) {
                Object resolved = parameter.getDefaultValue().accept(this);
                if (resolved != null) {
                    value = resolved;
                }
            }
            newLocals.put(parameter.getName(), value);
        }
        locals = newLocals;
        visitStatements(node.getBody());
        locals = previousLocals;
        return null;
    }

    @Override
    public Object visitIfStatement(IfStatementNode node) {
        node.getCondition().accept(this);

        Map<String, Object> before = new LinkedHashMap<>(currentScope());
        List<Map<String, Object>> branchOutcomes = new ArrayList<>();

        branchOutcomes.add(runBranch(before, node.getThenBody()));
        for (IfStatementNode.ElifClause elif : node.getElifClauses()) {
            elif.getCondition().accept(this);
            branchOutcomes.add(runBranch(before, elif.getBody()));
        }
        if (node.hasElse()) {
            branchOutcomes.add(runBranch(before, node.getElseBody()));
        } else {
            // The if/elif chain might not execute at all.
            branchOutcomes.add(before);
        }

        mergeInto(currentScope(), before, branchOutcomes);
        return null;
    }

    @Override
    public Object visitForStatement(ForStatementNode node) {
        node.getIterable().accept(this);

        Map<String, Object> before = new LinkedHashMap<>(currentScope());
        if (node.getTarget() instanceof IdentifierNode loopVar) {
            currentScope().put(loopVar.getName(), UNKNOWN);
        }

        visitStatements(node.getBody());
        if (node.hasElse()) {
            visitStatements(node.getElseBody());
        }
        Map<String, Object> afterLoopBody = new LinkedHashMap<>(currentScope());

        // The loop may execute zero or many times: merge "never ran" (before)
        // with "ran at least once" (afterLoopBody) conservatively.
        mergeInto(currentScope(), before, List.of(before, afterLoopBody));
        return null;
    }

    private Map<String, Object> runBranch(Map<String, Object> before, List<Statement> body) {
        Map<String, Object> scope = currentScope();
        scope.clear();
        scope.putAll(before);
        visitStatements(body);
        return new LinkedHashMap<>(scope);
    }

    private void mergeInto(Map<String, Object> target, Map<String, Object> before, List<Map<String, Object>> outcomes) {
        java.util.LinkedHashSet<String> keys = new java.util.LinkedHashSet<>(before.keySet());
        for (Map<String, Object> outcome : outcomes) {
            keys.addAll(outcome.keySet());
        }
        target.clear();
        for (String key : keys) {
            Object first = outcomes.get(0).getOrDefault(key, UNKNOWN);
            boolean agree = true;
            for (Map<String, Object> outcome : outcomes) {
                Object value = outcome.getOrDefault(key, UNKNOWN);
                if (!Objects.equals(value, first)) {
                    agree = false;
                    break;
                }
            }
            target.put(key, agree ? first : UNKNOWN);
        }
    }

    // ------------------------------------------------------------------
    // Assignment (identifier / attribute / subscript targets)
    // ------------------------------------------------------------------

    @Override
    public Object visitAssignment(AssignmentNode node) {
        Object value = node.getValue().accept(this);
        assignTo(node.getTarget(), value);
        return null;
    }

    private void assignTo(Expression target, Object value) {
        if (target instanceof IdentifierNode identifierNode) {
            assignSimple(identifierNode.getName(), value);
            return;
        }
        if (target instanceof AttributeAccessNode attributeAccessNode) {
            Map<String, Object> container = resolveOrCreateMapContainer(attributeAccessNode.getTarget());
            if (container != null) {
                container.put(attributeAccessNode.getAttribute(), value);
            }
            return;
        }
        if (target instanceof SubscriptNode subscriptNode) {
            Expression indexExpr = subscriptNode.getIndex();
            Object key = indexExpr != null ? indexExpr.accept(this) : null;
            Object container = resolveOrCreateIndexableContainer(subscriptNode.getTarget());
            if (container instanceof List<?> rawList && key instanceof Number number) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) rawList;
                int index = number.intValue();
                while (list.size() <= index) {
                    list.add(null);
                }
                list.set(index, value);
            } else if (container instanceof Map<?, ?> rawMap) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) rawMap;
                map.put(String.valueOf(key), value);
            }
        }
    }

    /**
     * Resolves the object that {@code target.attribute = value} should write into,
     * creating an empty dict-like container along the way if needed so that
     * property assignment works even when the base object was not previously a
     * known dict (best-effort object-as-dict model).
     */
    private Map<String, Object> resolveOrCreateMapContainer(Expression target) {
        if (target instanceof IdentifierNode identifierNode) {
            Object current = lookup(identifierNode.getName());
            Map<String, Object> map = asMutableMap(current);
            assignSimple(identifierNode.getName(), map);
            return map;
        }
        if (target instanceof AttributeAccessNode attributeAccessNode) {
            Map<String, Object> parent = resolveOrCreateMapContainer(attributeAccessNode.getTarget());
            if (parent == null) {
                return null;
            }
            Map<String, Object> child = asMutableMap(parent.get(attributeAccessNode.getAttribute()));
            parent.put(attributeAccessNode.getAttribute(), child);
            return child;
        }
        if (target instanceof SubscriptNode subscriptNode) {
            Object container = resolveOrCreateIndexableContainer(subscriptNode.getTarget());
            Expression indexExpr = subscriptNode.getIndex();
            Object key = indexExpr != null ? indexExpr.accept(this) : null;
            if (container instanceof Map<?, ?> rawMap) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) rawMap;
                Map<String, Object> child = asMutableMap(map.get(String.valueOf(key)));
                map.put(String.valueOf(key), child);
                return child;
            }
        }
        return null;
    }

    private Object resolveOrCreateIndexableContainer(Expression target) {
        if (target instanceof IdentifierNode identifierNode) {
            Object current = lookup(identifierNode.getName());
            if (!(current instanceof List<?>) && !(current instanceof Map<?, ?>)) {
                current = new LinkedHashMap<String, Object>();
            }
            assignSimple(identifierNode.getName(), current);
            return current;
        }
        Object value = target.accept(this);
        if (value instanceof List<?> || value instanceof Map<?, ?>) {
            return value;
        }
        return null;
    }

    private Map<String, Object> asMutableMap(Object value) {
        if (value instanceof Map<?, ?> rawMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            return map;
        }
        return new LinkedHashMap<>();
    }

    // ------------------------------------------------------------------
    // Expressions -> resolved runtime values (best effort)
    // ------------------------------------------------------------------

    @Override
    public Object visitLiteral(LiteralNode node) {
        return node.getValue();
    }

    @Override
    public Object visitIdentifier(IdentifierNode node) {
        return lookup(node.getName());
    }

    @Override
    public Object visitDict(DictNode node) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (DictNode.DictItem item : node.getItems()) {
            Object keyValue = item.getKey().accept(this);
            Object valueValue = item.getValue().accept(this);
            if (keyValue != UNKNOWN) {
                result.put(String.valueOf(keyValue), valueValue == UNKNOWN ? UNKNOWN : valueValue);
            }
        }
        return result;
    }

    @Override
    public Object visitList(ListNode node) {
        List<Object> result = new ArrayList<>();
        for (Expression element : node.getElements()) {
            result.add(element.accept(this));
        }
        return result;
    }

    @Override
    public Object visitSet(SetNode node) {
        List<Object> result = new ArrayList<>();
        for (Expression element : node.getElements()) {
            result.add(element.accept(this));
        }
        return result;
    }

    @Override
    public Object visitTuple(TupleNode node) {
        List<Object> result = new ArrayList<>();
        for (Expression element : node.getElements()) {
            result.add(element.accept(this));
        }
        return result;
    }

    @Override
    public Object visitAttributeAccess(AttributeAccessNode node) {
        Object target = node.getTarget().accept(this);
        if (target instanceof Map<?, ?> map) {
            if (map.containsKey(node.getAttribute())) {
                return map.get(node.getAttribute());
            }
        }
        return UNKNOWN;
    }

    @Override
    public Object visitSubscript(SubscriptNode node) {
        Object target = node.getTarget().accept(this);
        Expression indexExpr = node.getIndex();
        if (indexExpr == null) {
            return UNKNOWN;
        }
        Object index = indexExpr.accept(this);
        if (target instanceof List<?> list && index instanceof Number number) {
            int i = number.intValue();
            if (i >= 0 && i < list.size()) {
                return list.get(i);
            }
            return UNKNOWN;
        }
        if (target instanceof Map<?, ?> map) {
            String key = String.valueOf(index);
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return UNKNOWN;
    }

    @Override
    public Object visitUnaryOp(UnaryOpNode node) {
        Object operand = node.getOperand().accept(this);
        if (operand == UNKNOWN) {
            return UNKNOWN;
        }
        return switch (node.getOperator()) {
            case "-" -> negate(operand);
            case "not" -> !PythonTruth.isTruthy(operand);
            default -> operand;
        };
    }

    private Object negate(Object value) {
        if (value instanceof Integer i) {
            return -i;
        }
        if (value instanceof Double d) {
            return -d;
        }
        if (value instanceof Long l) {
            return -l;
        }
        return UNKNOWN;
    }

    @Override
    public Object visitBinaryOp(BinaryOpNode node) {
        Object left = node.getLeft().accept(this);
        Object right = node.getRight().accept(this);
        if (left == UNKNOWN || right == UNKNOWN) {
            return UNKNOWN;
        }
        return PythonArithmetic.apply(node.getOperator(), left, right);
    }

    @Override
    public Object visitCompare(CompareNode node) {
        Object left = node.getLeft().accept(this);
        List<String> operators = node.getOperators();
        List<Expression> comparators = node.getComparators();
        for (int i = 0; i < operators.size(); i++) {
            Object right = comparators.get(i).accept(this);
            if (left == UNKNOWN || right == UNKNOWN) {
                return UNKNOWN;
            }
            Boolean result = PythonArithmetic.compare(operators.get(i), left, right);
            if (result == null) {
                return UNKNOWN;
            }
            if (!result) {
                return false;
            }
            left = right;
        }
        return true;
    }

    @Override
    public Object visitFunctionCall(FunctionCallNode node) {
        String calleeName = calleeName(node);

        if ("render_template".equals(calleeName)) {
            recordRenderCall(node);
            return UNKNOWN;
        }

        List<Object> args = new ArrayList<>();
        for (Expression arg : node.getArgs()) {
            args.add(arg.accept(this));
        }
        Map<String, Object> kwargs = new LinkedHashMap<>();
        for (Map.Entry<String, Expression> entry : node.getKwargs().entrySet()) {
            kwargs.put(entry.getKey(), entry.getValue().accept(this));
        }
        // Visit spreads for their side effects (nested render_template calls, etc.)
        for (Expression spread : node.getStarArgs()) {
            spread.accept(this);
        }
        for (Expression spread : node.getKwargsSpread()) {
            Object value = spread.accept(this);
            if (value instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    kwargs.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
        }

        return simulateBuiltin(calleeName, args, kwargs);
    }

    /** Best-effort simulation of a handful of common builtins; everything else is UNKNOWN. */
    private Object simulateBuiltin(String calleeName, List<Object> args, Map<String, Object> kwargs) {
        if (calleeName == null) {
            return UNKNOWN;
        }
        switch (calleeName) {
            case "dict": {
                Map<String, Object> result = new LinkedHashMap<>(kwargs);
                if (!args.isEmpty() && args.get(0) instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        result.put(String.valueOf(entry.getKey()), entry.getValue());
                    }
                }
                return result;
            }
            case "list":
                if (args.isEmpty()) {
                    return new ArrayList<>();
                }
                if (args.get(0) instanceof List<?> list) {
                    return new ArrayList<>(list);
                }
                return UNKNOWN;
            case "str":
                return args.isEmpty() ? "" : String.valueOf(args.get(0));
            case "int":
                if (!args.isEmpty()) {
                    try {
                        return Integer.parseInt(String.valueOf(args.get(0)).trim());
                    } catch (NumberFormatException ignored) {
                        return UNKNOWN;
                    }
                }
                return 0;
            case "float":
                if (!args.isEmpty()) {
                    try {
                        return Double.parseDouble(String.valueOf(args.get(0)).trim());
                    } catch (NumberFormatException ignored) {
                        return UNKNOWN;
                    }
                }
                return 0.0;
            case "len":
                if (!args.isEmpty()) {
                    Object value = args.get(0);
                    if (value instanceof List<?> list) {
                        return list.size();
                    }
                    if (value instanceof Map<?, ?> map) {
                        return map.size();
                    }
                    if (value instanceof String s) {
                        return s.length();
                    }
                }
                return UNKNOWN;
            default:
                return UNKNOWN;
        }
    }

    private void recordRenderCall(FunctionCallNode call) {
        String templateName = null;
        List<Expression> args = call.getArgs();
        Map<String, Object> arguments = new LinkedHashMap<>();

        if (!args.isEmpty()) {
            Expression first = args.get(0);
            Object firstValue = first.accept(this);
            if (first instanceof LiteralNode literalNode && literalNode.getValue() != null) {
                templateName = String.valueOf(literalNode.getValue());
            } else if (firstValue instanceof String s) {
                templateName = s;
            }
            // Extra positional arguments (besides the template name) that resolve
            // to dictionaries are merged in as context, e.g. render_template(name, context_dict).
            for (int i = 1; i < args.size(); i++) {
                Object value = args.get(i).accept(this);
                if (value instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        arguments.put(String.valueOf(entry.getKey()), entry.getValue());
                    }
                }
            }
        }

        for (Map.Entry<String, Expression> entry : call.getKwargs().entrySet()) {
            arguments.put(entry.getKey(), entry.getValue().accept(this));
        }

        for (Expression spread : call.getKwargsSpread()) {
            Object value = spread.accept(this);
            if (value instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    arguments.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
        }

        renderCalls.add(new RenderCall(templateName, exportable(arguments)));
    }

    private String calleeName(FunctionCallNode node) {
        Expression callee = node.getFunction();
        if (callee instanceof IdentifierNode identifierNode) {
            return identifierNode.getName();
        }
        if (callee instanceof AttributeAccessNode attributeAccessNode) {
            return attributeAccessNode.getAttribute();
        }
        return null;
    }
}
