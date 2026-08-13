package generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import template.ast.TemplateProgramNode;
import template.ast.html.HtmlNode;
import template.ast.html.JinjaHostNode;

import template.ast.jinja.JinjaBlockNode;
import template.ast.jinja.JinjaCommentNode;
import template.ast.jinja.JinjaElifNode;
import template.ast.jinja.JinjaElseNode;
import template.ast.jinja.JinjaExpressionNode;
import template.ast.jinja.JinjaExtendsNode;
import template.ast.jinja.JinjaFilterNode;
import template.ast.jinja.JinjaForNode;
import template.ast.jinja.JinjaHtmlRefNode;
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

    /**
     * (original hoisted Jinja node &rarr; its resolved replacement(s)),
     * populated during {@link #transformTemplateAst()} for every top-level
     * entry of {@code templateAst.getJinjaElements()}. Consumed by {@link
     * HtmlGenerator#visitJinjaHostNode} to inline a resolved value back at
     * the exact position, inside the HTML tree, that it was hoisted out of -
     * see {@link template.ast.html.JinjaHostNode} for the full reasoning.
     */
    private final Map<JinjaNode, List<JinjaNode>> resolvedReplacements = new IdentityHashMap<>();

    /**
     * Top-level {@link HtmlNode}s that textually belonged only to an
     * {@code {% if %}}/{@code {% elif %}}/{@code {% else %}} branch that was
     * <em>not</em> taken, collected while resolving the Jinja tree (see
     * {@link #suppressUntakenBranches}) and excluded from {@link
     * #transformedTemplate}'s HTML list. See {@link
     * template.ast.jinja.JinjaHtmlRefNode} for the full reasoning.
     */
    private final Set<HtmlNode> suppressedHtmlNodes = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * Identities, within {@link #resolvedReplacements}'s VALUES (i.e. within
     * the transformed tree {@link #transformedTemplate} actually holds — not
     * the original one), of every node that resulted from resolving a
     * hoisted Jinja construct that also has a {@link JinjaHostNode}
     * placeholder somewhere in the HTML tree. See {@link
     * #transformTemplateAst()}'s inline comment for why this is tracked
     * separately rather than simply excluded from {@link
     * #transformedTemplate}'s own Jinja list.
     */
    private final Set<JinjaNode> hostedReplacementNodes = Collections.newSetFromMap(new IdentityHashMap<>());


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


        // The Jinja pass runs first: transformIf populates
        // suppressedHtmlNodes as a side effect (see suppressUntakenBranches)
        // while it determines which branch, if any, was taken - so the HTML
        // list below can then be filtered against a complete set.
        //
        // Every transformed node is still added to the result's top-level
        // Jinja list below, even ones whose ORIGINAL (pre-transform) node
        // has a JinjaHostNode placeholder elsewhere in the HTML tree (see
        // that class) that will ALSO render the same resolved value inline.
        // That is deliberate here: this TemplateProgramNode is a shared
        // result consumed independently by more than one caller - including
        // JinjaGenerator.generate() on its own, which is documented and
        // tested to show every resolved value regardless of whether some
        // OTHER consumer also has a way to show it inline. Only
        // FinalDocumentGenerator actually combines the HTML and Jinja halves
        // into one document, so it is the one place a hosted node's value
        // could visibly double up - hostedReplacementNodes (identities
        // within THIS transformed list, not the original one, since that is
        // what FinalDocumentGenerator's own getJinjaElements() loop compares
        // against) exists precisely so that exclusion can be applied there,
        // right before merging, without this method's own callers losing
        // anything.
        Set<JinjaNode> hostedOriginals = Collections.newSetFromMap(new IdentityHashMap<>());
        for (HtmlNode htmlNode : templateAst.getHtmlElements()) {
            collectHostedJinjaNodes(htmlNode, hostedOriginals);
        }

        List<JinjaNode> transformedJinja = new ArrayList<>();
        for (JinjaNode jinjaNode : templateAst.getJinjaElements()) {
            List<JinjaNode> transformedNode = transformJinjaNode(jinjaNode);
            resolvedReplacements.put(jinjaNode, transformedNode);
            transformedJinja.addAll(transformedNode);
            if (hostedOriginals.contains(jinjaNode)) {
                hostedReplacementNodes.addAll(transformedNode);
            }
        }

        TemplateProgramNode result = new TemplateProgramNode();
        for (HtmlNode htmlNode : templateAst.getHtmlElements()) {
            HtmlNode filtered = filterSuppressedHtml(htmlNode);
            if (filtered != null) {
                result.addHtmlElement(filtered);
            }
        }
        for (JinjaNode transformedNode : transformedJinja) {
            result.addJinjaElement(transformedNode);

        }

        this.transformedTemplate = result;
    }

    /**
     * Recursively rebuilds {@code node} with every {@link #suppressedHtmlNodes}
     * entry removed - including ones nested arbitrarily deep inside an
     * {@link HtmlElementNode}'s children, not just top-level ones (a
     * suppressed {@code <p>} belonging to an untaken {@code {% if %}} branch
     * is, after all, ordinarily nested inside a containing tag like
     * {@code <body>}, not itself a top-level node) - or {@code null} if
     * {@code node} itself is suppressed. Returns {@code node} completely
     * unchanged (same identity, not a rebuilt copy) when nothing under it
     * needed removing, so a {@link JinjaHostNode} placeholder several levels
     * down still correctly correlates by identity with {@link
     * #resolvedReplacements}/{@link #hostedReplacementNodes} for a subtree
     * that was not touched.
     */
    private HtmlNode filterSuppressedHtml(HtmlNode node) {
        if (suppressedHtmlNodes.contains(node)) {
            return null;
        }
        if (node instanceof template.ast.html.HtmlElementNode element) {
            List<HtmlNode> originalChildren = element.getChildren();
            List<HtmlNode> filteredChildren = null;
            for (int i = 0; i < originalChildren.size(); i++) {
                HtmlNode child = originalChildren.get(i);
                HtmlNode filteredChild = filterSuppressedHtml(child);
                if (filteredChildren == null && filteredChild != child) {
                    filteredChildren = new ArrayList<>(originalChildren.subList(0, i));
                }
                if (filteredChildren != null && filteredChild != null) {
                    filteredChildren.add(filteredChild);
                }
            }
            if (filteredChildren == null) {
                return element;
            }
            return new template.ast.html.HtmlElementNode(element.getTagName(), element.getAttributes(),
                    filteredChildren, element.isSelfClosing(), element.getLine(), element.getColumn());
        }
        return node;
    }

    /** Recursively collects every original node referenced by a {@link JinjaHostNode} anywhere under {@code htmlNode}. */
    private void collectHostedJinjaNodes(HtmlNode htmlNode, Set<JinjaNode> out) {
        if (htmlNode instanceof JinjaHostNode hostNode) {
            out.add(hostNode.getHostedNode());
        } else if (htmlNode instanceof template.ast.html.HtmlElementNode elementNode) {
            for (HtmlNode child : elementNode.getChildren()) {
                collectHostedJinjaNodes(child, out);
            }
        }
        // Other HtmlNode subtypes (text, comment, style, attribute) have no
        // nested HtmlNode children to walk.
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


        if (node instanceof JinjaHtmlRefNode) {
            // Bookkeeping-only marker; already consumed by
            // suppressUntakenBranches while resolving the enclosing {% if %}.
            // Never part of renderable output - see JinjaHtmlRefNode.
            return List.of();
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
     * inlined directly, with no surrounding {@code {% if %}} - and every
     * {@link HtmlNode} referenced (via {@link JinjaHtmlRefNode}) only by a
     * branch that was NOT taken is recorded in {@link #suppressedHtmlNodes}
     * so {@link #transformTemplateAst()} excludes it from the resolved HTML
     * list too. If any condition along the way is unknown, the whole
     * if/elif/else structure is kept, with each branch still individually
     * transformed, and nothing is suppressed (since which branch will
     * actually run is not known here).

     */
    private List<JinjaNode> transformIf(JinjaIfNode ifNode) {
        Boolean condition = JinjaConditionEvaluator.evaluate(ifNode.getConditionTree(), ifNode.getCondition(), context);
        if (condition != null) {
            if (condition) {
                suppressUntakenBranches(ifNode, 0);
                return transformBody(ifNode.getThenBody());
            }
            List<JinjaElifNode> elifs = ifNode.getElifNodes();
            for (int i = 0; i < elifs.size(); i++) {
                JinjaElifNode elif = elifs.get(i);

                Boolean elifCondition = JinjaConditionEvaluator.evaluate(elif.getConditionTree(), elif.getCondition(), context);
                if (elifCondition == null) {
                    return List.of(structurallyTransformedIf(ifNode));
                }
                if (elifCondition) {
                    suppressUntakenBranches(ifNode, 1 + i);
                    return transformBody(elif.getBody());
                }
            }
            if (ifNode.hasElse()) {
                suppressUntakenBranches(ifNode, 1 + elifs.size());
                return transformBody(ifNode.getElseNode().getBody());
            }
            // No branch matched and there is no else: every branch is
            // untaken, so pass an index no real branch can ever equal.
            suppressUntakenBranches(ifNode, -1);
            return List.of();

        }
        return List.of(structurallyTransformedIf(ifNode));
    }

    /**
     * Marks every {@link HtmlNode} referenced (via {@link JinjaHtmlRefNode})
     * by every branch of {@code ifNode} <em>except</em> the one identified by
     * {@code takenBranchIndex} as suppressed: {@code 0} = the then-branch,
     * {@code 1..elifs.size()} = elif number {@code takenBranchIndex - 1},
     * {@code elifs.size() + 1} = the else branch, and any other value (e.g.
     * {@code -1}) suppresses every branch. Branches are identified by index
     * rather than by comparing {@link JinjaIfNode#getThenBody()}/{@link
     * JinjaElifNode#getBody()}/{@link JinjaElseNode#getBody()} object
     * references deliberately: each of those getters wraps its underlying
     * list in a fresh {@link Collections#unmodifiableList} on every call, so
     * two separate calls are never {@code ==} to each other even for what is
     * conceptually "the same" body - a reference-equality comparison would
     * never actually match the branch that WAS taken.
     */
    private void suppressUntakenBranches(JinjaIfNode ifNode, int takenBranchIndex) {
        if (takenBranchIndex != 0) {
            collectSuppressedHtml(ifNode.getThenBody());
        }
        List<JinjaElifNode> elifs = ifNode.getElifNodes();
        for (int i = 0; i < elifs.size(); i++) {
            if (takenBranchIndex != 1 + i) {
                collectSuppressedHtml(elifs.get(i).getBody());
            }
        }
        if (ifNode.hasElse() && takenBranchIndex != 1 + elifs.size()) {
            collectSuppressedHtml(ifNode.getElseNode().getBody());
        }
    }

    /**
     * Recursively collects every {@link HtmlNode} referenced by a {@link
     * JinjaHtmlRefNode} within {@code body} - a branch that {@link
     * #suppressUntakenBranches} determined was not taken - into {@link
     * #suppressedHtmlNodes}. Descends into nested {@code {% if %}}/
     * {@code {% for %}}/{@code {% block %}} bodies too, since everything
     * under an untaken branch is unconditionally untaken as well; a nested
     * {@code {% macro %}} body is a template definition rather than
     * directly-rendered content, so it is intentionally not walked.
     */
    private void collectSuppressedHtml(List<JinjaNode> body) {
        for (JinjaNode node : body) {
            if (node instanceof JinjaHtmlRefNode ref) {
                suppressedHtmlNodes.add(ref.getReferencedHtmlNode());
            } else if (node instanceof JinjaIfNode nestedIf) {
                collectSuppressedHtml(nestedIf.getThenBody());
                for (JinjaElifNode elif : nestedIf.getElifNodes()) {
                    collectSuppressedHtml(elif.getBody());
                }
                if (nestedIf.hasElse()) {
                    collectSuppressedHtml(nestedIf.getElseNode().getBody());
                }
            } else if (node instanceof JinjaForNode nestedFor) {
                collectSuppressedHtml(nestedFor.getBody());
                collectSuppressedHtml(nestedFor.getElseBody());
            } else if (node instanceof JinjaBlockNode nestedBlock) {
                collectSuppressedHtml(nestedBlock.getBody());
            }
        }
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


    /**
     * (original hoisted Jinja node &rarr; its resolved replacement(s)) for
     * every top-level entry of the source Template AST's Jinja list, as
     * resolved by the last {@link #generate()} call. Feed this to {@link
     * HtmlGenerator#withResolvedReplacements} so a {@link
     * template.ast.html.JinjaHostNode} placeholder inside {@link
     * #getTransformedTemplate()}'s HTML tree can be rendered as the value it
     * actually resolved to. Empty (never {@code null}) before {@link
     * #generate()} has run, or if {@code templateAst}/the derived context was
     * {@code null}.
     */
    public Map<JinjaNode, List<JinjaNode>> getResolvedReplacements() {
        return Collections.unmodifiableMap(resolvedReplacements);
    }

    /**
     * See {@link #hostedReplacementNodes}. Feed this to {@link
     * FinalDocumentGenerator#withHostedReplacementNodes} so its top-level
     * merge does not also emit, as an extra sibling, a resolved value that
     * {@link #getTransformedTemplate()}'s HTML tree already shows inline via
     * a {@link template.ast.html.JinjaHostNode}.
     */
    public Set<JinjaNode> getHostedReplacementNodes() {
        return Collections.unmodifiableSet(hostedReplacementNodes);
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
