package generator;

/**
 * Evaluates a raw Jinja2 condition string (e.g. {@code "user.is_admin and count > 0"})
 * against a {@link Context}. Returns {@code null} when the condition cannot be
 * fully determined (some referenced variable is unknown), in which case the
 * generator should leave the {@code {% if %}} structure in place rather than
 * guessing.
 */
public final class JinjaConditionEvaluator {

    private JinjaConditionEvaluator() {
    }

    public static Boolean evaluate(String condition, Context context) {
        if (condition == null || condition.isBlank()) {
            return null;
        }
        try {
            Parser parser = new Parser(condition, context);
            Object result = parser.parseOr();
            parser.skipWhitespace();
            if (!parser.atEnd() || result == DataFlowAnalyzer.UNKNOWN) {
                return null;
            }
            return PythonTruth.isTruthy(result);
        } catch (RuntimeException malformed) {
            return null;
        }
    }

    /**
     * Prefers evaluating the structured expression tree (see {@link
     * JinjaTreeEvaluator}) when one is available, since it walks the exact
     * nodes ANTLR already parsed instead of re-parsing {@code fallbackText}
     * from scratch. Falls back to {@link #evaluate(String, Context)} only
     * when {@code tree} is {@code null} (e.g. a hand-built AST from a test
     * that never went through {@code TemplateASTBuilder}).
     */
    public static Boolean evaluate(template.ast.jinja.JinjaNode tree, String fallbackText, Context context) {
        if (tree != null) {
            return JinjaTreeEvaluator.isResolvable(tree, context)
                    ? PythonTruth.isTruthy(JinjaTreeEvaluator.resolve(tree, context))
                    : null;
        }
        return evaluate(fallbackText, context);
    }

    private static boolean isIdentStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private static boolean isIdentChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '.' || c == '[' || c == ']' || c == '\'' || c == '"';
    }

    private static final class Parser {
        private final String text;
        private final Context context;
        private int pos;

        Parser(String text, Context context) {
            this.text = text;
            this.context = context;
            this.pos = 0;
        }

        boolean atEnd() {
            return pos >= text.length();
        }

        void skipWhitespace() {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }

        private boolean tryConsumeKeyword(String keyword) {
            skipWhitespace();
            int end = pos + keyword.length();
            if (end <= text.length() && text.regionMatches(pos, keyword, 0, keyword.length())
                    && (end == text.length() || !Character.isLetterOrDigit(text.charAt(end)) && text.charAt(end) != '_')) {
                pos = end;
                return true;
            }
            return false;
        }

        Object parseOr() {
            Object left = parseAnd();
            while (true) {
                int save = pos;
                if (tryConsumeKeyword("or")) {
                    Object right = parseAnd();
                    left = combineOr(left, right);
                } else {
                    pos = save;
                    break;
                }
            }
            return left;
        }

        private Object combineOr(Object left, Object right) {
            if (left != DataFlowAnalyzer.UNKNOWN && PythonTruth.isTruthy(left)) {
                return true;
            }
            if (left == DataFlowAnalyzer.UNKNOWN || right == DataFlowAnalyzer.UNKNOWN) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            return PythonTruth.isTruthy(right);
        }

        Object parseAnd() {
            Object left = parseNot();
            while (true) {
                int save = pos;
                if (tryConsumeKeyword("and")) {
                    Object right = parseNot();
                    left = combineAnd(left, right);
                } else {
                    pos = save;
                    break;
                }
            }
            return left;
        }

        private Object combineAnd(Object left, Object right) {
            if (left != DataFlowAnalyzer.UNKNOWN && !PythonTruth.isTruthy(left)) {
                return false;
            }
            if (left == DataFlowAnalyzer.UNKNOWN || right == DataFlowAnalyzer.UNKNOWN) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            return PythonTruth.isTruthy(right);
        }

        Object parseNot() {
            int save = pos;
            if (tryConsumeKeyword("not")) {
                Object value = parseNot();
                if (value == DataFlowAnalyzer.UNKNOWN) {
                    return DataFlowAnalyzer.UNKNOWN;
                }
                return !PythonTruth.isTruthy(value);
            }
            pos = save;
            return parseComparison();
        }

        Object parseComparison() {
            Object left = parseAtom();
            skipWhitespace();
            String op = tryConsumeCompOp();
            if (op == null) {
                return left;
            }
            Object right = parseAtom();
            if (left == DataFlowAnalyzer.UNKNOWN || right == DataFlowAnalyzer.UNKNOWN) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            Boolean result = PythonArithmetic.compare(op, left, right);
            return result == null ? DataFlowAnalyzer.UNKNOWN : result;
        }

        private String tryConsumeCompOp() {
            skipWhitespace();
            for (String op : new String[]{"==", "!=", "<=", ">="}) {
                if (text.regionMatches(pos, op, 0, op.length())) {
                    pos += op.length();
                    return op;
                }
            }
            if (pos < text.length() && (text.charAt(pos) == '<' || text.charAt(pos) == '>')) {
                String op = String.valueOf(text.charAt(pos));
                pos++;
                return op;
            }
            if (tryConsumeKeyword("in")) {
                return "in";
            }
            if (tryConsumeKeyword("is")) {
                return "is";
            }
            return null;
        }

        Object parseAtom() {
            skipWhitespace();
            if (atEnd()) {
                throw new IllegalStateException("Unexpected end of condition");
            }
            char c = text.charAt(pos);
            if (c == '(') {
                pos++;
                Object value = parseOr();
                skipWhitespace();
                if (!atEnd() && text.charAt(pos) == ')') {
                    pos++;
                }
                return value;
            }
            if (c == '\'' || c == '"') {
                return parseStringLiteral(c);
            }
            if (Character.isDigit(c)) {
                return parseNumberLiteral();
            }
            if (tryConsumeKeyword("True")) {
                return Boolean.TRUE;
            }
            if (tryConsumeKeyword("False")) {
                return Boolean.FALSE;
            }
            if (tryConsumeKeyword("None")) {
                return null;
            }
            if (isIdentStart(c)) {
                return parseIdentifier();
            }
            throw new IllegalStateException("Unexpected character '" + c + "' in condition");
        }

        private Object parseStringLiteral(char quote) {
            int start = ++pos;
            while (pos < text.length() && text.charAt(pos) != quote) {
                pos++;
            }
            String value = text.substring(start, pos);
            if (pos < text.length()) {
                pos++; // closing quote
            }
            return value;
        }

        private Object parseNumberLiteral() {
            int start = pos;
            while (pos < text.length() && (Character.isDigit(text.charAt(pos)) || text.charAt(pos) == '.')) {
                pos++;
            }
            String numberText = text.substring(start, pos);
            if (numberText.contains(".")) {
                return Double.parseDouble(numberText);
            }
            return Integer.parseInt(numberText);
        }

        private Object parseIdentifier() {
            int start = pos;
            pos++;
            while (pos < text.length() && isIdentChar(text.charAt(pos))) {
                pos++;
            }
            String name = text.substring(start, pos);
            if (!context.isResolvable(name)) {
                return DataFlowAnalyzer.UNKNOWN;
            }
            return context.resolve(name);
        }
    }
}
