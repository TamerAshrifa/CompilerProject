// Generated from grammar/template/TemplateParser.g4 by ANTLR 4.13.1
package grammar.template;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TemplateParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TemplateParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code Document}
	 * labeled alternative in {@link TemplateParser#htmlDocument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDocument(TemplateParser.DocumentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Elements}
	 * labeled alternative in {@link TemplateParser#htmlElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElements(TemplateParser.ElementsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TagElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTagElement(TemplateParser.TagElementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StyleElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleElement(TemplateParser.StyleElementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaBlockElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBlockElement(TemplateParser.JinjaBlockElementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaExprElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExprElement(TemplateParser.JinjaExprElementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaCommentElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaCommentElement(TemplateParser.JinjaCommentElementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Content}
	 * labeled alternative in {@link TemplateParser#htmlContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContent(TemplateParser.ContentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Attribute}
	 * labeled alternative in {@link TemplateParser#htmlAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute(TemplateParser.AttributeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TextContent}
	 * labeled alternative in {@link TemplateParser#htmlChardata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextContent(TemplateParser.TextContentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhitespaceContent}
	 * labeled alternative in {@link TemplateParser#htmlChardata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhitespaceContent(TemplateParser.WhitespaceContentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MiscDoctype}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMiscDoctype(TemplateParser.MiscDoctypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MiscComment}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMiscComment(TemplateParser.MiscCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MiscWhitespace}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMiscWhitespace(TemplateParser.MiscWhitespaceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MiscText}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMiscText(TemplateParser.MiscTextContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#htmlDoctype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlDoctype(TemplateParser.HtmlDoctypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StandardComment}
	 * labeled alternative in {@link TemplateParser#htmlComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStandardComment(TemplateParser.StandardCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ConditionalComment}
	 * labeled alternative in {@link TemplateParser#htmlComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalComment(TemplateParser.ConditionalCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StyleBlock}
	 * labeled alternative in {@link TemplateParser#style}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleBlock(TemplateParser.StyleBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssStylesheet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssStylesheet(TemplateParser.CssStylesheetContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssStylesheetItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssStylesheetItem(TemplateParser.CssStylesheetItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssRule(TemplateParser.CssRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssAtRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAtRule(TemplateParser.CssAtRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssAtRulePrelude}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAtRulePrelude(TemplateParser.CssAtRulePreludeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssQualifiedRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssQualifiedRule(TemplateParser.CssQualifiedRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssSelectorList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSelectorList(TemplateParser.CssSelectorListContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssComma}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssComma(TemplateParser.CssCommaContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSelector(TemplateParser.CssSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssCombinator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssCombinator(TemplateParser.CssCombinatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssCompoundSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssCompoundSelector(TemplateParser.CssCompoundSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssTypeSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssTypeSelector(TemplateParser.CssTypeSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssSimpleSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSimpleSelector(TemplateParser.CssSimpleSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssIdSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssIdSelector(TemplateParser.CssIdSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssClassSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssClassSelector(TemplateParser.CssClassSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssAttributeSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAttributeSelector(TemplateParser.CssAttributeSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssAttributeMatcher}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAttributeMatcher(TemplateParser.CssAttributeMatcherContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssAttributeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssAttributeValue(TemplateParser.CssAttributeValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssPseudoSelector(TemplateParser.CssPseudoSelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssBlock(TemplateParser.CssBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssBlockItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssBlockItem(TemplateParser.CssBlockItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssDeclaration(TemplateParser.CssDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssPropertyName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssPropertyName(TemplateParser.CssPropertyNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssImportant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssImportant(TemplateParser.CssImportantContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssValueSequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssValueSequence(TemplateParser.CssValueSequenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssComponentValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssComponentValue(TemplateParser.CssComponentValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssFunctionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFunctionCall(TemplateParser.CssFunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssFunctionArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFunctionArguments(TemplateParser.CssFunctionArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssFunctionArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFunctionArgument(TemplateParser.CssFunctionArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssColor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssColor(TemplateParser.CssColorContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssMeasurement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssMeasurement(TemplateParser.CssMeasurementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssIdent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssIdent(TemplateParser.CssIdentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssWhitespace}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssWhitespace(TemplateParser.CssWhitespaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#cssComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssComment(TemplateParser.CssCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaBlockRule}
	 * labeled alternative in {@link TemplateParser#jinjaBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBlockRule(TemplateParser.JinjaBlockRuleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfTag(TemplateParser.IfTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ElifTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElifTag(TemplateParser.ElifTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ElseTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseTag(TemplateParser.ElseTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EndIfTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndIfTag(TemplateParser.EndIfTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForTag(TemplateParser.ForTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EndForTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndForTag(TemplateParser.EndForTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BlockTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockTag(TemplateParser.BlockTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EndBlockTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndBlockTag(TemplateParser.EndBlockTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MacroTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacroTag(TemplateParser.MacroTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EndMacroTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndMacroTag(TemplateParser.EndMacroTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExtendsTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtendsTag(TemplateParser.ExtendsTagContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IncludeTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncludeTag(TemplateParser.IncludeTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaForTargets}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaForTargets(TemplateParser.JinjaForTargetsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaParamList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaParamList(TemplateParser.JinjaParamListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaExpression}
	 * labeled alternative in {@link TemplateParser#jinjaExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExpression(TemplateParser.JinjaExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaFilterCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFilterCall(TemplateParser.JinjaFilterCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaArgList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaArgList(TemplateParser.JinjaArgListContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaOrExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaOrExpr(TemplateParser.JinjaOrExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaAndExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaAndExpr(TemplateParser.JinjaAndExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaNotExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaNotExpr(TemplateParser.JinjaNotExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaComparisonExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaComparisonExpr(TemplateParser.JinjaComparisonExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaAdditiveExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaAdditiveExpr(TemplateParser.JinjaAdditiveExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaMultiplicativeExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaMultiplicativeExpr(TemplateParser.JinjaMultiplicativeExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaFilteredPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFilteredPrimary(TemplateParser.JinjaFilteredPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaPrimary(TemplateParser.JinjaPrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaAtomTrailer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaAtomTrailer(TemplateParser.JinjaAtomTrailerContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaTrailer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaTrailer(TemplateParser.JinjaTrailerContext ctx);
	/**
	 * Visit a parse tree produced by {@link TemplateParser#jinjaAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaAtom(TemplateParser.JinjaAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaCommentRule}
	 * labeled alternative in {@link TemplateParser#jinjaComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaCommentRule(TemplateParser.JinjaCommentRuleContext ctx);
}