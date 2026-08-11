// Generated from grammar/template/TemplateParser.g4 by ANTLR 4.13.1
package grammar.template;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TemplateParser}.
 */
public interface TemplateParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code Document}
	 * labeled alternative in {@link TemplateParser#htmlDocument}.
	 * @param ctx the parse tree
	 */
	void enterDocument(TemplateParser.DocumentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Document}
	 * labeled alternative in {@link TemplateParser#htmlDocument}.
	 * @param ctx the parse tree
	 */
	void exitDocument(TemplateParser.DocumentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Elements}
	 * labeled alternative in {@link TemplateParser#htmlElements}.
	 * @param ctx the parse tree
	 */
	void enterElements(TemplateParser.ElementsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Elements}
	 * labeled alternative in {@link TemplateParser#htmlElements}.
	 * @param ctx the parse tree
	 */
	void exitElements(TemplateParser.ElementsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TagElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void enterTagElement(TemplateParser.TagElementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TagElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void exitTagElement(TemplateParser.TagElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StyleElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void enterStyleElement(TemplateParser.StyleElementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StyleElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void exitStyleElement(TemplateParser.StyleElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaBlockElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void enterJinjaBlockElement(TemplateParser.JinjaBlockElementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaBlockElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void exitJinjaBlockElement(TemplateParser.JinjaBlockElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaExprElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExprElement(TemplateParser.JinjaExprElementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaExprElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExprElement(TemplateParser.JinjaExprElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaCommentElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void enterJinjaCommentElement(TemplateParser.JinjaCommentElementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaCommentElement}
	 * labeled alternative in {@link TemplateParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void exitJinjaCommentElement(TemplateParser.JinjaCommentElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Content}
	 * labeled alternative in {@link TemplateParser#htmlContent}.
	 * @param ctx the parse tree
	 */
	void enterContent(TemplateParser.ContentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Content}
	 * labeled alternative in {@link TemplateParser#htmlContent}.
	 * @param ctx the parse tree
	 */
	void exitContent(TemplateParser.ContentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Attribute}
	 * labeled alternative in {@link TemplateParser#htmlAttribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(TemplateParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Attribute}
	 * labeled alternative in {@link TemplateParser#htmlAttribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(TemplateParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TextContent}
	 * labeled alternative in {@link TemplateParser#htmlChardata}.
	 * @param ctx the parse tree
	 */
	void enterTextContent(TemplateParser.TextContentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TextContent}
	 * labeled alternative in {@link TemplateParser#htmlChardata}.
	 * @param ctx the parse tree
	 */
	void exitTextContent(TemplateParser.TextContentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhitespaceContent}
	 * labeled alternative in {@link TemplateParser#htmlChardata}.
	 * @param ctx the parse tree
	 */
	void enterWhitespaceContent(TemplateParser.WhitespaceContentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhitespaceContent}
	 * labeled alternative in {@link TemplateParser#htmlChardata}.
	 * @param ctx the parse tree
	 */
	void exitWhitespaceContent(TemplateParser.WhitespaceContentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MiscDoctype}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 */
	void enterMiscDoctype(TemplateParser.MiscDoctypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MiscDoctype}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 */
	void exitMiscDoctype(TemplateParser.MiscDoctypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MiscComment}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 */
	void enterMiscComment(TemplateParser.MiscCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MiscComment}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 */
	void exitMiscComment(TemplateParser.MiscCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MiscWhitespace}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 */
	void enterMiscWhitespace(TemplateParser.MiscWhitespaceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MiscWhitespace}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 */
	void exitMiscWhitespace(TemplateParser.MiscWhitespaceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MiscText}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 */
	void enterMiscText(TemplateParser.MiscTextContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MiscText}
	 * labeled alternative in {@link TemplateParser#htmlMisc}.
	 * @param ctx the parse tree
	 */
	void exitMiscText(TemplateParser.MiscTextContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#htmlDoctype}.
	 * @param ctx the parse tree
	 */
	void enterHtmlDoctype(TemplateParser.HtmlDoctypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#htmlDoctype}.
	 * @param ctx the parse tree
	 */
	void exitHtmlDoctype(TemplateParser.HtmlDoctypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StandardComment}
	 * labeled alternative in {@link TemplateParser#htmlComment}.
	 * @param ctx the parse tree
	 */
	void enterStandardComment(TemplateParser.StandardCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StandardComment}
	 * labeled alternative in {@link TemplateParser#htmlComment}.
	 * @param ctx the parse tree
	 */
	void exitStandardComment(TemplateParser.StandardCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ConditionalComment}
	 * labeled alternative in {@link TemplateParser#htmlComment}.
	 * @param ctx the parse tree
	 */
	void enterConditionalComment(TemplateParser.ConditionalCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ConditionalComment}
	 * labeled alternative in {@link TemplateParser#htmlComment}.
	 * @param ctx the parse tree
	 */
	void exitConditionalComment(TemplateParser.ConditionalCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StyleBlock}
	 * labeled alternative in {@link TemplateParser#style}.
	 * @param ctx the parse tree
	 */
	void enterStyleBlock(TemplateParser.StyleBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StyleBlock}
	 * labeled alternative in {@link TemplateParser#style}.
	 * @param ctx the parse tree
	 */
	void exitStyleBlock(TemplateParser.StyleBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssStylesheet}.
	 * @param ctx the parse tree
	 */
	void enterCssStylesheet(TemplateParser.CssStylesheetContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssStylesheet}.
	 * @param ctx the parse tree
	 */
	void exitCssStylesheet(TemplateParser.CssStylesheetContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssStylesheetItem}.
	 * @param ctx the parse tree
	 */
	void enterCssStylesheetItem(TemplateParser.CssStylesheetItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssStylesheetItem}.
	 * @param ctx the parse tree
	 */
	void exitCssStylesheetItem(TemplateParser.CssStylesheetItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssRule}.
	 * @param ctx the parse tree
	 */
	void enterCssRule(TemplateParser.CssRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssRule}.
	 * @param ctx the parse tree
	 */
	void exitCssRule(TemplateParser.CssRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssAtRule}.
	 * @param ctx the parse tree
	 */
	void enterCssAtRule(TemplateParser.CssAtRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssAtRule}.
	 * @param ctx the parse tree
	 */
	void exitCssAtRule(TemplateParser.CssAtRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssAtRulePrelude}.
	 * @param ctx the parse tree
	 */
	void enterCssAtRulePrelude(TemplateParser.CssAtRulePreludeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssAtRulePrelude}.
	 * @param ctx the parse tree
	 */
	void exitCssAtRulePrelude(TemplateParser.CssAtRulePreludeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssQualifiedRule}.
	 * @param ctx the parse tree
	 */
	void enterCssQualifiedRule(TemplateParser.CssQualifiedRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssQualifiedRule}.
	 * @param ctx the parse tree
	 */
	void exitCssQualifiedRule(TemplateParser.CssQualifiedRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssSelectorList}.
	 * @param ctx the parse tree
	 */
	void enterCssSelectorList(TemplateParser.CssSelectorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssSelectorList}.
	 * @param ctx the parse tree
	 */
	void exitCssSelectorList(TemplateParser.CssSelectorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssComma}.
	 * @param ctx the parse tree
	 */
	void enterCssComma(TemplateParser.CssCommaContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssComma}.
	 * @param ctx the parse tree
	 */
	void exitCssComma(TemplateParser.CssCommaContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssSelector(TemplateParser.CssSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssSelector(TemplateParser.CssSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssCombinator}.
	 * @param ctx the parse tree
	 */
	void enterCssCombinator(TemplateParser.CssCombinatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssCombinator}.
	 * @param ctx the parse tree
	 */
	void exitCssCombinator(TemplateParser.CssCombinatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssCompoundSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssCompoundSelector(TemplateParser.CssCompoundSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssCompoundSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssCompoundSelector(TemplateParser.CssCompoundSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssTypeSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssTypeSelector(TemplateParser.CssTypeSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssTypeSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssTypeSelector(TemplateParser.CssTypeSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssSimpleSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssSimpleSelector(TemplateParser.CssSimpleSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssSimpleSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssSimpleSelector(TemplateParser.CssSimpleSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssIdSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssIdSelector(TemplateParser.CssIdSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssIdSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssIdSelector(TemplateParser.CssIdSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssClassSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssClassSelector(TemplateParser.CssClassSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssClassSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssClassSelector(TemplateParser.CssClassSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssAttributeSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssAttributeSelector(TemplateParser.CssAttributeSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssAttributeSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssAttributeSelector(TemplateParser.CssAttributeSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssAttributeMatcher}.
	 * @param ctx the parse tree
	 */
	void enterCssAttributeMatcher(TemplateParser.CssAttributeMatcherContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssAttributeMatcher}.
	 * @param ctx the parse tree
	 */
	void exitCssAttributeMatcher(TemplateParser.CssAttributeMatcherContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssAttributeValue}.
	 * @param ctx the parse tree
	 */
	void enterCssAttributeValue(TemplateParser.CssAttributeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssAttributeValue}.
	 * @param ctx the parse tree
	 */
	void exitCssAttributeValue(TemplateParser.CssAttributeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 */
	void enterCssPseudoSelector(TemplateParser.CssPseudoSelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssPseudoSelector}.
	 * @param ctx the parse tree
	 */
	void exitCssPseudoSelector(TemplateParser.CssPseudoSelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssBlock}.
	 * @param ctx the parse tree
	 */
	void enterCssBlock(TemplateParser.CssBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssBlock}.
	 * @param ctx the parse tree
	 */
	void exitCssBlock(TemplateParser.CssBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssBlockItem}.
	 * @param ctx the parse tree
	 */
	void enterCssBlockItem(TemplateParser.CssBlockItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssBlockItem}.
	 * @param ctx the parse tree
	 */
	void exitCssBlockItem(TemplateParser.CssBlockItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterCssDeclaration(TemplateParser.CssDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitCssDeclaration(TemplateParser.CssDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssPropertyName}.
	 * @param ctx the parse tree
	 */
	void enterCssPropertyName(TemplateParser.CssPropertyNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssPropertyName}.
	 * @param ctx the parse tree
	 */
	void exitCssPropertyName(TemplateParser.CssPropertyNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssImportant}.
	 * @param ctx the parse tree
	 */
	void enterCssImportant(TemplateParser.CssImportantContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssImportant}.
	 * @param ctx the parse tree
	 */
	void exitCssImportant(TemplateParser.CssImportantContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssValueSequence}.
	 * @param ctx the parse tree
	 */
	void enterCssValueSequence(TemplateParser.CssValueSequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssValueSequence}.
	 * @param ctx the parse tree
	 */
	void exitCssValueSequence(TemplateParser.CssValueSequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssComponentValue}.
	 * @param ctx the parse tree
	 */
	void enterCssComponentValue(TemplateParser.CssComponentValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssComponentValue}.
	 * @param ctx the parse tree
	 */
	void exitCssComponentValue(TemplateParser.CssComponentValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssFunctionCall}.
	 * @param ctx the parse tree
	 */
	void enterCssFunctionCall(TemplateParser.CssFunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssFunctionCall}.
	 * @param ctx the parse tree
	 */
	void exitCssFunctionCall(TemplateParser.CssFunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssFunctionArguments}.
	 * @param ctx the parse tree
	 */
	void enterCssFunctionArguments(TemplateParser.CssFunctionArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssFunctionArguments}.
	 * @param ctx the parse tree
	 */
	void exitCssFunctionArguments(TemplateParser.CssFunctionArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssFunctionArgument}.
	 * @param ctx the parse tree
	 */
	void enterCssFunctionArgument(TemplateParser.CssFunctionArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssFunctionArgument}.
	 * @param ctx the parse tree
	 */
	void exitCssFunctionArgument(TemplateParser.CssFunctionArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssColor}.
	 * @param ctx the parse tree
	 */
	void enterCssColor(TemplateParser.CssColorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssColor}.
	 * @param ctx the parse tree
	 */
	void exitCssColor(TemplateParser.CssColorContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssMeasurement}.
	 * @param ctx the parse tree
	 */
	void enterCssMeasurement(TemplateParser.CssMeasurementContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssMeasurement}.
	 * @param ctx the parse tree
	 */
	void exitCssMeasurement(TemplateParser.CssMeasurementContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssIdent}.
	 * @param ctx the parse tree
	 */
	void enterCssIdent(TemplateParser.CssIdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssIdent}.
	 * @param ctx the parse tree
	 */
	void exitCssIdent(TemplateParser.CssIdentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssWhitespace}.
	 * @param ctx the parse tree
	 */
	void enterCssWhitespace(TemplateParser.CssWhitespaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssWhitespace}.
	 * @param ctx the parse tree
	 */
	void exitCssWhitespace(TemplateParser.CssWhitespaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#cssComment}.
	 * @param ctx the parse tree
	 */
	void enterCssComment(TemplateParser.CssCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#cssComment}.
	 * @param ctx the parse tree
	 */
	void exitCssComment(TemplateParser.CssCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaBlockRule}
	 * labeled alternative in {@link TemplateParser#jinjaBlock}.
	 * @param ctx the parse tree
	 */
	void enterJinjaBlockRule(TemplateParser.JinjaBlockRuleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaBlockRule}
	 * labeled alternative in {@link TemplateParser#jinjaBlock}.
	 * @param ctx the parse tree
	 */
	void exitJinjaBlockRule(TemplateParser.JinjaBlockRuleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterIfTag(TemplateParser.IfTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitIfTag(TemplateParser.IfTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ElifTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterElifTag(TemplateParser.ElifTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ElifTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitElifTag(TemplateParser.ElifTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ElseTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterElseTag(TemplateParser.ElseTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ElseTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitElseTag(TemplateParser.ElseTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EndIfTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterEndIfTag(TemplateParser.EndIfTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EndIfTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitEndIfTag(TemplateParser.EndIfTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterForTag(TemplateParser.ForTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitForTag(TemplateParser.ForTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EndForTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterEndForTag(TemplateParser.EndForTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EndForTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitEndForTag(TemplateParser.EndForTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BlockTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterBlockTag(TemplateParser.BlockTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BlockTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitBlockTag(TemplateParser.BlockTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EndBlockTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterEndBlockTag(TemplateParser.EndBlockTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EndBlockTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitEndBlockTag(TemplateParser.EndBlockTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MacroTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterMacroTag(TemplateParser.MacroTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MacroTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitMacroTag(TemplateParser.MacroTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EndMacroTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterEndMacroTag(TemplateParser.EndMacroTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EndMacroTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitEndMacroTag(TemplateParser.EndMacroTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExtendsTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterExtendsTag(TemplateParser.ExtendsTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExtendsTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitExtendsTag(TemplateParser.ExtendsTagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IncludeTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void enterIncludeTag(TemplateParser.IncludeTagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IncludeTag}
	 * labeled alternative in {@link TemplateParser#jinjaTag}.
	 * @param ctx the parse tree
	 */
	void exitIncludeTag(TemplateParser.IncludeTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaForTargets}.
	 * @param ctx the parse tree
	 */
	void enterJinjaForTargets(TemplateParser.JinjaForTargetsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaForTargets}.
	 * @param ctx the parse tree
	 */
	void exitJinjaForTargets(TemplateParser.JinjaForTargetsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaParamList}.
	 * @param ctx the parse tree
	 */
	void enterJinjaParamList(TemplateParser.JinjaParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaParamList}.
	 * @param ctx the parse tree
	 */
	void exitJinjaParamList(TemplateParser.JinjaParamListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaExpression}
	 * labeled alternative in {@link TemplateParser#jinjaExpr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExpression(TemplateParser.JinjaExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaExpression}
	 * labeled alternative in {@link TemplateParser#jinjaExpr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExpression(TemplateParser.JinjaExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaFilterCall}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFilterCall(TemplateParser.JinjaFilterCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaFilterCall}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFilterCall(TemplateParser.JinjaFilterCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaArgList}.
	 * @param ctx the parse tree
	 */
	void enterJinjaArgList(TemplateParser.JinjaArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaArgList}.
	 * @param ctx the parse tree
	 */
	void exitJinjaArgList(TemplateParser.JinjaArgListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaOrExpr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaOrExpr(TemplateParser.JinjaOrExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaOrExpr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaOrExpr(TemplateParser.JinjaOrExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaAndExpr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaAndExpr(TemplateParser.JinjaAndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaAndExpr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaAndExpr(TemplateParser.JinjaAndExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaNotExpr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaNotExpr(TemplateParser.JinjaNotExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaNotExpr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaNotExpr(TemplateParser.JinjaNotExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaComparisonExpr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaComparisonExpr(TemplateParser.JinjaComparisonExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaComparisonExpr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaComparisonExpr(TemplateParser.JinjaComparisonExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaAdditiveExpr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaAdditiveExpr(TemplateParser.JinjaAdditiveExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaAdditiveExpr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaAdditiveExpr(TemplateParser.JinjaAdditiveExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaMultiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaMultiplicativeExpr(TemplateParser.JinjaMultiplicativeExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaMultiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaMultiplicativeExpr(TemplateParser.JinjaMultiplicativeExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaFilteredPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFilteredPrimary(TemplateParser.JinjaFilteredPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaFilteredPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFilteredPrimary(TemplateParser.JinjaFilteredPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void enterJinjaPrimary(TemplateParser.JinjaPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaPrimary}.
	 * @param ctx the parse tree
	 */
	void exitJinjaPrimary(TemplateParser.JinjaPrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaAtomTrailer}.
	 * @param ctx the parse tree
	 */
	void enterJinjaAtomTrailer(TemplateParser.JinjaAtomTrailerContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaAtomTrailer}.
	 * @param ctx the parse tree
	 */
	void exitJinjaAtomTrailer(TemplateParser.JinjaAtomTrailerContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaTrailer}.
	 * @param ctx the parse tree
	 */
	void enterJinjaTrailer(TemplateParser.JinjaTrailerContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaTrailer}.
	 * @param ctx the parse tree
	 */
	void exitJinjaTrailer(TemplateParser.JinjaTrailerContext ctx);
	/**
	 * Enter a parse tree produced by {@link TemplateParser#jinjaAtom}.
	 * @param ctx the parse tree
	 */
	void enterJinjaAtom(TemplateParser.JinjaAtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link TemplateParser#jinjaAtom}.
	 * @param ctx the parse tree
	 */
	void exitJinjaAtom(TemplateParser.JinjaAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaCommentRule}
	 * labeled alternative in {@link TemplateParser#jinjaComment}.
	 * @param ctx the parse tree
	 */
	void enterJinjaCommentRule(TemplateParser.JinjaCommentRuleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaCommentRule}
	 * labeled alternative in {@link TemplateParser#jinjaComment}.
	 * @param ctx the parse tree
	 */
	void exitJinjaCommentRule(TemplateParser.JinjaCommentRuleContext ctx);
}