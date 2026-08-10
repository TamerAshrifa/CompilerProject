package generator;

import java.util.List;
import java.util.Map;
import template.ast.jinja.JinjaAttributeAccessNode;
import template.ast.jinja.JinjaBinaryOpNode;
import template.ast.jinja.JinjaCompareNode;
import template.ast.jinja.JinjaFilterApplicationNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaIdentifierNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaSubscriptNode;
import template.ast.jinja.JinjaUnaryOpNode;
import template.ast.jinja.LiteralNode;

/**
 * Statically evaluates a structured Jinja2 expression tree - see {@code
 * JinjaExpressionNode#getRoot()}, {@code JinjaIfNode#getConditionTree()}
 * and {@code JinjaForNode#getIterableTree()} - against a {@link Context} of
 * values extracted from Python, by walking the real nested nodes built by
 * {@code TemplateASTBuilder} instead of re-parsing flattened source text.
 *
 * This is what lets the Generator resolve expressions more complex than a
 * plain "name(.attr|[idx])*" chain, e.g. "{{ price * quantity }}" or
 * "{{ items[0].name }}", using the exact same tree ANTLR already built
 * rather than a second, independent text-based parser.
 *
 * Mirrors the isResolvable/resolve split already used by {@link Context}
 * for its own string-path resolution: Python/Jinja values can legitimately
 * be {@code null} ({@code None}), so "the value is null" must stay
 * distinguishable from "this could not be determined statically". Callers
 * should check {@link #isResolvable} before trusting a {@link #resolve}
 * result of {@code null}.
 */
public final class JinjaTreeEvaluator {

    private JinjaTreeEvaluator() {
    }

    public static boolean isResolvable(JinjaNode node, Context context) {
        return tryResolve(node, context) != DataFlowAnalyzer.UNKNOWN;
    }

    public static Object resolve(JinjaNode node, Context context) {
        Object result = tryResolve(node, context);
        return result == DataFlowAnalyzer.UNKNOWN ? null : result;
    }

    private static Object tryResolve(JinjaNode node, Context context) {
        if (node == null) {
            return DataFlowAnalyzer.UNKNOWN;
        }
        if (node instanceof LiteralNode literal) {
            return literal.getValue();
        }
        if (node instanceof JinjaIdentifierNode identifier) {
            return context.has(identifier.getName())
                    ? context.resolve(identifier.getName())
                    : DataFlowAnalyzer.UNKNOWN;
        }
        if (node instanceof JinjaAttributeAccessNode attributeAccess) {
            Object base = tryResolve(attributeAccess.getObject(), context);
            if (base == DataFlowAnalyzer.UNKNOWN || !(base instanceof Map<?, ?> map)) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            return map.containsKey(attributeAccess.getAttributeName())
                    ? map.get(attributeAccess.getAttributeName())
                    : DataFlowAnalyzer.UNKNOWN;
        }
        if (node instanceof JinjaSubscriptNode subscript) {
            Object base = tryResolve(subscript.getObject(), context);
            Object index = tryResolve(subscript.getIndex(), context);
            if (base == DataFlowAnalyzer.UNKNOWN || index == DataFlowAnalyzer.UNKNOWN) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            return resolveIndexed(base, index);
        }
        if (node instanceof JinjaUnaryOpNode unaryOp) {
            Object operand = tryResolve(unaryOp.getOperand(), context);
            if (operand == DataFlowAnalyzer.UNKNOWN) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            return applyUnary(unaryOp.getOperator(), operand);
        }
        if (node instanceof JinjaBinaryOpNode binaryOp) {
            return tryResolveBinaryOp(binaryOp, context);
        }
        if (node instanceof JinjaCompareNode compare) {
            Object left = tryResolve(compare.getLeft(), context);
            Object right = tryResolve(compare.getRight(), context);
            if (left == DataFlowAnalyzer.UNKNOWN || right == DataFlowAnalyzer.UNKNOWN) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            Boolean result = PythonArithmetic.compare(compare.getOperator(), left, right);
            return result != null ? result : DataFlowAnalyzer.UNKNOWN;
        }
        if (node instanceof JinjaFilterApplicationNode filterApplication) {
            Object value = tryResolve(filterApplication.getTarget(), context);
            if (value == DataFlowAnalyzer.UNKNOWN) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            for (JinjaFilterNode filter : filterApplication.getFilters()) {
                if (!JinjaFilters.isKnown(filter.getFilterName())) {
                    return DataFlowAnalyzer.UNKNOWN;
                }
                value = JinjaFilters.apply(filter.getFilterName(), filter.getArguments(), value);
                if (value == null) {
                    return DataFlowAnalyzer.UNKNOWN;
                }
            }
            return value;
        }
        // JinjaCallNode (arbitrary calls) and anything else: not statically evaluable.
        return DataFlowAnalyzer.UNKNOWN;
    }

    private static Object tryResolveBinaryOp(JinjaBinaryOpNode binaryOp, Context context) {
        String operator = binaryOp.getOperator();
        if ("or".equals(operator) || "and".equals(operator)) {
            Object left = tryResolve(binaryOp.getLeft(), context);
            if (left == DataFlowAnalyzer.UNKNOWN) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            boolean leftTruthy = PythonTruth.isTruthy(left);
            boolean shortCircuits = ("or".equals(operator) && leftTruthy) || ("and".equals(operator) && !leftTruthy);
            return shortCircuits ? left : tryResolve(binaryOp.getRight(), context);
        }
        Object left = tryResolve(binaryOp.getLeft(), context);
        Object right = tryResolve(binaryOp.getRight(), context);
        if (left == DataFlowAnalyzer.UNKNOWN || right == DataFlowAnalyzer.UNKNOWN) {
            return DataFlowAnalyzer.UNKNOWN;
        }
        if ("~".equals(operator)) {
            // Jinja's string-concatenation operator.
            return String.valueOf(left) + String.valueOf(right);
        }
        return PythonArithmetic.apply(operator, left, right);
    }

    private static Object applyUnary(String operator, Object operand) {
        if ("not".equals(operator)) {
            return !PythonTruth.isTruthy(operand);
        }
        if ("-".equals(operator)) {
            if (operand instanceof Integer intValue) {
                return -intValue;
            }
            if (operand instanceof Double doubleValue) {
                return -doubleValue;
            }
        }
        return DataFlowAnalyzer.UNKNOWN;
    }

    private static Object resolveIndexed(Object base, Object index) {
        if (base instanceof List<?> values && index instanceof Integer intIndex) {
            if (intIndex >= 0 && intIndex < values.size()) {
                return values.get(intIndex);
            }
            return DataFlowAnalyzer.UNKNOWN;
        }
        if (base instanceof Map<?, ?> map) {
            return map.containsKey(index) ? map.get(index) : DataFlowAnalyzer.UNKNOWN;
        }
        return DataFlowAnalyzer.UNKNOWN;
    }
}
