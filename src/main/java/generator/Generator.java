package generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import template.ast.TemplateProgramNode;
import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaCommentNode;
import template.ast.jinja.JinjaElifNode;
import template.ast.jinja.JinjaElseNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaExtendsNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaIfNode;
import template.ast.jinja.JinjaIncludeNode;
import template.ast.jinja.JinjaMacroNode;
import template.ast.jinja.JinjaNode;
import template.ast.jinja.JinjaProgramNode;
import template.ast.jinja.JinjaVariableNode;
import template.ast.jinja.LiteralNode;

/**
 * Generator Phase - Transforms Template AST using a Python-derived context.
 *
 * The generator does not parse Python again; it consumes the Python AST,
 * extracts variable bindings from assignments and render_template() calls,
 * builds a runtime Context, and replaces Jinja variable nodes with literal nodes.
 */
public class Generator {

    private final Object pythonAst;
    private final TemplateProgramNode templateAst;
    private final Object symbolTable;

    private Context context;
    private TemplateProgramNode transformedTemplate;
    private String templateName;

    public Generator(Object pythonAst, TemplateProgramNode templateAst, Object symbolTable) {
        this.pythonAst = pythonAst;
        this.templateAst = templateAst;
        this.symbolTable = symbolTable;
        this.context = null;
        this.transformedTemplate = null;
        this.templateName = null;
    }

    public TemplateProgramNode generate() {
        PythonContextExtractor extractor = extractVariablesFromPython();
        RenderTemplateExtractor renderExtractor = extractRenderTemplateCall(extractor);
        buildContext(extractor, renderExtractor);
        transformTemplateAst();
        return transformedTemplate;
    }

    private PythonContextExtractor extractVariablesFromPython() {
        PythonContextExtractor extractor = new PythonContextExtractor();
        if (pythonAst instanceof flask.ast.nodes.statements.ProgramNode programNode) {
            extractor.extract(programNode);
        }
        return extractor;
    }

    private RenderTemplateExtractor extractRenderTemplateCall(PythonContextExtractor extractor) {
        RenderTemplateExtractor renderExtractor = new RenderTemplateExtractor(context);
        if (extractor != null) {
            renderExtractor.setTemplateName(extractor.getTemplateName());
            for (Map.Entry<String, Object> entry : extractor.getRenderArguments().entrySet()) {
                renderExtractor.addArgument(entry.getKey(), entry.getValue());
            }
        }
        return renderExtractor;
    }

    private void buildContext(PythonContextExtractor variableExtractor, RenderTemplateExtractor renderExtractor) {
        this.context = new Context();

        if (variableExtractor != null) {
            for (Map.Entry<String, Object> entry : variableExtractor.getVariables().entrySet()) {
                context.set(entry.getKey(), entry.getValue());
            }
        }

        if (renderExtractor != null) {
            for (Map.Entry<String, Object> arg : renderExtractor.getArguments().entrySet()) {
                context.set(arg.getKey(), arg.getValue());
            }
            this.templateName = renderExtractor.getTemplateName();
        }
    }

    private void transformTemplateAst() {
        if (templateAst == null) {
            this.transformedTemplate = null;
            return;
        }

        if (context == null) {
            this.transformedTemplate = templateAst;
            return;
        }

        TemplateProgramNode result = new TemplateProgramNode();
        for (template.ast.html.HtmlNode htmlNode : templateAst.getHtmlElements()) {
            result.addHtmlElement(htmlNode);
        }

        for (JinjaNode jinjaNode : templateAst.getJinjaElements()) {
            for (JinjaNode transformedNode : transformJinjaNode(jinjaNode)) {
                result.addJinjaElement(transformedNode);
            }
        }

        this.transformedTemplate = result;
    }

    /**
     * Transforms a single Jinja node into zero, one, or many nodes.
     *
     * <p>Most node kinds map 1:1, but this can now also:</p>
     * <ul>
     *   <li>collapse a {@code {% if %}} down to just the taken branch's body
     *       when the condition is fully known from the data-flow context;</li>
     *   <li>unroll a {@code {% for %}} into one copy of its body per element
     *       when the iterable resolves to a known list.</li>
     * </ul>
     */
    private List<JinjaNode> transformJinjaNode(JinjaNode node) {
        if (node == null) {
            return List.of();
        }

        if (node instanceof JinjaVariableNode variableNode) {
            return List.of(transformVariable(variableNode));
        }

        if (node instanceof JinjaProgramNode programNode) {
            return List.of(new JinjaProgramNode(transformBody(programNode.getElements()), programNode.getLine(), programNode.getColumn()));
        }

        if (node instanceof JinjaIfNode ifNode) {
            return transformIf(ifNode);
        }

        if (node instanceof JinjaElifNode elifNode) {
            return List.of(new JinjaElifNode(elifNode.getCondition(), elifNode.getConditionTree(), transformBody(elifNode.getBody()), elifNode.getLine(), elifNode.getColumn()));
        }

        if (node instanceof JinjaElseNode elseNode) {
            return List.of(new JinjaElseNode(transformBody(elseNode.getBody()), elseNode.getLine(), elseNode.getColumn()));
        }

        if (node instanceof JinjaForNode forNode) {
            return transformFor(forNode);
        }

        if (node instanceof JinjaBlockNode blockNode) {
            return List.of(new JinjaBlockNode(blockNode.getBlockName(), transformBody(blockNode.getBody()), blockNode.getLine(), blockNode.getColumn()));
        }

        if (node instanceof JinjaMacroNode macroNode) {
            return List.of(new JinjaMacroNode(macroNode.getMacroName(), new ArrayList<>(macroNode.getParameters()), transformBody(macroNode.getBody()), macroNode.getLine(), macroNode.getColumn()));
        }

        if (node instanceof JinjaExpressionNode expressionNode) {
            return List.of(transformExpression(expressionNode));
        }

        if (node instanceof JinjaCommentNode commentNode) {
            return List.of(new JinjaCommentNode(commentNode.getContent(), commentNode.getLine(), commentNode.getColumn()));
        }

        if (node instanceof JinjaExtendsNode extendsNode) {
            return List.of(new JinjaExtendsNode(extendsNode.getParentTemplatePath(), extendsNode.getLine(), extendsNode.getColumn()));
        }

        if (node instanceof JinjaIncludeNode includeNode) {
            return List.of(new JinjaIncludeNode(includeNode.getTemplatePath(), includeNode.getLine(), includeNode.getColumn()));
        }

        return List.of(node);
    }

    /**
     * Substitutes {@code {{ variable }}} with a literal when the data-flow
     * context can resolve it (including through attribute/index access), and
     * applies any filters (e.g. {@code |upper}) that resolve to a known value.
     * A variable/filter that cannot be fully determined is left as-is so it
     * still renders correctly at runtime.
     */
    private JinjaNode transformVariable(JinjaVariableNode variableNode) {
        if (!context.isResolvable(variableNode.getVariableName())) {
            return variableNode;
        }

        Object value = context.resolve(variableNode.getVariableName());
        for (JinjaFilterNode filter : variableNode.getFilters()) {
            if (!JinjaFilters.isKnown(filter.getFilterName())) {
                // Filter isn't one we can evaluate statically: keep the
                // variable node (with its filters) instead of guessing.
                return variableNode;
            }
            value = JinjaFilters.apply(filter.getFilterName(), filter.getArguments(), value);
        }
        return new LiteralNode(value, variableNode.getLine(), variableNode.getColumn());
    }

    /**
     * Substitutes {@code {{ expression }}} with a literal when its
     * structured tree (see {@link JinjaExpressionNode#getRoot()}) is fully
     * resolvable against the data-flow context - e.g. {@code {{ price * qty }}}
     * or {@code {{ items[0].name }}}, not just a plain variable reference.
     * Falls back to keeping the expression node (tree included, so a later
     * pass over a different context - e.g. each unrolled {@code {% for %}}
     * iteration - can still try again) when it isn't resolvable, or when
     * there is no tree at all (e.g. a hand-built test AST).
     */
    private JinjaNode transformExpression(JinjaExpressionNode expressionNode) {
        JinjaNode root = expressionNode.getRoot();
        if (root == null || !JinjaTreeEvaluator.isResolvable(root, context)) {
            return new JinjaExpressionNode(expressionNode.getExpression(), root, expressionNode.getFilters(),
                    expressionNode.getLine(), expressionNode.getColumn());
        }

        Object value = JinjaTreeEvaluator.resolve(root, context);
        for (JinjaFilterNode filter : expressionNode.getFilters()) {
            if (!JinjaFilters.isKnown(filter.getFilterName())) {
                return new JinjaExpressionNode(expressionNode.getExpression(), root, expressionNode.getFilters(),
                        expressionNode.getLine(), expressionNode.getColumn());
            }
            value = JinjaFilters.apply(filter.getFilterName(), filter.getArguments(), value);
        }
        return new LiteralNode(value, expressionNode.getLine(), expressionNode.getColumn());
    }

    /**
     * Evaluates the if/elif/else chain against the context. If every
     * condition up to and including the taken branch is statically
     * determined, only that branch's (transformed) body is emitted -
     * inlined directly, with no surrounding {@code {% if %}}. If any
     * condition along the way is unknown, the whole if/elif/else structure
     * is kept, with each branch still individually transformed.
     */
    private List<JinjaNode> transformIf(JinjaIfNode ifNode) {
        Boolean condition = JinjaConditionEvaluator.evaluate(ifNode.getConditionTree(), ifNode.getCondition(), context);
        if (condition != null) {
            if (condition) {
                return transformBody(ifNode.getThenBody());
            }
            for (JinjaElifNode elif : ifNode.getElifNodes()) {
                Boolean elifCondition = JinjaConditionEvaluator.evaluate(elif.getConditionTree(), elif.getCondition(), context);
                if (elifCondition == null) {
                    return List.of(structurallyTransformedIf(ifNode));
                }
                if (elifCondition) {
                    return transformBody(elif.getBody());
                }
            }
            return ifNode.hasElse() ? transformBody(ifNode.getElseNode().getBody()) : List.of();
        }
        return List.of(structurallyTransformedIf(ifNode));
    }

    private JinjaIfNode structurallyTransformedIf(JinjaIfNode ifNode) {
        List<JinjaNode> thenBody = transformBody(ifNode.getThenBody());
        List<JinjaElifNode> elifNodes = new ArrayList<>();
        for (JinjaElifNode elifNode : ifNode.getElifNodes()) {
            elifNodes.add(new JinjaElifNode(elifNode.getCondition(), elifNode.getConditionTree(), transformBody(elifNode.getBody()), elifNode.getLine(), elifNode.getColumn()));
        }
        JinjaElseNode elseNode = ifNode.hasElse()
                ? new JinjaElseNode(transformBody(ifNode.getElseNode().getBody()), ifNode.getElseNode().getLine(), ifNode.getElseNode().getColumn())
                : null;
        return new JinjaIfNode(ifNode.getCondition(), ifNode.getConditionTree(), thenBody, elifNodes, elseNode, ifNode.getLine(), ifNode.getColumn());
    }

    /**
     * Unrolls the loop when the iterable resolves to a known list, binding
     * the loop variable to each element in turn (via a scoped copy of the
     * context) so nested {@code {{ item }}}/{@code {{ item.name }}}
     * references resolve too. Falls back to keeping the {@code {% for %}}
     * structure (with its body still transformed against the outer context)
     * when the iterable is not statically known.
     */
    private List<JinjaNode> transformFor(JinjaForNode forNode) {
        JinjaNode iterableTree = forNode.getIterableTree();
        boolean resolvable = iterableTree != null
                ? JinjaTreeEvaluator.isResolvable(iterableTree, context)
                : context.isResolvable(forNode.getIterable());
        if (!resolvable) {
            return List.of(new JinjaForNode(forNode.getLoopVariable(), forNode.getIterable(), iterableTree,
                    transformBody(forNode.getBody()), transformBody(forNode.getElseBody()), forNode.getLine(), forNode.getColumn()));
        }

        Object iterable = iterableTree != null
                ? JinjaTreeEvaluator.resolve(iterableTree, context)
                : context.resolve(forNode.getIterable());
        if (!(iterable instanceof List<?> elements)) {
            return List.of(new JinjaForNode(forNode.getLoopVariable(), forNode.getIterable(), iterableTree,
                    transformBody(forNode.getBody()), transformBody(forNode.getElseBody()), forNode.getLine(), forNode.getColumn()));
        }

        if (elements.isEmpty()) {
            return forNode.hasElse() ? transformBody(forNode.getElseBody()) : List.of();
        }

        List<JinjaNode> unrolled = new ArrayList<>();
        Context savedContext = this.context;
        for (Object element : elements) {
            this.context = savedContext.withOverride(forNode.getLoopVariable(), element);
            unrolled.addAll(transformBody(forNode.getBody()));
        }
        this.context = savedContext;
        return unrolled;
    }

    private List<JinjaNode> transformBody(List<JinjaNode> body) {
        List<JinjaNode> transformed = new ArrayList<>();
        for (JinjaNode child : body) {
            transformed.addAll(transformJinjaNode(child));
        }
        return transformed;
    }

    public Context getContext() {
        return context;
    }

    public TemplateProgramNode getTransformedTemplate() {
        return transformedTemplate;
    }

    public String getTemplateName() {
        return templateName;
    }

    public boolean isSuccessful() {
        return context != null && transformedTemplate != null;
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generation Summary:\n");
        sb.append("  Input Python AST: ").append(pythonAst != null ? "provided" : "null").append("\n");
        sb.append("  Input Template AST: ").append(templateAst != null ? "provided" : "null").append("\n");
        sb.append("  Symbol Table: ").append(symbolTable != null ? "provided" : "null").append("\n");

        if (context != null) {
            sb.append("  Context Variables: ").append(context.size()).append("\n");
            for (String varName : context.getAll().keySet()) {
                sb.append("    - ").append(varName).append(" = ").append(context.get(varName)).append("\n");
            }
        } else {
            sb.append("  Context: NOT GENERATED\n");
        }

        if (transformedTemplate != null) {
            sb.append("  Transformed Template: Generated\n");
            sb.append("    HTML elements: ").append(transformedTemplate.getHtmlElements().size()).append("\n");
            sb.append("    Jinja2 elements: ").append(transformedTemplate.getJinjaElements().size()).append("\n");
        } else {
            sb.append("  Transformed Template: NOT GENERATED\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Generator{" +
                "pythonAst=" + pythonAst +
                ", templateAst=" + (templateAst != null ? "TemplateProgramNode" : "null") +
                ", symbolTable=" + symbolTable +
                ", context=" + (context != null ? "Context(" + context.size() + " vars)" : "null") +
                ", transformedTemplate=" + (transformedTemplate != null ? "TemplateProgramNode" : "null") +
                '}';
    }
}
