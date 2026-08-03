// Generated from grammar/template/TemplateParser.g4 by ANTLR 4.13.1
package grammar.template;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class TemplateParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		JINJA_BLOCK_OPEN=1, JINJA_EXPR_OPEN=2, JINJA_COMMENT_OPEN=3, TAG_OPEN=4, 
		HTML_DOCTYPE=5, HTML_COMMENT=6, HTML_CONDITIONAL_COMMENT=7, CDATA=8, STYLE_OPEN=9, 
		SEA_WS=10, HTML_TEXT=11, TAG_SLASH_CLOSE=12, TAG_CLOSE=13, TAG_SLASH=14, 
		TAG_EQUALS=15, TAG_NAME=16, ATTVALUE_VALUE=17, TAG_WHITESPACE=18, JINJA_BLOCK_CLOSE=19, 
		JINJA_EXPR_CLOSE=20, JJ_WS=21, JJ_IF=22, JJ_ELIF=23, JJ_ELSE=24, JJ_ENDIF=25, 
		JJ_FOR=26, JJ_IN=27, JJ_ENDFOR=28, JJ_BLOCK=29, JJ_ENDBLOCK=30, JJ_MACRO=31, 
		JJ_ENDMACRO=32, JJ_EXTENDS=33, JJ_INCLUDE=34, JJ_NOT=35, JJ_AND=36, JJ_OR=37, 
		JJ_TRUE=38, JJ_FALSE=39, JJ_NONE=40, JJ_EQ=41, JJ_NEQ=42, JJ_LE=43, JJ_GE=44, 
		JJ_LT=45, JJ_GT=46, JJ_ASSIGN=47, JJ_PIPE=48, JJ_DOT=49, JJ_COMMA=50, 
		JJ_LPAREN=51, JJ_RPAREN=52, JJ_LBRACKET=53, JJ_RBRACKET=54, JJ_PLUS=55, 
		JJ_MINUS=56, JJ_STAR=57, JJ_SLASH=58, JJ_TILDE=59, JJ_NUMBER=60, JJ_STRING=61, 
		JJ_IDENTIFIER=62, JINJA_COMMENT_CLOSE=63, JINJA_COMMENT_CONTENT=64, JINJA_BLOCK_OPEN_IN_STYLE=65, 
		JINJA_EXPR_OPEN_IN_STYLE=66, JINJA_COMMENT_OPEN_IN_STYLE=67, STYLE_CLOSE=68, 
		CSS_COMMENT=69, CSS_LBRACE=70, CSS_RBRACE=71, CSS_LPAREN=72, CSS_RPAREN=73, 
		CSS_LBRACKET=74, CSS_RBRACKET=75, CSS_COLON=76, CSS_SEMICOLON=77, CSS_COMMA=78, 
		CSS_GREATER=79, CSS_PLUS=80, CSS_TILDE=81, CSS_PIPE=82, CSS_CARET=83, 
		CSS_DOLLAR=84, CSS_STAR=85, CSS_DOT=86, CSS_HASH=87, CSS_EQUALS=88, CSS_SLASH=89, 
		CSS_BANG=90, CSS_MINUS=91, CSS_AT_KEYWORD=92, CSS_CUSTOM_PROPERTY=93, 
		CSS_HEX_COLOR=94, CSS_PERCENTAGE=95, CSS_DIMENSION=96, CSS_NUMBER=97, 
		CSS_STRING=98, CSS_IDENT=99, CSS_WS=100, CSS_DELIM=101;
	public static final int
		RULE_htmlDocument = 0, RULE_htmlElements = 1, RULE_htmlElement = 2, RULE_htmlContent = 3, 
		RULE_htmlAttribute = 4, RULE_htmlChardata = 5, RULE_htmlMisc = 6, RULE_htmlDoctype = 7, 
		RULE_htmlComment = 8, RULE_style = 9, RULE_cssStylesheet = 10, RULE_cssStylesheetItem = 11, 
		RULE_cssRule = 12, RULE_cssAtRule = 13, RULE_cssAtRulePrelude = 14, RULE_cssQualifiedRule = 15, 
		RULE_cssSelectorList = 16, RULE_cssComma = 17, RULE_cssSelector = 18, 
		RULE_cssCombinator = 19, RULE_cssCompoundSelector = 20, RULE_cssTypeSelector = 21, 
		RULE_cssSimpleSelector = 22, RULE_cssIdSelector = 23, RULE_cssClassSelector = 24, 
		RULE_cssAttributeSelector = 25, RULE_cssAttributeMatcher = 26, RULE_cssAttributeValue = 27, 
		RULE_cssPseudoSelector = 28, RULE_cssBlock = 29, RULE_cssBlockItem = 30, 
		RULE_cssDeclaration = 31, RULE_cssPropertyName = 32, RULE_cssImportant = 33, 
		RULE_cssValueSequence = 34, RULE_cssComponentValue = 35, RULE_cssFunctionCall = 36, 
		RULE_cssFunctionArguments = 37, RULE_cssFunctionArgument = 38, RULE_cssColor = 39, 
		RULE_cssMeasurement = 40, RULE_cssIdent = 41, RULE_cssWhitespace = 42, 
		RULE_cssComment = 43, RULE_jinjaBlock = 44, RULE_jinjaTag = 45, RULE_jinjaForTargets = 46, 
		RULE_jinjaParamList = 47, RULE_jinjaExpr = 48, RULE_jinjaFilterCall = 49, 
		RULE_jinjaArgList = 50, RULE_jinjaOrExpr = 51, RULE_jinjaAndExpr = 52, 
		RULE_jinjaNotExpr = 53, RULE_jinjaComparisonExpr = 54, RULE_jinjaAdditiveExpr = 55, 
		RULE_jinjaMultiplicativeExpr = 56, RULE_jinjaFilteredPrimary = 57, RULE_jinjaPrimary = 58, 
		RULE_jinjaAtomTrailer = 59, RULE_jinjaTrailer = 60, RULE_jinjaAtom = 61, 
		RULE_jinjaComment = 62;
	private static String[] makeRuleNames() {
		return new String[] {
			"htmlDocument", "htmlElements", "htmlElement", "htmlContent", "htmlAttribute", 
			"htmlChardata", "htmlMisc", "htmlDoctype", "htmlComment", "style", "cssStylesheet", 
			"cssStylesheetItem", "cssRule", "cssAtRule", "cssAtRulePrelude", "cssQualifiedRule", 
			"cssSelectorList", "cssComma", "cssSelector", "cssCombinator", "cssCompoundSelector", 
			"cssTypeSelector", "cssSimpleSelector", "cssIdSelector", "cssClassSelector", 
			"cssAttributeSelector", "cssAttributeMatcher", "cssAttributeValue", "cssPseudoSelector", 
			"cssBlock", "cssBlockItem", "cssDeclaration", "cssPropertyName", "cssImportant", 
			"cssValueSequence", "cssComponentValue", "cssFunctionCall", "cssFunctionArguments", 
			"cssFunctionArgument", "cssColor", "cssMeasurement", "cssIdent", "cssWhitespace", 
			"cssComment", "jinjaBlock", "jinjaTag", "jinjaForTargets", "jinjaParamList", 
			"jinjaExpr", "jinjaFilterCall", "jinjaArgList", "jinjaOrExpr", "jinjaAndExpr", 
			"jinjaNotExpr", "jinjaComparisonExpr", "jinjaAdditiveExpr", "jinjaMultiplicativeExpr", 
			"jinjaFilteredPrimary", "jinjaPrimary", "jinjaAtomTrailer", "jinjaTrailer", 
			"jinjaAtom", "jinjaComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'/>'", null, null, null, null, null, null, "'%}'", "'}}'", null, "'if'", 
			"'elif'", "'else'", "'endif'", "'for'", "'in'", "'endfor'", "'block'", 
			"'endblock'", "'macro'", "'endmacro'", "'extends'", "'include'", "'not'", 
			"'and'", "'or'", null, null, null, "'=='", "'!='", "'<='", "'>='", null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "'#}'", null, null, null, null, null, null, 
			"'{'", "'}'", null, null, null, null, "':'", "';'", null, null, null, 
			null, null, "'^'", "'$'", null, null, "'#'", null, null, "'!'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "JINJA_BLOCK_OPEN", "JINJA_EXPR_OPEN", "JINJA_COMMENT_OPEN", "TAG_OPEN", 
			"HTML_DOCTYPE", "HTML_COMMENT", "HTML_CONDITIONAL_COMMENT", "CDATA", 
			"STYLE_OPEN", "SEA_WS", "HTML_TEXT", "TAG_SLASH_CLOSE", "TAG_CLOSE", 
			"TAG_SLASH", "TAG_EQUALS", "TAG_NAME", "ATTVALUE_VALUE", "TAG_WHITESPACE", 
			"JINJA_BLOCK_CLOSE", "JINJA_EXPR_CLOSE", "JJ_WS", "JJ_IF", "JJ_ELIF", 
			"JJ_ELSE", "JJ_ENDIF", "JJ_FOR", "JJ_IN", "JJ_ENDFOR", "JJ_BLOCK", "JJ_ENDBLOCK", 
			"JJ_MACRO", "JJ_ENDMACRO", "JJ_EXTENDS", "JJ_INCLUDE", "JJ_NOT", "JJ_AND", 
			"JJ_OR", "JJ_TRUE", "JJ_FALSE", "JJ_NONE", "JJ_EQ", "JJ_NEQ", "JJ_LE", 
			"JJ_GE", "JJ_LT", "JJ_GT", "JJ_ASSIGN", "JJ_PIPE", "JJ_DOT", "JJ_COMMA", 
			"JJ_LPAREN", "JJ_RPAREN", "JJ_LBRACKET", "JJ_RBRACKET", "JJ_PLUS", "JJ_MINUS", 
			"JJ_STAR", "JJ_SLASH", "JJ_TILDE", "JJ_NUMBER", "JJ_STRING", "JJ_IDENTIFIER", 
			"JINJA_COMMENT_CLOSE", "JINJA_COMMENT_CONTENT", "JINJA_BLOCK_OPEN_IN_STYLE", 
			"JINJA_EXPR_OPEN_IN_STYLE", "JINJA_COMMENT_OPEN_IN_STYLE", "STYLE_CLOSE", 
			"CSS_COMMENT", "CSS_LBRACE", "CSS_RBRACE", "CSS_LPAREN", "CSS_RPAREN", 
			"CSS_LBRACKET", "CSS_RBRACKET", "CSS_COLON", "CSS_SEMICOLON", "CSS_COMMA", 
			"CSS_GREATER", "CSS_PLUS", "CSS_TILDE", "CSS_PIPE", "CSS_CARET", "CSS_DOLLAR", 
			"CSS_STAR", "CSS_DOT", "CSS_HASH", "CSS_EQUALS", "CSS_SLASH", "CSS_BANG", 
			"CSS_MINUS", "CSS_AT_KEYWORD", "CSS_CUSTOM_PROPERTY", "CSS_HEX_COLOR", 
			"CSS_PERCENTAGE", "CSS_DIMENSION", "CSS_NUMBER", "CSS_STRING", "CSS_IDENT", 
			"CSS_WS", "CSS_DELIM"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TemplateParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TemplateParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlDocumentContext extends ParserRuleContext {
		public HtmlDocumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlDocument; }
	 
		public HtmlDocumentContext() { }
		public void copyFrom(HtmlDocumentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DocumentContext extends HtmlDocumentContext {
		public TerminalNode EOF() { return getToken(TemplateParser.EOF, 0); }
		public List<HtmlElementsContext> htmlElements() {
			return getRuleContexts(HtmlElementsContext.class);
		}
		public HtmlElementsContext htmlElements(int i) {
			return getRuleContext(HtmlElementsContext.class,i);
		}
		public DocumentContext(HtmlDocumentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterDocument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitDocument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitDocument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlDocumentContext htmlDocument() throws RecognitionException {
		HtmlDocumentContext _localctx = new HtmlDocumentContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_htmlDocument);
		int _la;
		try {
			_localctx = new DocumentContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 3838L) != 0)) {
				{
				{
				setState(126);
				htmlElements();
				}
				}
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(132);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlElementsContext extends ParserRuleContext {
		public HtmlElementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlElements; }
	 
		public HtmlElementsContext() { }
		public void copyFrom(HtmlElementsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ElementsContext extends HtmlElementsContext {
		public HtmlElementContext htmlElement() {
			return getRuleContext(HtmlElementContext.class,0);
		}
		public List<HtmlMiscContext> htmlMisc() {
			return getRuleContexts(HtmlMiscContext.class);
		}
		public HtmlMiscContext htmlMisc(int i) {
			return getRuleContext(HtmlMiscContext.class,i);
		}
		public ElementsContext(HtmlElementsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterElements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitElements(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitElements(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlElementsContext htmlElements() throws RecognitionException {
		HtmlElementsContext _localctx = new HtmlElementsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_htmlElements);
		int _la;
		try {
			int _alt;
			_localctx = new ElementsContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 3296L) != 0)) {
				{
				{
				setState(134);
				htmlMisc();
				}
				}
				setState(139);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(140);
			htmlElement();
			setState(144);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(141);
					htmlMisc();
					}
					} 
				}
				setState(146);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlElementContext extends ParserRuleContext {
		public HtmlElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlElement; }
	 
		public HtmlElementContext() { }
		public void copyFrom(HtmlElementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class JinjaCommentElementContext extends HtmlElementContext {
		public JinjaCommentContext jinjaComment() {
			return getRuleContext(JinjaCommentContext.class,0);
		}
		public JinjaCommentElementContext(HtmlElementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaCommentElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaCommentElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaCommentElement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class JinjaBlockElementContext extends HtmlElementContext {
		public JinjaBlockContext jinjaBlock() {
			return getRuleContext(JinjaBlockContext.class,0);
		}
		public JinjaBlockElementContext(HtmlElementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaBlockElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaBlockElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaBlockElement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class JinjaExprElementContext extends HtmlElementContext {
		public JinjaExprContext jinjaExpr() {
			return getRuleContext(JinjaExprContext.class,0);
		}
		public JinjaExprElementContext(HtmlElementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaExprElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaExprElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaExprElement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TagElementContext extends HtmlElementContext {
		public List<TerminalNode> TAG_OPEN() { return getTokens(TemplateParser.TAG_OPEN); }
		public TerminalNode TAG_OPEN(int i) {
			return getToken(TemplateParser.TAG_OPEN, i);
		}
		public List<TerminalNode> TAG_NAME() { return getTokens(TemplateParser.TAG_NAME); }
		public TerminalNode TAG_NAME(int i) {
			return getToken(TemplateParser.TAG_NAME, i);
		}
		public List<TerminalNode> TAG_CLOSE() { return getTokens(TemplateParser.TAG_CLOSE); }
		public TerminalNode TAG_CLOSE(int i) {
			return getToken(TemplateParser.TAG_CLOSE, i);
		}
		public TerminalNode TAG_SLASH_CLOSE() { return getToken(TemplateParser.TAG_SLASH_CLOSE, 0); }
		public List<HtmlAttributeContext> htmlAttribute() {
			return getRuleContexts(HtmlAttributeContext.class);
		}
		public HtmlAttributeContext htmlAttribute(int i) {
			return getRuleContext(HtmlAttributeContext.class,i);
		}
		public HtmlContentContext htmlContent() {
			return getRuleContext(HtmlContentContext.class,0);
		}
		public TerminalNode TAG_SLASH() { return getToken(TemplateParser.TAG_SLASH, 0); }
		public TagElementContext(HtmlElementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterTagElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitTagElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitTagElement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StyleElementContext extends HtmlElementContext {
		public StyleContext style() {
			return getRuleContext(StyleContext.class,0);
		}
		public StyleElementContext(HtmlElementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterStyleElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitStyleElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitStyleElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlElementContext htmlElement() throws RecognitionException {
		HtmlElementContext _localctx = new HtmlElementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_htmlElement);
		int _la;
		try {
			setState(171);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TAG_OPEN:
				_localctx = new TagElementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(147);
				match(TAG_OPEN);
				setState(148);
				match(TAG_NAME);
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==TAG_NAME) {
					{
					{
					setState(149);
					htmlAttribute();
					}
					}
					setState(154);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(165);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case TAG_CLOSE:
					{
					setState(155);
					match(TAG_CLOSE);
					setState(162);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
					case 1:
						{
						setState(156);
						htmlContent();
						setState(157);
						match(TAG_OPEN);
						setState(158);
						match(TAG_SLASH);
						setState(159);
						match(TAG_NAME);
						setState(160);
						match(TAG_CLOSE);
						}
						break;
					}
					}
					break;
				case TAG_SLASH_CLOSE:
					{
					setState(164);
					match(TAG_SLASH_CLOSE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case STYLE_OPEN:
				_localctx = new StyleElementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(167);
				style();
				}
				break;
			case JINJA_BLOCK_OPEN:
				_localctx = new JinjaBlockElementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(168);
				jinjaBlock();
				}
				break;
			case JINJA_EXPR_OPEN:
				_localctx = new JinjaExprElementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(169);
				jinjaExpr();
				}
				break;
			case JINJA_COMMENT_OPEN:
				_localctx = new JinjaCommentElementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(170);
				jinjaComment();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlContentContext extends ParserRuleContext {
		public HtmlContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlContent; }
	 
		public HtmlContentContext() { }
		public void copyFrom(HtmlContentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ContentContext extends HtmlContentContext {
		public List<HtmlChardataContext> htmlChardata() {
			return getRuleContexts(HtmlChardataContext.class);
		}
		public HtmlChardataContext htmlChardata(int i) {
			return getRuleContext(HtmlChardataContext.class,i);
		}
		public List<HtmlElementContext> htmlElement() {
			return getRuleContexts(HtmlElementContext.class);
		}
		public HtmlElementContext htmlElement(int i) {
			return getRuleContext(HtmlElementContext.class,i);
		}
		public List<TerminalNode> CDATA() { return getTokens(TemplateParser.CDATA); }
		public TerminalNode CDATA(int i) {
			return getToken(TemplateParser.CDATA, i);
		}
		public List<HtmlCommentContext> htmlComment() {
			return getRuleContexts(HtmlCommentContext.class);
		}
		public HtmlCommentContext htmlComment(int i) {
			return getRuleContext(HtmlCommentContext.class,i);
		}
		public List<JinjaBlockContext> jinjaBlock() {
			return getRuleContexts(JinjaBlockContext.class);
		}
		public JinjaBlockContext jinjaBlock(int i) {
			return getRuleContext(JinjaBlockContext.class,i);
		}
		public List<JinjaExprContext> jinjaExpr() {
			return getRuleContexts(JinjaExprContext.class);
		}
		public JinjaExprContext jinjaExpr(int i) {
			return getRuleContext(JinjaExprContext.class,i);
		}
		public List<JinjaCommentContext> jinjaComment() {
			return getRuleContexts(JinjaCommentContext.class);
		}
		public JinjaCommentContext jinjaComment(int i) {
			return getRuleContext(JinjaCommentContext.class,i);
		}
		public ContentContext(HtmlContentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlContentContext htmlContent() throws RecognitionException {
		HtmlContentContext _localctx = new HtmlContentContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_htmlContent);
		int _la;
		try {
			int _alt;
			_localctx = new ContentContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEA_WS || _la==HTML_TEXT) {
				{
				setState(173);
				htmlChardata();
				}
			}

			setState(189);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(182);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
					case 1:
						{
						setState(176);
						htmlElement();
						}
						break;
					case 2:
						{
						setState(177);
						match(CDATA);
						}
						break;
					case 3:
						{
						setState(178);
						htmlComment();
						}
						break;
					case 4:
						{
						setState(179);
						jinjaBlock();
						}
						break;
					case 5:
						{
						setState(180);
						jinjaExpr();
						}
						break;
					case 6:
						{
						setState(181);
						jinjaComment();
						}
						break;
					}
					setState(185);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==SEA_WS || _la==HTML_TEXT) {
						{
						setState(184);
						htmlChardata();
						}
					}

					}
					} 
				}
				setState(191);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlAttributeContext extends ParserRuleContext {
		public HtmlAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlAttribute; }
	 
		public HtmlAttributeContext() { }
		public void copyFrom(HtmlAttributeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AttributeContext extends HtmlAttributeContext {
		public TerminalNode TAG_NAME() { return getToken(TemplateParser.TAG_NAME, 0); }
		public TerminalNode TAG_EQUALS() { return getToken(TemplateParser.TAG_EQUALS, 0); }
		public TerminalNode ATTVALUE_VALUE() { return getToken(TemplateParser.ATTVALUE_VALUE, 0); }
		public AttributeContext(HtmlAttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlAttributeContext htmlAttribute() throws RecognitionException {
		HtmlAttributeContext _localctx = new HtmlAttributeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_htmlAttribute);
		int _la;
		try {
			_localctx = new AttributeContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			match(TAG_NAME);
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TAG_EQUALS) {
				{
				setState(193);
				match(TAG_EQUALS);
				setState(194);
				match(ATTVALUE_VALUE);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlChardataContext extends ParserRuleContext {
		public HtmlChardataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlChardata; }
	 
		public HtmlChardataContext() { }
		public void copyFrom(HtmlChardataContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WhitespaceContentContext extends HtmlChardataContext {
		public TerminalNode SEA_WS() { return getToken(TemplateParser.SEA_WS, 0); }
		public WhitespaceContentContext(HtmlChardataContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterWhitespaceContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitWhitespaceContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitWhitespaceContent(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TextContentContext extends HtmlChardataContext {
		public TerminalNode HTML_TEXT() { return getToken(TemplateParser.HTML_TEXT, 0); }
		public TextContentContext(HtmlChardataContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterTextContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitTextContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitTextContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlChardataContext htmlChardata() throws RecognitionException {
		HtmlChardataContext _localctx = new HtmlChardataContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_htmlChardata);
		try {
			setState(199);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HTML_TEXT:
				_localctx = new TextContentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(197);
				match(HTML_TEXT);
				}
				break;
			case SEA_WS:
				_localctx = new WhitespaceContentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(198);
				match(SEA_WS);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlMiscContext extends ParserRuleContext {
		public HtmlMiscContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlMisc; }
	 
		public HtmlMiscContext() { }
		public void copyFrom(HtmlMiscContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MiscDoctypeContext extends HtmlMiscContext {
		public HtmlDoctypeContext htmlDoctype() {
			return getRuleContext(HtmlDoctypeContext.class,0);
		}
		public MiscDoctypeContext(HtmlMiscContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterMiscDoctype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitMiscDoctype(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitMiscDoctype(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MiscTextContext extends HtmlMiscContext {
		public TerminalNode HTML_TEXT() { return getToken(TemplateParser.HTML_TEXT, 0); }
		public MiscTextContext(HtmlMiscContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterMiscText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitMiscText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitMiscText(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MiscCommentContext extends HtmlMiscContext {
		public HtmlCommentContext htmlComment() {
			return getRuleContext(HtmlCommentContext.class,0);
		}
		public MiscCommentContext(HtmlMiscContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterMiscComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitMiscComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitMiscComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MiscWhitespaceContext extends HtmlMiscContext {
		public TerminalNode SEA_WS() { return getToken(TemplateParser.SEA_WS, 0); }
		public MiscWhitespaceContext(HtmlMiscContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterMiscWhitespace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitMiscWhitespace(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitMiscWhitespace(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlMiscContext htmlMisc() throws RecognitionException {
		HtmlMiscContext _localctx = new HtmlMiscContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_htmlMisc);
		try {
			setState(205);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HTML_DOCTYPE:
				_localctx = new MiscDoctypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(201);
				htmlDoctype();
				}
				break;
			case HTML_COMMENT:
			case HTML_CONDITIONAL_COMMENT:
				_localctx = new MiscCommentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(202);
				htmlComment();
				}
				break;
			case SEA_WS:
				_localctx = new MiscWhitespaceContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(203);
				match(SEA_WS);
				}
				break;
			case HTML_TEXT:
				_localctx = new MiscTextContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(204);
				match(HTML_TEXT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlDoctypeContext extends ParserRuleContext {
		public TerminalNode HTML_DOCTYPE() { return getToken(TemplateParser.HTML_DOCTYPE, 0); }
		public HtmlDoctypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlDoctype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterHtmlDoctype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitHtmlDoctype(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitHtmlDoctype(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlDoctypeContext htmlDoctype() throws RecognitionException {
		HtmlDoctypeContext _localctx = new HtmlDoctypeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_htmlDoctype);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(HTML_DOCTYPE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HtmlCommentContext extends ParserRuleContext {
		public HtmlCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_htmlComment; }
	 
		public HtmlCommentContext() { }
		public void copyFrom(HtmlCommentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StandardCommentContext extends HtmlCommentContext {
		public TerminalNode HTML_COMMENT() { return getToken(TemplateParser.HTML_COMMENT, 0); }
		public StandardCommentContext(HtmlCommentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterStandardComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitStandardComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitStandardComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConditionalCommentContext extends HtmlCommentContext {
		public TerminalNode HTML_CONDITIONAL_COMMENT() { return getToken(TemplateParser.HTML_CONDITIONAL_COMMENT, 0); }
		public ConditionalCommentContext(HtmlCommentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterConditionalComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitConditionalComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitConditionalComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HtmlCommentContext htmlComment() throws RecognitionException {
		HtmlCommentContext _localctx = new HtmlCommentContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_htmlComment);
		try {
			setState(211);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HTML_COMMENT:
				_localctx = new StandardCommentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(209);
				match(HTML_COMMENT);
				}
				break;
			case HTML_CONDITIONAL_COMMENT:
				_localctx = new ConditionalCommentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(210);
				match(HTML_CONDITIONAL_COMMENT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StyleContext extends ParserRuleContext {
		public StyleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_style; }
	 
		public StyleContext() { }
		public void copyFrom(StyleContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StyleBlockContext extends StyleContext {
		public TerminalNode STYLE_OPEN() { return getToken(TemplateParser.STYLE_OPEN, 0); }
		public CssStylesheetContext cssStylesheet() {
			return getRuleContext(CssStylesheetContext.class,0);
		}
		public TerminalNode STYLE_CLOSE() { return getToken(TemplateParser.STYLE_CLOSE, 0); }
		public StyleBlockContext(StyleContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterStyleBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitStyleBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitStyleBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StyleContext style() throws RecognitionException {
		StyleContext _localctx = new StyleContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_style);
		try {
			_localctx = new StyleBlockContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(STYLE_OPEN);
			setState(214);
			cssStylesheet();
			setState(215);
			match(STYLE_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssStylesheetContext extends ParserRuleContext {
		public List<CssStylesheetItemContext> cssStylesheetItem() {
			return getRuleContexts(CssStylesheetItemContext.class);
		}
		public CssStylesheetItemContext cssStylesheetItem(int i) {
			return getRuleContext(CssStylesheetItemContext.class,i);
		}
		public CssStylesheetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssStylesheet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssStylesheet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssStylesheet(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssStylesheet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssStylesheetContext cssStylesheet() throws RecognitionException {
		CssStylesheetContext _localctx = new CssStylesheetContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_cssStylesheet);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 3246857891L) != 0)) {
				{
				{
				setState(217);
				cssStylesheetItem();
				}
				}
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssStylesheetItemContext extends ParserRuleContext {
		public CssWhitespaceContext cssWhitespace() {
			return getRuleContext(CssWhitespaceContext.class,0);
		}
		public CssCommentContext cssComment() {
			return getRuleContext(CssCommentContext.class,0);
		}
		public CssRuleContext cssRule() {
			return getRuleContext(CssRuleContext.class,0);
		}
		public CssStylesheetItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssStylesheetItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssStylesheetItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssStylesheetItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssStylesheetItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssStylesheetItemContext cssStylesheetItem() throws RecognitionException {
		CssStylesheetItemContext _localctx = new CssStylesheetItemContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_cssStylesheetItem);
		try {
			setState(226);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(223);
				cssWhitespace();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(224);
				cssComment();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(225);
				cssRule();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssRuleContext extends ParserRuleContext {
		public CssAtRuleContext cssAtRule() {
			return getRuleContext(CssAtRuleContext.class,0);
		}
		public CssQualifiedRuleContext cssQualifiedRule() {
			return getRuleContext(CssQualifiedRuleContext.class,0);
		}
		public CssRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssRuleContext cssRule() throws RecognitionException {
		CssRuleContext _localctx = new CssRuleContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_cssRule);
		try {
			setState(230);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CSS_AT_KEYWORD:
				enterOuterAlt(_localctx, 1);
				{
				setState(228);
				cssAtRule();
				}
				break;
			case CSS_LBRACE:
			case CSS_LBRACKET:
			case CSS_COLON:
			case CSS_COMMA:
			case CSS_GREATER:
			case CSS_PLUS:
			case CSS_TILDE:
			case CSS_STAR:
			case CSS_DOT:
			case CSS_HASH:
			case CSS_CUSTOM_PROPERTY:
			case CSS_IDENT:
			case CSS_WS:
				enterOuterAlt(_localctx, 2);
				{
				setState(229);
				cssQualifiedRule();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssAtRuleContext extends ParserRuleContext {
		public TerminalNode CSS_AT_KEYWORD() { return getToken(TemplateParser.CSS_AT_KEYWORD, 0); }
		public CssBlockContext cssBlock() {
			return getRuleContext(CssBlockContext.class,0);
		}
		public TerminalNode CSS_SEMICOLON() { return getToken(TemplateParser.CSS_SEMICOLON, 0); }
		public List<CssAtRulePreludeContext> cssAtRulePrelude() {
			return getRuleContexts(CssAtRulePreludeContext.class);
		}
		public CssAtRulePreludeContext cssAtRulePrelude(int i) {
			return getRuleContext(CssAtRulePreludeContext.class,i);
		}
		public List<CssWhitespaceContext> cssWhitespace() {
			return getRuleContexts(CssWhitespaceContext.class);
		}
		public CssWhitespaceContext cssWhitespace(int i) {
			return getRuleContext(CssWhitespaceContext.class,i);
		}
		public CssAtRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssAtRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssAtRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssAtRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssAtRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssAtRuleContext cssAtRule() throws RecognitionException {
		CssAtRuleContext _localctx = new CssAtRuleContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_cssAtRule);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			match(CSS_AT_KEYWORD);
			setState(239);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(234);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==CSS_WS) {
						{
						setState(233);
						cssWhitespace();
						}
					}

					setState(236);
					cssAtRulePrelude();
					}
					} 
				}
				setState(241);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CSS_WS) {
				{
				setState(242);
				cssWhitespace();
				}
			}

			setState(247);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CSS_LBRACE:
				{
				setState(245);
				cssBlock();
				}
				break;
			case CSS_SEMICOLON:
				{
				setState(246);
				match(CSS_SEMICOLON);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssAtRulePreludeContext extends ParserRuleContext {
		public CssComponentValueContext cssComponentValue() {
			return getRuleContext(CssComponentValueContext.class,0);
		}
		public CssAtRulePreludeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssAtRulePrelude; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssAtRulePrelude(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssAtRulePrelude(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssAtRulePrelude(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssAtRulePreludeContext cssAtRulePrelude() throws RecognitionException {
		CssAtRulePreludeContext _localctx = new CssAtRulePreludeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_cssAtRulePrelude);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			cssComponentValue();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssQualifiedRuleContext extends ParserRuleContext {
		public CssSelectorListContext cssSelectorList() {
			return getRuleContext(CssSelectorListContext.class,0);
		}
		public CssBlockContext cssBlock() {
			return getRuleContext(CssBlockContext.class,0);
		}
		public CssWhitespaceContext cssWhitespace() {
			return getRuleContext(CssWhitespaceContext.class,0);
		}
		public CssQualifiedRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssQualifiedRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssQualifiedRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssQualifiedRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssQualifiedRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssQualifiedRuleContext cssQualifiedRule() throws RecognitionException {
		CssQualifiedRuleContext _localctx = new CssQualifiedRuleContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_cssQualifiedRule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			cssSelectorList();
			setState(253);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CSS_WS) {
				{
				setState(252);
				cssWhitespace();
				}
			}

			setState(255);
			cssBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssSelectorListContext extends ParserRuleContext {
		public List<CssSelectorContext> cssSelector() {
			return getRuleContexts(CssSelectorContext.class);
		}
		public CssSelectorContext cssSelector(int i) {
			return getRuleContext(CssSelectorContext.class,i);
		}
		public List<CssCommaContext> cssComma() {
			return getRuleContexts(CssCommaContext.class);
		}
		public CssCommaContext cssComma(int i) {
			return getRuleContext(CssCommaContext.class,i);
		}
		public List<CssWhitespaceContext> cssWhitespace() {
			return getRuleContexts(CssWhitespaceContext.class);
		}
		public CssWhitespaceContext cssWhitespace(int i) {
			return getRuleContext(CssWhitespaceContext.class,i);
		}
		public CssSelectorListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssSelectorList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssSelectorList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssSelectorList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssSelectorList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssSelectorListContext cssSelectorList() throws RecognitionException {
		CssSelectorListContext _localctx = new CssSelectorListContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_cssSelectorList);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			cssSelector();
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CSS_COMMA) {
				{
				{
				setState(258);
				cssComma();
				setState(262);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(259);
						cssWhitespace();
						}
						} 
					}
					setState(264);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
				}
				setState(265);
				cssSelector();
				}
				}
				setState(271);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssCommaContext extends ParserRuleContext {
		public TerminalNode CSS_COMMA() { return getToken(TemplateParser.CSS_COMMA, 0); }
		public CssCommaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssComma; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssComma(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssComma(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssComma(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssCommaContext cssComma() throws RecognitionException {
		CssCommaContext _localctx = new CssCommaContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_cssComma);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(272);
			match(CSS_COMMA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssSelectorContext extends ParserRuleContext {
		public List<CssCompoundSelectorContext> cssCompoundSelector() {
			return getRuleContexts(CssCompoundSelectorContext.class);
		}
		public CssCompoundSelectorContext cssCompoundSelector(int i) {
			return getRuleContext(CssCompoundSelectorContext.class,i);
		}
		public List<CssCombinatorContext> cssCombinator() {
			return getRuleContexts(CssCombinatorContext.class);
		}
		public CssCombinatorContext cssCombinator(int i) {
			return getRuleContext(CssCombinatorContext.class,i);
		}
		public List<CssWhitespaceContext> cssWhitespace() {
			return getRuleContexts(CssWhitespaceContext.class);
		}
		public CssWhitespaceContext cssWhitespace(int i) {
			return getRuleContext(CssWhitespaceContext.class,i);
		}
		public CssSelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssSelector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssSelectorContext cssSelector() throws RecognitionException {
		CssSelectorContext _localctx = new CssSelectorContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_cssSelector);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			cssCompoundSelector();
			setState(299);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(297);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
					case 1:
						{
						setState(276); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(275);
								cssWhitespace();
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(278); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						setState(280);
						cssCompoundSelector();
						}
						break;
					case 2:
						{
						setState(285);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==CSS_WS) {
							{
							{
							setState(282);
							cssWhitespace();
							}
							}
							setState(287);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(288);
						cssCombinator();
						setState(292);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
						while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
							if ( _alt==1 ) {
								{
								{
								setState(289);
								cssWhitespace();
								}
								} 
							}
							setState(294);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
						}
						setState(295);
						cssCompoundSelector();
						}
						break;
					}
					} 
				}
				setState(301);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssCombinatorContext extends ParserRuleContext {
		public TerminalNode CSS_GREATER() { return getToken(TemplateParser.CSS_GREATER, 0); }
		public TerminalNode CSS_PLUS() { return getToken(TemplateParser.CSS_PLUS, 0); }
		public TerminalNode CSS_TILDE() { return getToken(TemplateParser.CSS_TILDE, 0); }
		public CssCombinatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssCombinator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssCombinator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssCombinator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssCombinator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssCombinatorContext cssCombinator() throws RecognitionException {
		CssCombinatorContext _localctx = new CssCombinatorContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_cssCombinator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			_la = _input.LA(1);
			if ( !(((((_la - 79)) & ~0x3f) == 0 && ((1L << (_la - 79)) & 7L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssCompoundSelectorContext extends ParserRuleContext {
		public CssTypeSelectorContext cssTypeSelector() {
			return getRuleContext(CssTypeSelectorContext.class,0);
		}
		public List<CssSimpleSelectorContext> cssSimpleSelector() {
			return getRuleContexts(CssSimpleSelectorContext.class);
		}
		public CssSimpleSelectorContext cssSimpleSelector(int i) {
			return getRuleContext(CssSimpleSelectorContext.class,i);
		}
		public CssCompoundSelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssCompoundSelector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssCompoundSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssCompoundSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssCompoundSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssCompoundSelectorContext cssCompoundSelector() throws RecognitionException {
		CssCompoundSelectorContext _localctx = new CssCompoundSelectorContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_cssCompoundSelector);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 85)) & ~0x3f) == 0 && ((1L << (_la - 85)) & 16641L) != 0)) {
				{
				setState(304);
				cssTypeSelector();
				}
			}

			setState(310);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & 12293L) != 0)) {
				{
				{
				setState(307);
				cssSimpleSelector();
				}
				}
				setState(312);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssTypeSelectorContext extends ParserRuleContext {
		public TerminalNode CSS_STAR() { return getToken(TemplateParser.CSS_STAR, 0); }
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public CssTypeSelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssTypeSelector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssTypeSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssTypeSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssTypeSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssTypeSelectorContext cssTypeSelector() throws RecognitionException {
		CssTypeSelectorContext _localctx = new CssTypeSelectorContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_cssTypeSelector);
		try {
			setState(315);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CSS_STAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(313);
				match(CSS_STAR);
				}
				break;
			case CSS_CUSTOM_PROPERTY:
			case CSS_IDENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(314);
				cssIdent();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssSimpleSelectorContext extends ParserRuleContext {
		public CssIdSelectorContext cssIdSelector() {
			return getRuleContext(CssIdSelectorContext.class,0);
		}
		public CssClassSelectorContext cssClassSelector() {
			return getRuleContext(CssClassSelectorContext.class,0);
		}
		public CssAttributeSelectorContext cssAttributeSelector() {
			return getRuleContext(CssAttributeSelectorContext.class,0);
		}
		public CssPseudoSelectorContext cssPseudoSelector() {
			return getRuleContext(CssPseudoSelectorContext.class,0);
		}
		public CssSimpleSelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssSimpleSelector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssSimpleSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssSimpleSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssSimpleSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssSimpleSelectorContext cssSimpleSelector() throws RecognitionException {
		CssSimpleSelectorContext _localctx = new CssSimpleSelectorContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_cssSimpleSelector);
		try {
			setState(321);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CSS_HASH:
				enterOuterAlt(_localctx, 1);
				{
				setState(317);
				cssIdSelector();
				}
				break;
			case CSS_DOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(318);
				cssClassSelector();
				}
				break;
			case CSS_LBRACKET:
				enterOuterAlt(_localctx, 3);
				{
				setState(319);
				cssAttributeSelector();
				}
				break;
			case CSS_COLON:
				enterOuterAlt(_localctx, 4);
				{
				setState(320);
				cssPseudoSelector();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssIdSelectorContext extends ParserRuleContext {
		public TerminalNode CSS_HASH() { return getToken(TemplateParser.CSS_HASH, 0); }
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public CssIdSelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssIdSelector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssIdSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssIdSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssIdSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssIdSelectorContext cssIdSelector() throws RecognitionException {
		CssIdSelectorContext _localctx = new CssIdSelectorContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_cssIdSelector);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(323);
			match(CSS_HASH);
			setState(324);
			cssIdent();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssClassSelectorContext extends ParserRuleContext {
		public TerminalNode CSS_DOT() { return getToken(TemplateParser.CSS_DOT, 0); }
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public CssClassSelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssClassSelector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssClassSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssClassSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssClassSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssClassSelectorContext cssClassSelector() throws RecognitionException {
		CssClassSelectorContext _localctx = new CssClassSelectorContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_cssClassSelector);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			match(CSS_DOT);
			setState(327);
			cssIdent();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssAttributeSelectorContext extends ParserRuleContext {
		public TerminalNode CSS_LBRACKET() { return getToken(TemplateParser.CSS_LBRACKET, 0); }
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public TerminalNode CSS_RBRACKET() { return getToken(TemplateParser.CSS_RBRACKET, 0); }
		public List<CssWhitespaceContext> cssWhitespace() {
			return getRuleContexts(CssWhitespaceContext.class);
		}
		public CssWhitespaceContext cssWhitespace(int i) {
			return getRuleContext(CssWhitespaceContext.class,i);
		}
		public CssAttributeMatcherContext cssAttributeMatcher() {
			return getRuleContext(CssAttributeMatcherContext.class,0);
		}
		public CssAttributeValueContext cssAttributeValue() {
			return getRuleContext(CssAttributeValueContext.class,0);
		}
		public CssAttributeSelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssAttributeSelector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssAttributeSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssAttributeSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssAttributeSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssAttributeSelectorContext cssAttributeSelector() throws RecognitionException {
		CssAttributeSelectorContext _localctx = new CssAttributeSelectorContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_cssAttributeSelector);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(329);
			match(CSS_LBRACKET);
			setState(333);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CSS_WS) {
				{
				{
				setState(330);
				cssWhitespace();
				}
				}
				setState(335);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(336);
			cssIdent();
			setState(352);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(340);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CSS_WS) {
					{
					{
					setState(337);
					cssWhitespace();
					}
					}
					setState(342);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(343);
				cssAttributeMatcher();
				setState(347);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CSS_WS) {
					{
					{
					setState(344);
					cssWhitespace();
					}
					}
					setState(349);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(350);
				cssAttributeValue();
				}
				break;
			}
			setState(357);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CSS_WS) {
				{
				{
				setState(354);
				cssWhitespace();
				}
				}
				setState(359);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(360);
			match(CSS_RBRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssAttributeMatcherContext extends ParserRuleContext {
		public TerminalNode CSS_EQUALS() { return getToken(TemplateParser.CSS_EQUALS, 0); }
		public TerminalNode CSS_TILDE() { return getToken(TemplateParser.CSS_TILDE, 0); }
		public TerminalNode CSS_PIPE() { return getToken(TemplateParser.CSS_PIPE, 0); }
		public TerminalNode CSS_CARET() { return getToken(TemplateParser.CSS_CARET, 0); }
		public TerminalNode CSS_DOLLAR() { return getToken(TemplateParser.CSS_DOLLAR, 0); }
		public TerminalNode CSS_STAR() { return getToken(TemplateParser.CSS_STAR, 0); }
		public CssAttributeMatcherContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssAttributeMatcher; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssAttributeMatcher(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssAttributeMatcher(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssAttributeMatcher(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssAttributeMatcherContext cssAttributeMatcher() throws RecognitionException {
		CssAttributeMatcherContext _localctx = new CssAttributeMatcherContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_cssAttributeMatcher);
		try {
			setState(373);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CSS_EQUALS:
				enterOuterAlt(_localctx, 1);
				{
				setState(362);
				match(CSS_EQUALS);
				}
				break;
			case CSS_TILDE:
				enterOuterAlt(_localctx, 2);
				{
				setState(363);
				match(CSS_TILDE);
				setState(364);
				match(CSS_EQUALS);
				}
				break;
			case CSS_PIPE:
				enterOuterAlt(_localctx, 3);
				{
				setState(365);
				match(CSS_PIPE);
				setState(366);
				match(CSS_EQUALS);
				}
				break;
			case CSS_CARET:
				enterOuterAlt(_localctx, 4);
				{
				setState(367);
				match(CSS_CARET);
				setState(368);
				match(CSS_EQUALS);
				}
				break;
			case CSS_DOLLAR:
				enterOuterAlt(_localctx, 5);
				{
				setState(369);
				match(CSS_DOLLAR);
				setState(370);
				match(CSS_EQUALS);
				}
				break;
			case CSS_STAR:
				enterOuterAlt(_localctx, 6);
				{
				setState(371);
				match(CSS_STAR);
				setState(372);
				match(CSS_EQUALS);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssAttributeValueContext extends ParserRuleContext {
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public TerminalNode CSS_STRING() { return getToken(TemplateParser.CSS_STRING, 0); }
		public CssAttributeValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssAttributeValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssAttributeValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssAttributeValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssAttributeValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssAttributeValueContext cssAttributeValue() throws RecognitionException {
		CssAttributeValueContext _localctx = new CssAttributeValueContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_cssAttributeValue);
		try {
			setState(377);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CSS_CUSTOM_PROPERTY:
			case CSS_IDENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(375);
				cssIdent();
				}
				break;
			case CSS_STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(376);
				match(CSS_STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssPseudoSelectorContext extends ParserRuleContext {
		public List<TerminalNode> CSS_COLON() { return getTokens(TemplateParser.CSS_COLON); }
		public TerminalNode CSS_COLON(int i) {
			return getToken(TemplateParser.CSS_COLON, i);
		}
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public TerminalNode CSS_LPAREN() { return getToken(TemplateParser.CSS_LPAREN, 0); }
		public TerminalNode CSS_RPAREN() { return getToken(TemplateParser.CSS_RPAREN, 0); }
		public List<CssComponentValueContext> cssComponentValue() {
			return getRuleContexts(CssComponentValueContext.class);
		}
		public CssComponentValueContext cssComponentValue(int i) {
			return getRuleContext(CssComponentValueContext.class,i);
		}
		public CssPseudoSelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssPseudoSelector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssPseudoSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssPseudoSelector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssPseudoSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssPseudoSelectorContext cssPseudoSelector() throws RecognitionException {
		CssPseudoSelectorContext _localctx = new CssPseudoSelectorContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_cssPseudoSelector);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			match(CSS_COLON);
			setState(381);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CSS_COLON) {
				{
				setState(380);
				match(CSS_COLON);
				}
			}

			setState(383);
			cssIdent();
			setState(392);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CSS_LPAREN) {
				{
				setState(384);
				match(CSS_LPAREN);
				setState(388);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,42,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(385);
						cssComponentValue();
						}
						} 
					}
					setState(390);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,42,_ctx);
				}
				setState(391);
				match(CSS_RPAREN);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssBlockContext extends ParserRuleContext {
		public TerminalNode CSS_LBRACE() { return getToken(TemplateParser.CSS_LBRACE, 0); }
		public TerminalNode CSS_RBRACE() { return getToken(TemplateParser.CSS_RBRACE, 0); }
		public List<CssBlockItemContext> cssBlockItem() {
			return getRuleContexts(CssBlockItemContext.class);
		}
		public CssBlockItemContext cssBlockItem(int i) {
			return getRuleContext(CssBlockItemContext.class,i);
		}
		public CssBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssBlockContext cssBlock() throws RecognitionException {
		CssBlockContext _localctx = new CssBlockContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_cssBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(394);
			match(CSS_LBRACE);
			setState(398);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 3251052195L) != 0)) {
				{
				{
				setState(395);
				cssBlockItem();
				}
				}
				setState(400);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(401);
			match(CSS_RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssBlockItemContext extends ParserRuleContext {
		public CssWhitespaceContext cssWhitespace() {
			return getRuleContext(CssWhitespaceContext.class,0);
		}
		public CssCommentContext cssComment() {
			return getRuleContext(CssCommentContext.class,0);
		}
		public CssDeclarationContext cssDeclaration() {
			return getRuleContext(CssDeclarationContext.class,0);
		}
		public CssRuleContext cssRule() {
			return getRuleContext(CssRuleContext.class,0);
		}
		public CssBlockItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssBlockItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssBlockItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssBlockItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssBlockItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssBlockItemContext cssBlockItem() throws RecognitionException {
		CssBlockItemContext _localctx = new CssBlockItemContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_cssBlockItem);
		try {
			setState(407);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(403);
				cssWhitespace();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(404);
				cssComment();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(405);
				cssDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(406);
				cssRule();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssDeclarationContext extends ParserRuleContext {
		public CssPropertyNameContext cssPropertyName() {
			return getRuleContext(CssPropertyNameContext.class,0);
		}
		public TerminalNode CSS_COLON() { return getToken(TemplateParser.CSS_COLON, 0); }
		public CssValueSequenceContext cssValueSequence() {
			return getRuleContext(CssValueSequenceContext.class,0);
		}
		public List<CssWhitespaceContext> cssWhitespace() {
			return getRuleContexts(CssWhitespaceContext.class);
		}
		public CssWhitespaceContext cssWhitespace(int i) {
			return getRuleContext(CssWhitespaceContext.class,i);
		}
		public CssImportantContext cssImportant() {
			return getRuleContext(CssImportantContext.class,0);
		}
		public TerminalNode CSS_SEMICOLON() { return getToken(TemplateParser.CSS_SEMICOLON, 0); }
		public CssDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssDeclarationContext cssDeclaration() throws RecognitionException {
		CssDeclarationContext _localctx = new CssDeclarationContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_cssDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			cssPropertyName();
			setState(411);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CSS_WS) {
				{
				setState(410);
				cssWhitespace();
				}
			}

			setState(413);
			match(CSS_COLON);
			setState(415);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CSS_WS) {
				{
				setState(414);
				cssWhitespace();
				}
			}

			setState(417);
			cssValueSequence();
			setState(419);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				{
				setState(418);
				cssWhitespace();
				}
				break;
			}
			setState(422);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CSS_BANG) {
				{
				setState(421);
				cssImportant();
				}
			}

			setState(425);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				{
				setState(424);
				cssWhitespace();
				}
				break;
			}
			setState(428);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CSS_SEMICOLON) {
				{
				setState(427);
				match(CSS_SEMICOLON);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssPropertyNameContext extends ParserRuleContext {
		public TerminalNode CSS_CUSTOM_PROPERTY() { return getToken(TemplateParser.CSS_CUSTOM_PROPERTY, 0); }
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public TerminalNode CSS_MINUS() { return getToken(TemplateParser.CSS_MINUS, 0); }
		public CssPropertyNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssPropertyName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssPropertyName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssPropertyName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssPropertyNameContext cssPropertyName() throws RecognitionException {
		CssPropertyNameContext _localctx = new CssPropertyNameContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_cssPropertyName);
		int _la;
		try {
			setState(435);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(430);
				match(CSS_CUSTOM_PROPERTY);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(432);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CSS_MINUS) {
					{
					setState(431);
					match(CSS_MINUS);
					}
				}

				setState(434);
				cssIdent();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssImportantContext extends ParserRuleContext {
		public TerminalNode CSS_BANG() { return getToken(TemplateParser.CSS_BANG, 0); }
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public CssImportantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssImportant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssImportant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssImportant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssImportant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssImportantContext cssImportant() throws RecognitionException {
		CssImportantContext _localctx = new CssImportantContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_cssImportant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(437);
			match(CSS_BANG);
			setState(438);
			cssIdent();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssValueSequenceContext extends ParserRuleContext {
		public List<CssComponentValueContext> cssComponentValue() {
			return getRuleContexts(CssComponentValueContext.class);
		}
		public CssComponentValueContext cssComponentValue(int i) {
			return getRuleContext(CssComponentValueContext.class,i);
		}
		public List<CssCommaContext> cssComma() {
			return getRuleContexts(CssCommaContext.class);
		}
		public CssCommaContext cssComma(int i) {
			return getRuleContext(CssCommaContext.class,i);
		}
		public List<CssWhitespaceContext> cssWhitespace() {
			return getRuleContexts(CssWhitespaceContext.class);
		}
		public CssWhitespaceContext cssWhitespace(int i) {
			return getRuleContext(CssWhitespaceContext.class,i);
		}
		public CssValueSequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssValueSequence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssValueSequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssValueSequence(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssValueSequence(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssValueSequenceContext cssValueSequence() throws RecognitionException {
		CssValueSequenceContext _localctx = new CssValueSequenceContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_cssValueSequence);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			cssComponentValue();
			setState(459);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(457);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case CSS_WS:
						{
						setState(442); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(441);
							cssWhitespace();
							}
							}
							setState(444); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==CSS_WS );
						setState(446);
						cssComponentValue();
						}
						break;
					case CSS_COMMA:
						{
						setState(448);
						cssComma();
						setState(452);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==CSS_WS) {
							{
							{
							setState(449);
							cssWhitespace();
							}
							}
							setState(454);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(455);
						cssComponentValue();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(461);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,57,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssComponentValueContext extends ParserRuleContext {
		public CssFunctionCallContext cssFunctionCall() {
			return getRuleContext(CssFunctionCallContext.class,0);
		}
		public CssColorContext cssColor() {
			return getRuleContext(CssColorContext.class,0);
		}
		public CssMeasurementContext cssMeasurement() {
			return getRuleContext(CssMeasurementContext.class,0);
		}
		public TerminalNode CSS_STRING() { return getToken(TemplateParser.CSS_STRING, 0); }
		public TerminalNode CSS_NUMBER() { return getToken(TemplateParser.CSS_NUMBER, 0); }
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public TerminalNode CSS_HASH() { return getToken(TemplateParser.CSS_HASH, 0); }
		public TerminalNode CSS_DOT() { return getToken(TemplateParser.CSS_DOT, 0); }
		public TerminalNode CSS_COMMA() { return getToken(TemplateParser.CSS_COMMA, 0); }
		public TerminalNode CSS_COLON() { return getToken(TemplateParser.CSS_COLON, 0); }
		public TerminalNode CSS_SEMICOLON() { return getToken(TemplateParser.CSS_SEMICOLON, 0); }
		public TerminalNode CSS_SLASH() { return getToken(TemplateParser.CSS_SLASH, 0); }
		public TerminalNode CSS_PLUS() { return getToken(TemplateParser.CSS_PLUS, 0); }
		public TerminalNode CSS_GREATER() { return getToken(TemplateParser.CSS_GREATER, 0); }
		public TerminalNode CSS_TILDE() { return getToken(TemplateParser.CSS_TILDE, 0); }
		public TerminalNode CSS_PIPE() { return getToken(TemplateParser.CSS_PIPE, 0); }
		public TerminalNode CSS_STAR() { return getToken(TemplateParser.CSS_STAR, 0); }
		public TerminalNode CSS_EQUALS() { return getToken(TemplateParser.CSS_EQUALS, 0); }
		public TerminalNode CSS_LPAREN() { return getToken(TemplateParser.CSS_LPAREN, 0); }
		public TerminalNode CSS_RPAREN() { return getToken(TemplateParser.CSS_RPAREN, 0); }
		public TerminalNode CSS_LBRACKET() { return getToken(TemplateParser.CSS_LBRACKET, 0); }
		public TerminalNode CSS_RBRACKET() { return getToken(TemplateParser.CSS_RBRACKET, 0); }
		public TerminalNode CSS_LBRACE() { return getToken(TemplateParser.CSS_LBRACE, 0); }
		public TerminalNode CSS_RBRACE() { return getToken(TemplateParser.CSS_RBRACE, 0); }
		public TerminalNode CSS_BANG() { return getToken(TemplateParser.CSS_BANG, 0); }
		public TerminalNode CSS_DELIM() { return getToken(TemplateParser.CSS_DELIM, 0); }
		public CssComponentValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssComponentValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssComponentValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssComponentValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssComponentValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssComponentValueContext cssComponentValue() throws RecognitionException {
		CssComponentValueContext _localctx = new CssComponentValueContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_cssComponentValue);
		try {
			setState(488);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(462);
				cssFunctionCall();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(463);
				cssColor();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(464);
				cssMeasurement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(465);
				match(CSS_STRING);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(466);
				match(CSS_NUMBER);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(467);
				cssIdent();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(468);
				match(CSS_HASH);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(469);
				match(CSS_DOT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(470);
				match(CSS_COMMA);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(471);
				match(CSS_COLON);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(472);
				match(CSS_SEMICOLON);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(473);
				match(CSS_SLASH);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(474);
				match(CSS_PLUS);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(475);
				match(CSS_GREATER);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(476);
				match(CSS_TILDE);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(477);
				match(CSS_PIPE);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(478);
				match(CSS_STAR);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(479);
				match(CSS_EQUALS);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(480);
				match(CSS_LPAREN);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(481);
				match(CSS_RPAREN);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(482);
				match(CSS_LBRACKET);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(483);
				match(CSS_RBRACKET);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(484);
				match(CSS_LBRACE);
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(485);
				match(CSS_RBRACE);
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(486);
				match(CSS_BANG);
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(487);
				match(CSS_DELIM);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssFunctionCallContext extends ParserRuleContext {
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public TerminalNode CSS_LPAREN() { return getToken(TemplateParser.CSS_LPAREN, 0); }
		public TerminalNode CSS_RPAREN() { return getToken(TemplateParser.CSS_RPAREN, 0); }
		public CssFunctionArgumentsContext cssFunctionArguments() {
			return getRuleContext(CssFunctionArgumentsContext.class,0);
		}
		public CssFunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssFunctionCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssFunctionCallContext cssFunctionCall() throws RecognitionException {
		CssFunctionCallContext _localctx = new CssFunctionCallContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_cssFunctionCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(490);
			cssIdent();
			setState(491);
			match(CSS_LPAREN);
			setState(493);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				{
				setState(492);
				cssFunctionArguments();
				}
				break;
			}
			setState(495);
			match(CSS_RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssFunctionArgumentsContext extends ParserRuleContext {
		public List<CssFunctionArgumentContext> cssFunctionArgument() {
			return getRuleContexts(CssFunctionArgumentContext.class);
		}
		public CssFunctionArgumentContext cssFunctionArgument(int i) {
			return getRuleContext(CssFunctionArgumentContext.class,i);
		}
		public List<CssCommaContext> cssComma() {
			return getRuleContexts(CssCommaContext.class);
		}
		public CssCommaContext cssComma(int i) {
			return getRuleContext(CssCommaContext.class,i);
		}
		public List<CssWhitespaceContext> cssWhitespace() {
			return getRuleContexts(CssWhitespaceContext.class);
		}
		public CssWhitespaceContext cssWhitespace(int i) {
			return getRuleContext(CssWhitespaceContext.class,i);
		}
		public CssFunctionArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssFunctionArguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssFunctionArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssFunctionArguments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssFunctionArguments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssFunctionArgumentsContext cssFunctionArguments() throws RecognitionException {
		CssFunctionArgumentsContext _localctx = new CssFunctionArgumentsContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_cssFunctionArguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(497);
			cssFunctionArgument();
			setState(509);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CSS_COMMA) {
				{
				{
				setState(498);
				cssComma();
				setState(502);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CSS_WS) {
					{
					{
					setState(499);
					cssWhitespace();
					}
					}
					setState(504);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(505);
				cssFunctionArgument();
				}
				}
				setState(511);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssFunctionArgumentContext extends ParserRuleContext {
		public List<CssComponentValueContext> cssComponentValue() {
			return getRuleContexts(CssComponentValueContext.class);
		}
		public CssComponentValueContext cssComponentValue(int i) {
			return getRuleContext(CssComponentValueContext.class,i);
		}
		public CssFunctionArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssFunctionArgument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssFunctionArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssFunctionArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssFunctionArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssFunctionArgumentContext cssFunctionArgument() throws RecognitionException {
		CssFunctionArgumentContext _localctx = new CssFunctionArgumentContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_cssFunctionArgument);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(513); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(512);
					cssComponentValue();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(515); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssColorContext extends ParserRuleContext {
		public TerminalNode CSS_HEX_COLOR() { return getToken(TemplateParser.CSS_HEX_COLOR, 0); }
		public CssIdentContext cssIdent() {
			return getRuleContext(CssIdentContext.class,0);
		}
		public CssColorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssColor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssColor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssColorContext cssColor() throws RecognitionException {
		CssColorContext _localctx = new CssColorContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_cssColor);
		try {
			setState(519);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CSS_HEX_COLOR:
				enterOuterAlt(_localctx, 1);
				{
				setState(517);
				match(CSS_HEX_COLOR);
				}
				break;
			case CSS_CUSTOM_PROPERTY:
			case CSS_IDENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(518);
				cssIdent();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssMeasurementContext extends ParserRuleContext {
		public TerminalNode CSS_DIMENSION() { return getToken(TemplateParser.CSS_DIMENSION, 0); }
		public TerminalNode CSS_PERCENTAGE() { return getToken(TemplateParser.CSS_PERCENTAGE, 0); }
		public CssMeasurementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssMeasurement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssMeasurement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssMeasurement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssMeasurement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssMeasurementContext cssMeasurement() throws RecognitionException {
		CssMeasurementContext _localctx = new CssMeasurementContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_cssMeasurement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(521);
			_la = _input.LA(1);
			if ( !(_la==CSS_PERCENTAGE || _la==CSS_DIMENSION) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssIdentContext extends ParserRuleContext {
		public TerminalNode CSS_IDENT() { return getToken(TemplateParser.CSS_IDENT, 0); }
		public TerminalNode CSS_CUSTOM_PROPERTY() { return getToken(TemplateParser.CSS_CUSTOM_PROPERTY, 0); }
		public CssIdentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssIdent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssIdent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssIdent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssIdentContext cssIdent() throws RecognitionException {
		CssIdentContext _localctx = new CssIdentContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_cssIdent);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(523);
			_la = _input.LA(1);
			if ( !(_la==CSS_CUSTOM_PROPERTY || _la==CSS_IDENT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssWhitespaceContext extends ParserRuleContext {
		public List<TerminalNode> CSS_WS() { return getTokens(TemplateParser.CSS_WS); }
		public TerminalNode CSS_WS(int i) {
			return getToken(TemplateParser.CSS_WS, i);
		}
		public CssWhitespaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssWhitespace; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssWhitespace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssWhitespace(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssWhitespace(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssWhitespaceContext cssWhitespace() throws RecognitionException {
		CssWhitespaceContext _localctx = new CssWhitespaceContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_cssWhitespace);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(526); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(525);
					match(CSS_WS);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(528); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CssCommentContext extends ParserRuleContext {
		public TerminalNode CSS_COMMENT() { return getToken(TemplateParser.CSS_COMMENT, 0); }
		public CssCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cssComment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterCssComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitCssComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitCssComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CssCommentContext cssComment() throws RecognitionException {
		CssCommentContext _localctx = new CssCommentContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_cssComment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(530);
			match(CSS_COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaBlockContext extends ParserRuleContext {
		public JinjaBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaBlock; }
	 
		public JinjaBlockContext() { }
		public void copyFrom(JinjaBlockContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class JinjaBlockRuleContext extends JinjaBlockContext {
		public TerminalNode JINJA_BLOCK_OPEN() { return getToken(TemplateParser.JINJA_BLOCK_OPEN, 0); }
		public JinjaTagContext jinjaTag() {
			return getRuleContext(JinjaTagContext.class,0);
		}
		public TerminalNode JINJA_BLOCK_CLOSE() { return getToken(TemplateParser.JINJA_BLOCK_CLOSE, 0); }
		public JinjaBlockRuleContext(JinjaBlockContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaBlockRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaBlockRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaBlockRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaBlockContext jinjaBlock() throws RecognitionException {
		JinjaBlockContext _localctx = new JinjaBlockContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_jinjaBlock);
		try {
			_localctx = new JinjaBlockRuleContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
			match(JINJA_BLOCK_OPEN);
			setState(533);
			jinjaTag();
			setState(534);
			match(JINJA_BLOCK_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaTagContext extends ParserRuleContext {
		public JinjaTagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaTag; }
	 
		public JinjaTagContext() { }
		public void copyFrom(JinjaTagContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BlockTagContext extends JinjaTagContext {
		public TerminalNode JJ_BLOCK() { return getToken(TemplateParser.JJ_BLOCK, 0); }
		public TerminalNode JJ_IDENTIFIER() { return getToken(TemplateParser.JJ_IDENTIFIER, 0); }
		public BlockTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterBlockTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitBlockTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitBlockTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EndBlockTagContext extends JinjaTagContext {
		public TerminalNode JJ_ENDBLOCK() { return getToken(TemplateParser.JJ_ENDBLOCK, 0); }
		public TerminalNode JJ_IDENTIFIER() { return getToken(TemplateParser.JJ_IDENTIFIER, 0); }
		public EndBlockTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterEndBlockTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitEndBlockTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitEndBlockTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EndIfTagContext extends JinjaTagContext {
		public TerminalNode JJ_ENDIF() { return getToken(TemplateParser.JJ_ENDIF, 0); }
		public EndIfTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterEndIfTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitEndIfTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitEndIfTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ForTagContext extends JinjaTagContext {
		public TerminalNode JJ_FOR() { return getToken(TemplateParser.JJ_FOR, 0); }
		public JinjaForTargetsContext jinjaForTargets() {
			return getRuleContext(JinjaForTargetsContext.class,0);
		}
		public TerminalNode JJ_IN() { return getToken(TemplateParser.JJ_IN, 0); }
		public JinjaOrExprContext jinjaOrExpr() {
			return getRuleContext(JinjaOrExprContext.class,0);
		}
		public ForTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterForTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitForTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitForTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ElifTagContext extends JinjaTagContext {
		public TerminalNode JJ_ELIF() { return getToken(TemplateParser.JJ_ELIF, 0); }
		public JinjaOrExprContext jinjaOrExpr() {
			return getRuleContext(JinjaOrExprContext.class,0);
		}
		public ElifTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterElifTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitElifTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitElifTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EndMacroTagContext extends JinjaTagContext {
		public TerminalNode JJ_ENDMACRO() { return getToken(TemplateParser.JJ_ENDMACRO, 0); }
		public EndMacroTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterEndMacroTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitEndMacroTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitEndMacroTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IncludeTagContext extends JinjaTagContext {
		public TerminalNode JJ_INCLUDE() { return getToken(TemplateParser.JJ_INCLUDE, 0); }
		public TerminalNode JJ_STRING() { return getToken(TemplateParser.JJ_STRING, 0); }
		public IncludeTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterIncludeTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitIncludeTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitIncludeTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExtendsTagContext extends JinjaTagContext {
		public TerminalNode JJ_EXTENDS() { return getToken(TemplateParser.JJ_EXTENDS, 0); }
		public TerminalNode JJ_STRING() { return getToken(TemplateParser.JJ_STRING, 0); }
		public ExtendsTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterExtendsTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitExtendsTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitExtendsTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IfTagContext extends JinjaTagContext {
		public TerminalNode JJ_IF() { return getToken(TemplateParser.JJ_IF, 0); }
		public JinjaOrExprContext jinjaOrExpr() {
			return getRuleContext(JinjaOrExprContext.class,0);
		}
		public IfTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterIfTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitIfTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitIfTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EndForTagContext extends JinjaTagContext {
		public TerminalNode JJ_ENDFOR() { return getToken(TemplateParser.JJ_ENDFOR, 0); }
		public EndForTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterEndForTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitEndForTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitEndForTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ElseTagContext extends JinjaTagContext {
		public TerminalNode JJ_ELSE() { return getToken(TemplateParser.JJ_ELSE, 0); }
		public ElseTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterElseTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitElseTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitElseTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MacroTagContext extends JinjaTagContext {
		public TerminalNode JJ_MACRO() { return getToken(TemplateParser.JJ_MACRO, 0); }
		public TerminalNode JJ_IDENTIFIER() { return getToken(TemplateParser.JJ_IDENTIFIER, 0); }
		public TerminalNode JJ_LPAREN() { return getToken(TemplateParser.JJ_LPAREN, 0); }
		public TerminalNode JJ_RPAREN() { return getToken(TemplateParser.JJ_RPAREN, 0); }
		public JinjaParamListContext jinjaParamList() {
			return getRuleContext(JinjaParamListContext.class,0);
		}
		public MacroTagContext(JinjaTagContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterMacroTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitMacroTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitMacroTag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaTagContext jinjaTag() throws RecognitionException {
		JinjaTagContext _localctx = new JinjaTagContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_jinjaTag);
		int _la;
		try {
			setState(566);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JJ_IF:
				_localctx = new IfTagContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(536);
				match(JJ_IF);
				setState(537);
				jinjaOrExpr();
				}
				break;
			case JJ_ELIF:
				_localctx = new ElifTagContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(538);
				match(JJ_ELIF);
				setState(539);
				jinjaOrExpr();
				}
				break;
			case JJ_ELSE:
				_localctx = new ElseTagContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(540);
				match(JJ_ELSE);
				}
				break;
			case JJ_ENDIF:
				_localctx = new EndIfTagContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(541);
				match(JJ_ENDIF);
				}
				break;
			case JJ_FOR:
				_localctx = new ForTagContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(542);
				match(JJ_FOR);
				setState(543);
				jinjaForTargets();
				setState(544);
				match(JJ_IN);
				setState(545);
				jinjaOrExpr();
				}
				break;
			case JJ_ENDFOR:
				_localctx = new EndForTagContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(547);
				match(JJ_ENDFOR);
				}
				break;
			case JJ_BLOCK:
				_localctx = new BlockTagContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(548);
				match(JJ_BLOCK);
				setState(549);
				match(JJ_IDENTIFIER);
				}
				break;
			case JJ_ENDBLOCK:
				_localctx = new EndBlockTagContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(550);
				match(JJ_ENDBLOCK);
				setState(552);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==JJ_IDENTIFIER) {
					{
					setState(551);
					match(JJ_IDENTIFIER);
					}
				}

				}
				break;
			case JJ_MACRO:
				_localctx = new MacroTagContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(554);
				match(JJ_MACRO);
				setState(555);
				match(JJ_IDENTIFIER);
				setState(556);
				match(JJ_LPAREN);
				setState(558);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==JJ_IDENTIFIER) {
					{
					setState(557);
					jinjaParamList();
					}
				}

				setState(560);
				match(JJ_RPAREN);
				}
				break;
			case JJ_ENDMACRO:
				_localctx = new EndMacroTagContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(561);
				match(JJ_ENDMACRO);
				}
				break;
			case JJ_EXTENDS:
				_localctx = new ExtendsTagContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(562);
				match(JJ_EXTENDS);
				setState(563);
				match(JJ_STRING);
				}
				break;
			case JJ_INCLUDE:
				_localctx = new IncludeTagContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(564);
				match(JJ_INCLUDE);
				setState(565);
				match(JJ_STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaForTargetsContext extends ParserRuleContext {
		public List<TerminalNode> JJ_IDENTIFIER() { return getTokens(TemplateParser.JJ_IDENTIFIER); }
		public TerminalNode JJ_IDENTIFIER(int i) {
			return getToken(TemplateParser.JJ_IDENTIFIER, i);
		}
		public List<TerminalNode> JJ_COMMA() { return getTokens(TemplateParser.JJ_COMMA); }
		public TerminalNode JJ_COMMA(int i) {
			return getToken(TemplateParser.JJ_COMMA, i);
		}
		public JinjaForTargetsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaForTargets; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaForTargets(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaForTargets(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaForTargets(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaForTargetsContext jinjaForTargets() throws RecognitionException {
		JinjaForTargetsContext _localctx = new JinjaForTargetsContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_jinjaForTargets);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(568);
			match(JJ_IDENTIFIER);
			setState(573);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JJ_COMMA) {
				{
				{
				setState(569);
				match(JJ_COMMA);
				setState(570);
				match(JJ_IDENTIFIER);
				}
				}
				setState(575);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaParamListContext extends ParserRuleContext {
		public List<TerminalNode> JJ_IDENTIFIER() { return getTokens(TemplateParser.JJ_IDENTIFIER); }
		public TerminalNode JJ_IDENTIFIER(int i) {
			return getToken(TemplateParser.JJ_IDENTIFIER, i);
		}
		public List<TerminalNode> JJ_COMMA() { return getTokens(TemplateParser.JJ_COMMA); }
		public TerminalNode JJ_COMMA(int i) {
			return getToken(TemplateParser.JJ_COMMA, i);
		}
		public JinjaParamListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaParamList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaParamList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaParamList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaParamList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaParamListContext jinjaParamList() throws RecognitionException {
		JinjaParamListContext _localctx = new JinjaParamListContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_jinjaParamList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(576);
			match(JJ_IDENTIFIER);
			setState(581);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JJ_COMMA) {
				{
				{
				setState(577);
				match(JJ_COMMA);
				setState(578);
				match(JJ_IDENTIFIER);
				}
				}
				setState(583);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaExprContext extends ParserRuleContext {
		public JinjaExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaExpr; }
	 
		public JinjaExprContext() { }
		public void copyFrom(JinjaExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class JinjaExpressionContext extends JinjaExprContext {
		public TerminalNode JINJA_EXPR_OPEN() { return getToken(TemplateParser.JINJA_EXPR_OPEN, 0); }
		public JinjaOrExprContext jinjaOrExpr() {
			return getRuleContext(JinjaOrExprContext.class,0);
		}
		public TerminalNode JINJA_EXPR_CLOSE() { return getToken(TemplateParser.JINJA_EXPR_CLOSE, 0); }
		public JinjaExpressionContext(JinjaExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaExprContext jinjaExpr() throws RecognitionException {
		JinjaExprContext _localctx = new JinjaExprContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_jinjaExpr);
		try {
			_localctx = new JinjaExpressionContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(584);
			match(JINJA_EXPR_OPEN);
			setState(585);
			jinjaOrExpr();
			setState(586);
			match(JINJA_EXPR_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaFilterCallContext extends ParserRuleContext {
		public TerminalNode JJ_IDENTIFIER() { return getToken(TemplateParser.JJ_IDENTIFIER, 0); }
		public TerminalNode JJ_LPAREN() { return getToken(TemplateParser.JJ_LPAREN, 0); }
		public TerminalNode JJ_RPAREN() { return getToken(TemplateParser.JJ_RPAREN, 0); }
		public JinjaArgListContext jinjaArgList() {
			return getRuleContext(JinjaArgListContext.class,0);
		}
		public JinjaFilterCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaFilterCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaFilterCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaFilterCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaFilterCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaFilterCallContext jinjaFilterCall() throws RecognitionException {
		JinjaFilterCallContext _localctx = new JinjaFilterCallContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_jinjaFilterCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(588);
			match(JJ_IDENTIFIER);
			setState(594);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==JJ_LPAREN) {
				{
				setState(589);
				match(JJ_LPAREN);
				setState(591);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8144761884604628992L) != 0)) {
					{
					setState(590);
					jinjaArgList();
					}
				}

				setState(593);
				match(JJ_RPAREN);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaArgListContext extends ParserRuleContext {
		public List<JinjaOrExprContext> jinjaOrExpr() {
			return getRuleContexts(JinjaOrExprContext.class);
		}
		public JinjaOrExprContext jinjaOrExpr(int i) {
			return getRuleContext(JinjaOrExprContext.class,i);
		}
		public List<TerminalNode> JJ_COMMA() { return getTokens(TemplateParser.JJ_COMMA); }
		public TerminalNode JJ_COMMA(int i) {
			return getToken(TemplateParser.JJ_COMMA, i);
		}
		public JinjaArgListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaArgList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaArgList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaArgList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaArgList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaArgListContext jinjaArgList() throws RecognitionException {
		JinjaArgListContext _localctx = new JinjaArgListContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_jinjaArgList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(596);
			jinjaOrExpr();
			setState(601);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JJ_COMMA) {
				{
				{
				setState(597);
				match(JJ_COMMA);
				setState(598);
				jinjaOrExpr();
				}
				}
				setState(603);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaOrExprContext extends ParserRuleContext {
		public List<JinjaAndExprContext> jinjaAndExpr() {
			return getRuleContexts(JinjaAndExprContext.class);
		}
		public JinjaAndExprContext jinjaAndExpr(int i) {
			return getRuleContext(JinjaAndExprContext.class,i);
		}
		public List<TerminalNode> JJ_OR() { return getTokens(TemplateParser.JJ_OR); }
		public TerminalNode JJ_OR(int i) {
			return getToken(TemplateParser.JJ_OR, i);
		}
		public JinjaOrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaOrExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaOrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaOrExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaOrExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaOrExprContext jinjaOrExpr() throws RecognitionException {
		JinjaOrExprContext _localctx = new JinjaOrExprContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_jinjaOrExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(604);
			jinjaAndExpr();
			setState(609);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JJ_OR) {
				{
				{
				setState(605);
				match(JJ_OR);
				setState(606);
				jinjaAndExpr();
				}
				}
				setState(611);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaAndExprContext extends ParserRuleContext {
		public List<JinjaNotExprContext> jinjaNotExpr() {
			return getRuleContexts(JinjaNotExprContext.class);
		}
		public JinjaNotExprContext jinjaNotExpr(int i) {
			return getRuleContext(JinjaNotExprContext.class,i);
		}
		public List<TerminalNode> JJ_AND() { return getTokens(TemplateParser.JJ_AND); }
		public TerminalNode JJ_AND(int i) {
			return getToken(TemplateParser.JJ_AND, i);
		}
		public JinjaAndExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaAndExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaAndExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaAndExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaAndExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaAndExprContext jinjaAndExpr() throws RecognitionException {
		JinjaAndExprContext _localctx = new JinjaAndExprContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_jinjaAndExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(612);
			jinjaNotExpr();
			setState(617);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JJ_AND) {
				{
				{
				setState(613);
				match(JJ_AND);
				setState(614);
				jinjaNotExpr();
				}
				}
				setState(619);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaNotExprContext extends ParserRuleContext {
		public TerminalNode JJ_NOT() { return getToken(TemplateParser.JJ_NOT, 0); }
		public JinjaNotExprContext jinjaNotExpr() {
			return getRuleContext(JinjaNotExprContext.class,0);
		}
		public JinjaComparisonExprContext jinjaComparisonExpr() {
			return getRuleContext(JinjaComparisonExprContext.class,0);
		}
		public JinjaNotExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaNotExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaNotExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaNotExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaNotExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaNotExprContext jinjaNotExpr() throws RecognitionException {
		JinjaNotExprContext _localctx = new JinjaNotExprContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_jinjaNotExpr);
		try {
			setState(623);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JJ_NOT:
				enterOuterAlt(_localctx, 1);
				{
				setState(620);
				match(JJ_NOT);
				setState(621);
				jinjaNotExpr();
				}
				break;
			case JJ_TRUE:
			case JJ_FALSE:
			case JJ_NONE:
			case JJ_LPAREN:
			case JJ_MINUS:
			case JJ_NUMBER:
			case JJ_STRING:
			case JJ_IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(622);
				jinjaComparisonExpr();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaComparisonExprContext extends ParserRuleContext {
		public List<JinjaAdditiveExprContext> jinjaAdditiveExpr() {
			return getRuleContexts(JinjaAdditiveExprContext.class);
		}
		public JinjaAdditiveExprContext jinjaAdditiveExpr(int i) {
			return getRuleContext(JinjaAdditiveExprContext.class,i);
		}
		public TerminalNode JJ_EQ() { return getToken(TemplateParser.JJ_EQ, 0); }
		public TerminalNode JJ_NEQ() { return getToken(TemplateParser.JJ_NEQ, 0); }
		public TerminalNode JJ_LE() { return getToken(TemplateParser.JJ_LE, 0); }
		public TerminalNode JJ_GE() { return getToken(TemplateParser.JJ_GE, 0); }
		public TerminalNode JJ_LT() { return getToken(TemplateParser.JJ_LT, 0); }
		public TerminalNode JJ_GT() { return getToken(TemplateParser.JJ_GT, 0); }
		public JinjaComparisonExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaComparisonExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaComparisonExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaComparisonExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaComparisonExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaComparisonExprContext jinjaComparisonExpr() throws RecognitionException {
		JinjaComparisonExprContext _localctx = new JinjaComparisonExprContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_jinjaComparisonExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(625);
			jinjaAdditiveExpr();
			setState(628);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 138538465099776L) != 0)) {
				{
				setState(626);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 138538465099776L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(627);
				jinjaAdditiveExpr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaAdditiveExprContext extends ParserRuleContext {
		public List<JinjaMultiplicativeExprContext> jinjaMultiplicativeExpr() {
			return getRuleContexts(JinjaMultiplicativeExprContext.class);
		}
		public JinjaMultiplicativeExprContext jinjaMultiplicativeExpr(int i) {
			return getRuleContext(JinjaMultiplicativeExprContext.class,i);
		}
		public List<TerminalNode> JJ_PLUS() { return getTokens(TemplateParser.JJ_PLUS); }
		public TerminalNode JJ_PLUS(int i) {
			return getToken(TemplateParser.JJ_PLUS, i);
		}
		public List<TerminalNode> JJ_MINUS() { return getTokens(TemplateParser.JJ_MINUS); }
		public TerminalNode JJ_MINUS(int i) {
			return getToken(TemplateParser.JJ_MINUS, i);
		}
		public List<TerminalNode> JJ_TILDE() { return getTokens(TemplateParser.JJ_TILDE); }
		public TerminalNode JJ_TILDE(int i) {
			return getToken(TemplateParser.JJ_TILDE, i);
		}
		public JinjaAdditiveExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaAdditiveExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaAdditiveExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaAdditiveExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaAdditiveExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaAdditiveExprContext jinjaAdditiveExpr() throws RecognitionException {
		JinjaAdditiveExprContext _localctx = new JinjaAdditiveExprContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_jinjaAdditiveExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(630);
			jinjaMultiplicativeExpr();
			setState(635);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 684547143360315392L) != 0)) {
				{
				{
				setState(631);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 684547143360315392L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(632);
				jinjaMultiplicativeExpr();
				}
				}
				setState(637);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaMultiplicativeExprContext extends ParserRuleContext {
		public List<JinjaFilteredPrimaryContext> jinjaFilteredPrimary() {
			return getRuleContexts(JinjaFilteredPrimaryContext.class);
		}
		public JinjaFilteredPrimaryContext jinjaFilteredPrimary(int i) {
			return getRuleContext(JinjaFilteredPrimaryContext.class,i);
		}
		public List<TerminalNode> JJ_STAR() { return getTokens(TemplateParser.JJ_STAR); }
		public TerminalNode JJ_STAR(int i) {
			return getToken(TemplateParser.JJ_STAR, i);
		}
		public List<TerminalNode> JJ_SLASH() { return getTokens(TemplateParser.JJ_SLASH); }
		public TerminalNode JJ_SLASH(int i) {
			return getToken(TemplateParser.JJ_SLASH, i);
		}
		public JinjaMultiplicativeExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaMultiplicativeExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaMultiplicativeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaMultiplicativeExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaMultiplicativeExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaMultiplicativeExprContext jinjaMultiplicativeExpr() throws RecognitionException {
		JinjaMultiplicativeExprContext _localctx = new JinjaMultiplicativeExprContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_jinjaMultiplicativeExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			jinjaFilteredPrimary();
			setState(643);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JJ_STAR || _la==JJ_SLASH) {
				{
				{
				setState(639);
				_la = _input.LA(1);
				if ( !(_la==JJ_STAR || _la==JJ_SLASH) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(640);
				jinjaFilteredPrimary();
				}
				}
				setState(645);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaFilteredPrimaryContext extends ParserRuleContext {
		public JinjaPrimaryContext jinjaPrimary() {
			return getRuleContext(JinjaPrimaryContext.class,0);
		}
		public List<TerminalNode> JJ_PIPE() { return getTokens(TemplateParser.JJ_PIPE); }
		public TerminalNode JJ_PIPE(int i) {
			return getToken(TemplateParser.JJ_PIPE, i);
		}
		public List<JinjaFilterCallContext> jinjaFilterCall() {
			return getRuleContexts(JinjaFilterCallContext.class);
		}
		public JinjaFilterCallContext jinjaFilterCall(int i) {
			return getRuleContext(JinjaFilterCallContext.class,i);
		}
		public JinjaFilteredPrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaFilteredPrimary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaFilteredPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaFilteredPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaFilteredPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaFilteredPrimaryContext jinjaFilteredPrimary() throws RecognitionException {
		JinjaFilteredPrimaryContext _localctx = new JinjaFilteredPrimaryContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_jinjaFilteredPrimary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(646);
			jinjaPrimary();
			setState(651);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JJ_PIPE) {
				{
				{
				setState(647);
				match(JJ_PIPE);
				setState(648);
				jinjaFilterCall();
				}
				}
				setState(653);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaPrimaryContext extends ParserRuleContext {
		public TerminalNode JJ_MINUS() { return getToken(TemplateParser.JJ_MINUS, 0); }
		public JinjaPrimaryContext jinjaPrimary() {
			return getRuleContext(JinjaPrimaryContext.class,0);
		}
		public JinjaAtomTrailerContext jinjaAtomTrailer() {
			return getRuleContext(JinjaAtomTrailerContext.class,0);
		}
		public TerminalNode JJ_LPAREN() { return getToken(TemplateParser.JJ_LPAREN, 0); }
		public JinjaOrExprContext jinjaOrExpr() {
			return getRuleContext(JinjaOrExprContext.class,0);
		}
		public TerminalNode JJ_RPAREN() { return getToken(TemplateParser.JJ_RPAREN, 0); }
		public JinjaPrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaPrimary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaPrimaryContext jinjaPrimary() throws RecognitionException {
		JinjaPrimaryContext _localctx = new JinjaPrimaryContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_jinjaPrimary);
		try {
			setState(661);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JJ_MINUS:
				enterOuterAlt(_localctx, 1);
				{
				setState(654);
				match(JJ_MINUS);
				setState(655);
				jinjaPrimary();
				}
				break;
			case JJ_TRUE:
			case JJ_FALSE:
			case JJ_NONE:
			case JJ_NUMBER:
			case JJ_STRING:
			case JJ_IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(656);
				jinjaAtomTrailer();
				}
				break;
			case JJ_LPAREN:
				enterOuterAlt(_localctx, 3);
				{
				setState(657);
				match(JJ_LPAREN);
				setState(658);
				jinjaOrExpr();
				setState(659);
				match(JJ_RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaAtomTrailerContext extends ParserRuleContext {
		public JinjaAtomContext jinjaAtom() {
			return getRuleContext(JinjaAtomContext.class,0);
		}
		public List<JinjaTrailerContext> jinjaTrailer() {
			return getRuleContexts(JinjaTrailerContext.class);
		}
		public JinjaTrailerContext jinjaTrailer(int i) {
			return getRuleContext(JinjaTrailerContext.class,i);
		}
		public JinjaAtomTrailerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaAtomTrailer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaAtomTrailer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaAtomTrailer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaAtomTrailer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaAtomTrailerContext jinjaAtomTrailer() throws RecognitionException {
		JinjaAtomTrailerContext _localctx = new JinjaAtomTrailerContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_jinjaAtomTrailer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(663);
			jinjaAtom();
			setState(667);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 11821949021847552L) != 0)) {
				{
				{
				setState(664);
				jinjaTrailer();
				}
				}
				setState(669);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaTrailerContext extends ParserRuleContext {
		public TerminalNode JJ_DOT() { return getToken(TemplateParser.JJ_DOT, 0); }
		public TerminalNode JJ_IDENTIFIER() { return getToken(TemplateParser.JJ_IDENTIFIER, 0); }
		public TerminalNode JJ_LBRACKET() { return getToken(TemplateParser.JJ_LBRACKET, 0); }
		public JinjaOrExprContext jinjaOrExpr() {
			return getRuleContext(JinjaOrExprContext.class,0);
		}
		public TerminalNode JJ_RBRACKET() { return getToken(TemplateParser.JJ_RBRACKET, 0); }
		public TerminalNode JJ_LPAREN() { return getToken(TemplateParser.JJ_LPAREN, 0); }
		public TerminalNode JJ_RPAREN() { return getToken(TemplateParser.JJ_RPAREN, 0); }
		public JinjaArgListContext jinjaArgList() {
			return getRuleContext(JinjaArgListContext.class,0);
		}
		public JinjaTrailerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaTrailer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaTrailer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaTrailer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaTrailer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaTrailerContext jinjaTrailer() throws RecognitionException {
		JinjaTrailerContext _localctx = new JinjaTrailerContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_jinjaTrailer);
		int _la;
		try {
			setState(681);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JJ_DOT:
				enterOuterAlt(_localctx, 1);
				{
				setState(670);
				match(JJ_DOT);
				setState(671);
				match(JJ_IDENTIFIER);
				}
				break;
			case JJ_LBRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(672);
				match(JJ_LBRACKET);
				setState(673);
				jinjaOrExpr();
				setState(674);
				match(JJ_RBRACKET);
				}
				break;
			case JJ_LPAREN:
				enterOuterAlt(_localctx, 3);
				{
				setState(676);
				match(JJ_LPAREN);
				setState(678);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8144761884604628992L) != 0)) {
					{
					setState(677);
					jinjaArgList();
					}
				}

				setState(680);
				match(JJ_RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaAtomContext extends ParserRuleContext {
		public TerminalNode JJ_IDENTIFIER() { return getToken(TemplateParser.JJ_IDENTIFIER, 0); }
		public TerminalNode JJ_NUMBER() { return getToken(TemplateParser.JJ_NUMBER, 0); }
		public TerminalNode JJ_STRING() { return getToken(TemplateParser.JJ_STRING, 0); }
		public TerminalNode JJ_TRUE() { return getToken(TemplateParser.JJ_TRUE, 0); }
		public TerminalNode JJ_FALSE() { return getToken(TemplateParser.JJ_FALSE, 0); }
		public TerminalNode JJ_NONE() { return getToken(TemplateParser.JJ_NONE, 0); }
		public JinjaAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaAtomContext jinjaAtom() throws RecognitionException {
		JinjaAtomContext _localctx = new JinjaAtomContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_jinjaAtom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(683);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8070452456393277440L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JinjaCommentContext extends ParserRuleContext {
		public JinjaCommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jinjaComment; }
	 
		public JinjaCommentContext() { }
		public void copyFrom(JinjaCommentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class JinjaCommentRuleContext extends JinjaCommentContext {
		public TerminalNode JINJA_COMMENT_OPEN() { return getToken(TemplateParser.JINJA_COMMENT_OPEN, 0); }
		public TerminalNode JINJA_COMMENT_CLOSE() { return getToken(TemplateParser.JINJA_COMMENT_CLOSE, 0); }
		public List<TerminalNode> JINJA_COMMENT_CONTENT() { return getTokens(TemplateParser.JINJA_COMMENT_CONTENT); }
		public TerminalNode JINJA_COMMENT_CONTENT(int i) {
			return getToken(TemplateParser.JINJA_COMMENT_CONTENT, i);
		}
		public JinjaCommentRuleContext(JinjaCommentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).enterJinjaCommentRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TemplateParserListener ) ((TemplateParserListener)listener).exitJinjaCommentRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TemplateParserVisitor ) return ((TemplateParserVisitor<? extends T>)visitor).visitJinjaCommentRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JinjaCommentContext jinjaComment() throws RecognitionException {
		JinjaCommentContext _localctx = new JinjaCommentContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_jinjaComment);
		int _la;
		try {
			_localctx = new JinjaCommentRuleContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(685);
			match(JINJA_COMMENT_OPEN);
			setState(689);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==JINJA_COMMENT_CONTENT) {
				{
				{
				setState(686);
				match(JINJA_COMMENT_CONTENT);
				}
				}
				setState(691);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(692);
			match(JINJA_COMMENT_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001e\u02b7\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0001\u0000\u0005\u0000\u0080\b\u0000"+
		"\n\u0000\f\u0000\u0083\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0005"+
		"\u0001\u0088\b\u0001\n\u0001\f\u0001\u008b\t\u0001\u0001\u0001\u0001\u0001"+
		"\u0005\u0001\u008f\b\u0001\n\u0001\f\u0001\u0092\t\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002\u0097\b\u0002\n\u0002\f\u0002\u009a\t\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0003\u0002\u00a3\b\u0002\u0001\u0002\u0003\u0002\u00a6\b"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002\u00ac"+
		"\b\u0002\u0001\u0003\u0003\u0003\u00af\b\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00b7\b\u0003"+
		"\u0001\u0003\u0003\u0003\u00ba\b\u0003\u0005\u0003\u00bc\b\u0003\n\u0003"+
		"\f\u0003\u00bf\t\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004"+
		"\u00c4\b\u0004\u0001\u0005\u0001\u0005\u0003\u0005\u00c8\b\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u00ce\b\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0003\b\u00d4\b\b\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\n\u0005\n\u00db\b\n\n\n\f\n\u00de\t\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0003\u000b\u00e3\b\u000b\u0001\f\u0001\f\u0003\f\u00e7"+
		"\b\f\u0001\r\u0001\r\u0003\r\u00eb\b\r\u0001\r\u0005\r\u00ee\b\r\n\r\f"+
		"\r\u00f1\t\r\u0001\r\u0003\r\u00f4\b\r\u0001\r\u0001\r\u0003\r\u00f8\b"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0003\u000f\u00fe\b"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0005"+
		"\u0010\u0105\b\u0010\n\u0010\f\u0010\u0108\t\u0010\u0001\u0010\u0001\u0010"+
		"\u0005\u0010\u010c\b\u0010\n\u0010\f\u0010\u010f\t\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0012\u0001\u0012\u0004\u0012\u0115\b\u0012\u000b\u0012\f"+
		"\u0012\u0116\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u011c\b\u0012"+
		"\n\u0012\f\u0012\u011f\t\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u0123"+
		"\b\u0012\n\u0012\f\u0012\u0126\t\u0012\u0001\u0012\u0001\u0012\u0005\u0012"+
		"\u012a\b\u0012\n\u0012\f\u0012\u012d\t\u0012\u0001\u0013\u0001\u0013\u0001"+
		"\u0014\u0003\u0014\u0132\b\u0014\u0001\u0014\u0005\u0014\u0135\b\u0014"+
		"\n\u0014\f\u0014\u0138\t\u0014\u0001\u0015\u0001\u0015\u0003\u0015\u013c"+
		"\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u0142"+
		"\b\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0019\u0001\u0019\u0005\u0019\u014c\b\u0019\n\u0019\f\u0019"+
		"\u014f\t\u0019\u0001\u0019\u0001\u0019\u0005\u0019\u0153\b\u0019\n\u0019"+
		"\f\u0019\u0156\t\u0019\u0001\u0019\u0001\u0019\u0005\u0019\u015a\b\u0019"+
		"\n\u0019\f\u0019\u015d\t\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0161"+
		"\b\u0019\u0001\u0019\u0005\u0019\u0164\b\u0019\n\u0019\f\u0019\u0167\t"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0003\u001a\u0176\b\u001a\u0001\u001b\u0001\u001b\u0003"+
		"\u001b\u017a\b\u001b\u0001\u001c\u0001\u001c\u0003\u001c\u017e\b\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u0183\b\u001c\n\u001c"+
		"\f\u001c\u0186\t\u001c\u0001\u001c\u0003\u001c\u0189\b\u001c\u0001\u001d"+
		"\u0001\u001d\u0005\u001d\u018d\b\u001d\n\u001d\f\u001d\u0190\t\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003"+
		"\u001e\u0198\b\u001e\u0001\u001f\u0001\u001f\u0003\u001f\u019c\b\u001f"+
		"\u0001\u001f\u0001\u001f\u0003\u001f\u01a0\b\u001f\u0001\u001f\u0001\u001f"+
		"\u0003\u001f\u01a4\b\u001f\u0001\u001f\u0003\u001f\u01a7\b\u001f\u0001"+
		"\u001f\u0003\u001f\u01aa\b\u001f\u0001\u001f\u0003\u001f\u01ad\b\u001f"+
		"\u0001 \u0001 \u0003 \u01b1\b \u0001 \u0003 \u01b4\b \u0001!\u0001!\u0001"+
		"!\u0001\"\u0001\"\u0004\"\u01bb\b\"\u000b\"\f\"\u01bc\u0001\"\u0001\""+
		"\u0001\"\u0001\"\u0005\"\u01c3\b\"\n\"\f\"\u01c6\t\"\u0001\"\u0001\"\u0005"+
		"\"\u01ca\b\"\n\"\f\"\u01cd\t\"\u0001#\u0001#\u0001#\u0001#\u0001#\u0001"+
		"#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001"+
		"#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001"+
		"#\u0003#\u01e9\b#\u0001$\u0001$\u0001$\u0003$\u01ee\b$\u0001$\u0001$\u0001"+
		"%\u0001%\u0001%\u0005%\u01f5\b%\n%\f%\u01f8\t%\u0001%\u0001%\u0005%\u01fc"+
		"\b%\n%\f%\u01ff\t%\u0001&\u0004&\u0202\b&\u000b&\f&\u0203\u0001\'\u0001"+
		"\'\u0003\'\u0208\b\'\u0001(\u0001(\u0001)\u0001)\u0001*\u0004*\u020f\b"+
		"*\u000b*\f*\u0210\u0001+\u0001+\u0001,\u0001,\u0001,\u0001,\u0001-\u0001"+
		"-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001"+
		"-\u0001-\u0001-\u0001-\u0001-\u0003-\u0229\b-\u0001-\u0001-\u0001-\u0001"+
		"-\u0003-\u022f\b-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0003-\u0237"+
		"\b-\u0001.\u0001.\u0001.\u0005.\u023c\b.\n.\f.\u023f\t.\u0001/\u0001/"+
		"\u0001/\u0005/\u0244\b/\n/\f/\u0247\t/\u00010\u00010\u00010\u00010\u0001"+
		"1\u00011\u00011\u00031\u0250\b1\u00011\u00031\u0253\b1\u00012\u00012\u0001"+
		"2\u00052\u0258\b2\n2\f2\u025b\t2\u00013\u00013\u00013\u00053\u0260\b3"+
		"\n3\f3\u0263\t3\u00014\u00014\u00014\u00054\u0268\b4\n4\f4\u026b\t4\u0001"+
		"5\u00015\u00015\u00035\u0270\b5\u00016\u00016\u00016\u00036\u0275\b6\u0001"+
		"7\u00017\u00017\u00057\u027a\b7\n7\f7\u027d\t7\u00018\u00018\u00018\u0005"+
		"8\u0282\b8\n8\f8\u0285\t8\u00019\u00019\u00019\u00059\u028a\b9\n9\f9\u028d"+
		"\t9\u0001:\u0001:\u0001:\u0001:\u0001:\u0001:\u0001:\u0003:\u0296\b:\u0001"+
		";\u0001;\u0005;\u029a\b;\n;\f;\u029d\t;\u0001<\u0001<\u0001<\u0001<\u0001"+
		"<\u0001<\u0001<\u0001<\u0003<\u02a7\b<\u0001<\u0003<\u02aa\b<\u0001=\u0001"+
		"=\u0001>\u0001>\u0005>\u02b0\b>\n>\f>\u02b3\t>\u0001>\u0001>\u0001>\u0000"+
		"\u0000?\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|\u0000"+
		"\u0007\u0001\u0000OQ\u0001\u0000_`\u0002\u0000]]cc\u0001\u0000).\u0002"+
		"\u000078;;\u0001\u00009:\u0002\u0000&(<>\u0302\u0000\u0081\u0001\u0000"+
		"\u0000\u0000\u0002\u0089\u0001\u0000\u0000\u0000\u0004\u00ab\u0001\u0000"+
		"\u0000\u0000\u0006\u00ae\u0001\u0000\u0000\u0000\b\u00c0\u0001\u0000\u0000"+
		"\u0000\n\u00c7\u0001\u0000\u0000\u0000\f\u00cd\u0001\u0000\u0000\u0000"+
		"\u000e\u00cf\u0001\u0000\u0000\u0000\u0010\u00d3\u0001\u0000\u0000\u0000"+
		"\u0012\u00d5\u0001\u0000\u0000\u0000\u0014\u00dc\u0001\u0000\u0000\u0000"+
		"\u0016\u00e2\u0001\u0000\u0000\u0000\u0018\u00e6\u0001\u0000\u0000\u0000"+
		"\u001a\u00e8\u0001\u0000\u0000\u0000\u001c\u00f9\u0001\u0000\u0000\u0000"+
		"\u001e\u00fb\u0001\u0000\u0000\u0000 \u0101\u0001\u0000\u0000\u0000\""+
		"\u0110\u0001\u0000\u0000\u0000$\u0112\u0001\u0000\u0000\u0000&\u012e\u0001"+
		"\u0000\u0000\u0000(\u0131\u0001\u0000\u0000\u0000*\u013b\u0001\u0000\u0000"+
		"\u0000,\u0141\u0001\u0000\u0000\u0000.\u0143\u0001\u0000\u0000\u00000"+
		"\u0146\u0001\u0000\u0000\u00002\u0149\u0001\u0000\u0000\u00004\u0175\u0001"+
		"\u0000\u0000\u00006\u0179\u0001\u0000\u0000\u00008\u017b\u0001\u0000\u0000"+
		"\u0000:\u018a\u0001\u0000\u0000\u0000<\u0197\u0001\u0000\u0000\u0000>"+
		"\u0199\u0001\u0000\u0000\u0000@\u01b3\u0001\u0000\u0000\u0000B\u01b5\u0001"+
		"\u0000\u0000\u0000D\u01b8\u0001\u0000\u0000\u0000F\u01e8\u0001\u0000\u0000"+
		"\u0000H\u01ea\u0001\u0000\u0000\u0000J\u01f1\u0001\u0000\u0000\u0000L"+
		"\u0201\u0001\u0000\u0000\u0000N\u0207\u0001\u0000\u0000\u0000P\u0209\u0001"+
		"\u0000\u0000\u0000R\u020b\u0001\u0000\u0000\u0000T\u020e\u0001\u0000\u0000"+
		"\u0000V\u0212\u0001\u0000\u0000\u0000X\u0214\u0001\u0000\u0000\u0000Z"+
		"\u0236\u0001\u0000\u0000\u0000\\\u0238\u0001\u0000\u0000\u0000^\u0240"+
		"\u0001\u0000\u0000\u0000`\u0248\u0001\u0000\u0000\u0000b\u024c\u0001\u0000"+
		"\u0000\u0000d\u0254\u0001\u0000\u0000\u0000f\u025c\u0001\u0000\u0000\u0000"+
		"h\u0264\u0001\u0000\u0000\u0000j\u026f\u0001\u0000\u0000\u0000l\u0271"+
		"\u0001\u0000\u0000\u0000n\u0276\u0001\u0000\u0000\u0000p\u027e\u0001\u0000"+
		"\u0000\u0000r\u0286\u0001\u0000\u0000\u0000t\u0295\u0001\u0000\u0000\u0000"+
		"v\u0297\u0001\u0000\u0000\u0000x\u02a9\u0001\u0000\u0000\u0000z\u02ab"+
		"\u0001\u0000\u0000\u0000|\u02ad\u0001\u0000\u0000\u0000~\u0080\u0003\u0002"+
		"\u0001\u0000\u007f~\u0001\u0000\u0000\u0000\u0080\u0083\u0001\u0000\u0000"+
		"\u0000\u0081\u007f\u0001\u0000\u0000\u0000\u0081\u0082\u0001\u0000\u0000"+
		"\u0000\u0082\u0084\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000"+
		"\u0000\u0084\u0085\u0005\u0000\u0000\u0001\u0085\u0001\u0001\u0000\u0000"+
		"\u0000\u0086\u0088\u0003\f\u0006\u0000\u0087\u0086\u0001\u0000\u0000\u0000"+
		"\u0088\u008b\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000\u0000"+
		"\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008c\u0001\u0000\u0000\u0000"+
		"\u008b\u0089\u0001\u0000\u0000\u0000\u008c\u0090\u0003\u0004\u0002\u0000"+
		"\u008d\u008f\u0003\f\u0006\u0000\u008e\u008d\u0001\u0000\u0000\u0000\u008f"+
		"\u0092\u0001\u0000\u0000\u0000\u0090\u008e\u0001\u0000\u0000\u0000\u0090"+
		"\u0091\u0001\u0000\u0000\u0000\u0091\u0003\u0001\u0000\u0000\u0000\u0092"+
		"\u0090\u0001\u0000\u0000\u0000\u0093\u0094\u0005\u0004\u0000\u0000\u0094"+
		"\u0098\u0005\u0010\u0000\u0000\u0095\u0097\u0003\b\u0004\u0000\u0096\u0095"+
		"\u0001\u0000\u0000\u0000\u0097\u009a\u0001\u0000\u0000\u0000\u0098\u0096"+
		"\u0001\u0000\u0000\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u00a5"+
		"\u0001\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009b\u00a2"+
		"\u0005\r\u0000\u0000\u009c\u009d\u0003\u0006\u0003\u0000\u009d\u009e\u0005"+
		"\u0004\u0000\u0000\u009e\u009f\u0005\u000e\u0000\u0000\u009f\u00a0\u0005"+
		"\u0010\u0000\u0000\u00a0\u00a1\u0005\r\u0000\u0000\u00a1\u00a3\u0001\u0000"+
		"\u0000\u0000\u00a2\u009c\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000"+
		"\u0000\u0000\u00a3\u00a6\u0001\u0000\u0000\u0000\u00a4\u00a6\u0005\f\u0000"+
		"\u0000\u00a5\u009b\u0001\u0000\u0000\u0000\u00a5\u00a4\u0001\u0000\u0000"+
		"\u0000\u00a6\u00ac\u0001\u0000\u0000\u0000\u00a7\u00ac\u0003\u0012\t\u0000"+
		"\u00a8\u00ac\u0003X,\u0000\u00a9\u00ac\u0003`0\u0000\u00aa\u00ac\u0003"+
		"|>\u0000\u00ab\u0093\u0001\u0000\u0000\u0000\u00ab\u00a7\u0001\u0000\u0000"+
		"\u0000\u00ab\u00a8\u0001\u0000\u0000\u0000\u00ab\u00a9\u0001\u0000\u0000"+
		"\u0000\u00ab\u00aa\u0001\u0000\u0000\u0000\u00ac\u0005\u0001\u0000\u0000"+
		"\u0000\u00ad\u00af\u0003\n\u0005\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000"+
		"\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00bd\u0001\u0000\u0000\u0000"+
		"\u00b0\u00b7\u0003\u0004\u0002\u0000\u00b1\u00b7\u0005\b\u0000\u0000\u00b2"+
		"\u00b7\u0003\u0010\b\u0000\u00b3\u00b7\u0003X,\u0000\u00b4\u00b7\u0003"+
		"`0\u0000\u00b5\u00b7\u0003|>\u0000\u00b6\u00b0\u0001\u0000\u0000\u0000"+
		"\u00b6\u00b1\u0001\u0000\u0000\u0000\u00b6\u00b2\u0001\u0000\u0000\u0000"+
		"\u00b6\u00b3\u0001\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000"+
		"\u00b6\u00b5\u0001\u0000\u0000\u0000\u00b7\u00b9\u0001\u0000\u0000\u0000"+
		"\u00b8\u00ba\u0003\n\u0005\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000\u00b9"+
		"\u00ba\u0001\u0000\u0000\u0000\u00ba\u00bc\u0001\u0000\u0000\u0000\u00bb"+
		"\u00b6\u0001\u0000\u0000\u0000\u00bc\u00bf\u0001\u0000\u0000\u0000\u00bd"+
		"\u00bb\u0001\u0000\u0000\u0000\u00bd\u00be\u0001\u0000\u0000\u0000\u00be"+
		"\u0007\u0001\u0000\u0000\u0000\u00bf\u00bd\u0001\u0000\u0000\u0000\u00c0"+
		"\u00c3\u0005\u0010\u0000\u0000\u00c1\u00c2\u0005\u000f\u0000\u0000\u00c2"+
		"\u00c4\u0005\u0011\u0000\u0000\u00c3\u00c1\u0001\u0000\u0000\u0000\u00c3"+
		"\u00c4\u0001\u0000\u0000\u0000\u00c4\t\u0001\u0000\u0000\u0000\u00c5\u00c8"+
		"\u0005\u000b\u0000\u0000\u00c6\u00c8\u0005\n\u0000\u0000\u00c7\u00c5\u0001"+
		"\u0000\u0000\u0000\u00c7\u00c6\u0001\u0000\u0000\u0000\u00c8\u000b\u0001"+
		"\u0000\u0000\u0000\u00c9\u00ce\u0003\u000e\u0007\u0000\u00ca\u00ce\u0003"+
		"\u0010\b\u0000\u00cb\u00ce\u0005\n\u0000\u0000\u00cc\u00ce\u0005\u000b"+
		"\u0000\u0000\u00cd\u00c9\u0001\u0000\u0000\u0000\u00cd\u00ca\u0001\u0000"+
		"\u0000\u0000\u00cd\u00cb\u0001\u0000\u0000\u0000\u00cd\u00cc\u0001\u0000"+
		"\u0000\u0000\u00ce\r\u0001\u0000\u0000\u0000\u00cf\u00d0\u0005\u0005\u0000"+
		"\u0000\u00d0\u000f\u0001\u0000\u0000\u0000\u00d1\u00d4\u0005\u0006\u0000"+
		"\u0000\u00d2\u00d4\u0005\u0007\u0000\u0000\u00d3\u00d1\u0001\u0000\u0000"+
		"\u0000\u00d3\u00d2\u0001\u0000\u0000\u0000\u00d4\u0011\u0001\u0000\u0000"+
		"\u0000\u00d5\u00d6\u0005\t\u0000\u0000\u00d6\u00d7\u0003\u0014\n\u0000"+
		"\u00d7\u00d8\u0005D\u0000\u0000\u00d8\u0013\u0001\u0000\u0000\u0000\u00d9"+
		"\u00db\u0003\u0016\u000b\u0000\u00da\u00d9\u0001\u0000\u0000\u0000\u00db"+
		"\u00de\u0001\u0000\u0000\u0000\u00dc\u00da\u0001\u0000\u0000\u0000\u00dc"+
		"\u00dd\u0001\u0000\u0000\u0000\u00dd\u0015\u0001\u0000\u0000\u0000\u00de"+
		"\u00dc\u0001\u0000\u0000\u0000\u00df\u00e3\u0003T*\u0000\u00e0\u00e3\u0003"+
		"V+\u0000\u00e1\u00e3\u0003\u0018\f\u0000\u00e2\u00df\u0001\u0000\u0000"+
		"\u0000\u00e2\u00e0\u0001\u0000\u0000\u0000\u00e2\u00e1\u0001\u0000\u0000"+
		"\u0000\u00e3\u0017\u0001\u0000\u0000\u0000\u00e4\u00e7\u0003\u001a\r\u0000"+
		"\u00e5\u00e7\u0003\u001e\u000f\u0000\u00e6\u00e4\u0001\u0000\u0000\u0000"+
		"\u00e6\u00e5\u0001\u0000\u0000\u0000\u00e7\u0019\u0001\u0000\u0000\u0000"+
		"\u00e8\u00ef\u0005\\\u0000\u0000\u00e9\u00eb\u0003T*\u0000\u00ea\u00e9"+
		"\u0001\u0000\u0000\u0000\u00ea\u00eb\u0001\u0000\u0000\u0000\u00eb\u00ec"+
		"\u0001\u0000\u0000\u0000\u00ec\u00ee\u0003\u001c\u000e\u0000\u00ed\u00ea"+
		"\u0001\u0000\u0000\u0000\u00ee\u00f1\u0001\u0000\u0000\u0000\u00ef\u00ed"+
		"\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000\u00f0\u00f3"+
		"\u0001\u0000\u0000\u0000\u00f1\u00ef\u0001\u0000\u0000\u0000\u00f2\u00f4"+
		"\u0003T*\u0000\u00f3\u00f2\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000"+
		"\u0000\u0000\u00f4\u00f7\u0001\u0000\u0000\u0000\u00f5\u00f8\u0003:\u001d"+
		"\u0000\u00f6\u00f8\u0005M\u0000\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000"+
		"\u00f7\u00f6\u0001\u0000\u0000\u0000\u00f8\u001b\u0001\u0000\u0000\u0000"+
		"\u00f9\u00fa\u0003F#\u0000\u00fa\u001d\u0001\u0000\u0000\u0000\u00fb\u00fd"+
		"\u0003 \u0010\u0000\u00fc\u00fe\u0003T*\u0000\u00fd\u00fc\u0001\u0000"+
		"\u0000\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000"+
		"\u0000\u0000\u00ff\u0100\u0003:\u001d\u0000\u0100\u001f\u0001\u0000\u0000"+
		"\u0000\u0101\u010d\u0003$\u0012\u0000\u0102\u0106\u0003\"\u0011\u0000"+
		"\u0103\u0105\u0003T*\u0000\u0104\u0103\u0001\u0000\u0000\u0000\u0105\u0108"+
		"\u0001\u0000\u0000\u0000\u0106\u0104\u0001\u0000\u0000\u0000\u0106\u0107"+
		"\u0001\u0000\u0000\u0000\u0107\u0109\u0001\u0000\u0000\u0000\u0108\u0106"+
		"\u0001\u0000\u0000\u0000\u0109\u010a\u0003$\u0012\u0000\u010a\u010c\u0001"+
		"\u0000\u0000\u0000\u010b\u0102\u0001\u0000\u0000\u0000\u010c\u010f\u0001"+
		"\u0000\u0000\u0000\u010d\u010b\u0001\u0000\u0000\u0000\u010d\u010e\u0001"+
		"\u0000\u0000\u0000\u010e!\u0001\u0000\u0000\u0000\u010f\u010d\u0001\u0000"+
		"\u0000\u0000\u0110\u0111\u0005N\u0000\u0000\u0111#\u0001\u0000\u0000\u0000"+
		"\u0112\u012b\u0003(\u0014\u0000\u0113\u0115\u0003T*\u0000\u0114\u0113"+
		"\u0001\u0000\u0000\u0000\u0115\u0116\u0001\u0000\u0000\u0000\u0116\u0114"+
		"\u0001\u0000\u0000\u0000\u0116\u0117\u0001\u0000\u0000\u0000\u0117\u0118"+
		"\u0001\u0000\u0000\u0000\u0118\u0119\u0003(\u0014\u0000\u0119\u012a\u0001"+
		"\u0000\u0000\u0000\u011a\u011c\u0003T*\u0000\u011b\u011a\u0001\u0000\u0000"+
		"\u0000\u011c\u011f\u0001\u0000\u0000\u0000\u011d\u011b\u0001\u0000\u0000"+
		"\u0000\u011d\u011e\u0001\u0000\u0000\u0000\u011e\u0120\u0001\u0000\u0000"+
		"\u0000\u011f\u011d\u0001\u0000\u0000\u0000\u0120\u0124\u0003&\u0013\u0000"+
		"\u0121\u0123\u0003T*\u0000\u0122\u0121\u0001\u0000\u0000\u0000\u0123\u0126"+
		"\u0001\u0000\u0000\u0000\u0124\u0122\u0001\u0000\u0000\u0000\u0124\u0125"+
		"\u0001\u0000\u0000\u0000\u0125\u0127\u0001\u0000\u0000\u0000\u0126\u0124"+
		"\u0001\u0000\u0000\u0000\u0127\u0128\u0003(\u0014\u0000\u0128\u012a\u0001"+
		"\u0000\u0000\u0000\u0129\u0114\u0001\u0000\u0000\u0000\u0129\u011d\u0001"+
		"\u0000\u0000\u0000\u012a\u012d\u0001\u0000\u0000\u0000\u012b\u0129\u0001"+
		"\u0000\u0000\u0000\u012b\u012c\u0001\u0000\u0000\u0000\u012c%\u0001\u0000"+
		"\u0000\u0000\u012d\u012b\u0001\u0000\u0000\u0000\u012e\u012f\u0007\u0000"+
		"\u0000\u0000\u012f\'\u0001\u0000\u0000\u0000\u0130\u0132\u0003*\u0015"+
		"\u0000\u0131\u0130\u0001\u0000\u0000\u0000\u0131\u0132\u0001\u0000\u0000"+
		"\u0000\u0132\u0136\u0001\u0000\u0000\u0000\u0133\u0135\u0003,\u0016\u0000"+
		"\u0134\u0133\u0001\u0000\u0000\u0000\u0135\u0138\u0001\u0000\u0000\u0000"+
		"\u0136\u0134\u0001\u0000\u0000\u0000\u0136\u0137\u0001\u0000\u0000\u0000"+
		"\u0137)\u0001\u0000\u0000\u0000\u0138\u0136\u0001\u0000\u0000\u0000\u0139"+
		"\u013c\u0005U\u0000\u0000\u013a\u013c\u0003R)\u0000\u013b\u0139\u0001"+
		"\u0000\u0000\u0000\u013b\u013a\u0001\u0000\u0000\u0000\u013c+\u0001\u0000"+
		"\u0000\u0000\u013d\u0142\u0003.\u0017\u0000\u013e\u0142\u00030\u0018\u0000"+
		"\u013f\u0142\u00032\u0019\u0000\u0140\u0142\u00038\u001c\u0000\u0141\u013d"+
		"\u0001\u0000\u0000\u0000\u0141\u013e\u0001\u0000\u0000\u0000\u0141\u013f"+
		"\u0001\u0000\u0000\u0000\u0141\u0140\u0001\u0000\u0000\u0000\u0142-\u0001"+
		"\u0000\u0000\u0000\u0143\u0144\u0005W\u0000\u0000\u0144\u0145\u0003R)"+
		"\u0000\u0145/\u0001\u0000\u0000\u0000\u0146\u0147\u0005V\u0000\u0000\u0147"+
		"\u0148\u0003R)\u0000\u01481\u0001\u0000\u0000\u0000\u0149\u014d\u0005"+
		"J\u0000\u0000\u014a\u014c\u0003T*\u0000\u014b\u014a\u0001\u0000\u0000"+
		"\u0000\u014c\u014f\u0001\u0000\u0000\u0000\u014d\u014b\u0001\u0000\u0000"+
		"\u0000\u014d\u014e\u0001\u0000\u0000\u0000\u014e\u0150\u0001\u0000\u0000"+
		"\u0000\u014f\u014d\u0001\u0000\u0000\u0000\u0150\u0160\u0003R)\u0000\u0151"+
		"\u0153\u0003T*\u0000\u0152\u0151\u0001\u0000\u0000\u0000\u0153\u0156\u0001"+
		"\u0000\u0000\u0000\u0154\u0152\u0001\u0000\u0000\u0000\u0154\u0155\u0001"+
		"\u0000\u0000\u0000\u0155\u0157\u0001\u0000\u0000\u0000\u0156\u0154\u0001"+
		"\u0000\u0000\u0000\u0157\u015b\u00034\u001a\u0000\u0158\u015a\u0003T*"+
		"\u0000\u0159\u0158\u0001\u0000\u0000\u0000\u015a\u015d\u0001\u0000\u0000"+
		"\u0000\u015b\u0159\u0001\u0000\u0000\u0000\u015b\u015c\u0001\u0000\u0000"+
		"\u0000\u015c\u015e\u0001\u0000\u0000\u0000\u015d\u015b\u0001\u0000\u0000"+
		"\u0000\u015e\u015f\u00036\u001b\u0000\u015f\u0161\u0001\u0000\u0000\u0000"+
		"\u0160\u0154\u0001\u0000\u0000\u0000\u0160\u0161\u0001\u0000\u0000\u0000"+
		"\u0161\u0165\u0001\u0000\u0000\u0000\u0162\u0164\u0003T*\u0000\u0163\u0162"+
		"\u0001\u0000\u0000\u0000\u0164\u0167\u0001\u0000\u0000\u0000\u0165\u0163"+
		"\u0001\u0000\u0000\u0000\u0165\u0166\u0001\u0000\u0000\u0000\u0166\u0168"+
		"\u0001\u0000\u0000\u0000\u0167\u0165\u0001\u0000\u0000\u0000\u0168\u0169"+
		"\u0005K\u0000\u0000\u01693\u0001\u0000\u0000\u0000\u016a\u0176\u0005X"+
		"\u0000\u0000\u016b\u016c\u0005Q\u0000\u0000\u016c\u0176\u0005X\u0000\u0000"+
		"\u016d\u016e\u0005R\u0000\u0000\u016e\u0176\u0005X\u0000\u0000\u016f\u0170"+
		"\u0005S\u0000\u0000\u0170\u0176\u0005X\u0000\u0000\u0171\u0172\u0005T"+
		"\u0000\u0000\u0172\u0176\u0005X\u0000\u0000\u0173\u0174\u0005U\u0000\u0000"+
		"\u0174\u0176\u0005X\u0000\u0000\u0175\u016a\u0001\u0000\u0000\u0000\u0175"+
		"\u016b\u0001\u0000\u0000\u0000\u0175\u016d\u0001\u0000\u0000\u0000\u0175"+
		"\u016f\u0001\u0000\u0000\u0000\u0175\u0171\u0001\u0000\u0000\u0000\u0175"+
		"\u0173\u0001\u0000\u0000\u0000\u01765\u0001\u0000\u0000\u0000\u0177\u017a"+
		"\u0003R)\u0000\u0178\u017a\u0005b\u0000\u0000\u0179\u0177\u0001\u0000"+
		"\u0000\u0000\u0179\u0178\u0001\u0000\u0000\u0000\u017a7\u0001\u0000\u0000"+
		"\u0000\u017b\u017d\u0005L\u0000\u0000\u017c\u017e\u0005L\u0000\u0000\u017d"+
		"\u017c\u0001\u0000\u0000\u0000\u017d\u017e\u0001\u0000\u0000\u0000\u017e"+
		"\u017f\u0001\u0000\u0000\u0000\u017f\u0188\u0003R)\u0000\u0180\u0184\u0005"+
		"H\u0000\u0000\u0181\u0183\u0003F#\u0000\u0182\u0181\u0001\u0000\u0000"+
		"\u0000\u0183\u0186\u0001\u0000\u0000\u0000\u0184\u0182\u0001\u0000\u0000"+
		"\u0000\u0184\u0185\u0001\u0000\u0000\u0000\u0185\u0187\u0001\u0000\u0000"+
		"\u0000\u0186\u0184\u0001\u0000\u0000\u0000\u0187\u0189\u0005I\u0000\u0000"+
		"\u0188\u0180\u0001\u0000\u0000\u0000\u0188\u0189\u0001\u0000\u0000\u0000"+
		"\u01899\u0001\u0000\u0000\u0000\u018a\u018e\u0005F\u0000\u0000\u018b\u018d"+
		"\u0003<\u001e\u0000\u018c\u018b\u0001\u0000\u0000\u0000\u018d\u0190\u0001"+
		"\u0000\u0000\u0000\u018e\u018c\u0001\u0000\u0000\u0000\u018e\u018f\u0001"+
		"\u0000\u0000\u0000\u018f\u0191\u0001\u0000\u0000\u0000\u0190\u018e\u0001"+
		"\u0000\u0000\u0000\u0191\u0192\u0005G\u0000\u0000\u0192;\u0001\u0000\u0000"+
		"\u0000\u0193\u0198\u0003T*\u0000\u0194\u0198\u0003V+\u0000\u0195\u0198"+
		"\u0003>\u001f\u0000\u0196\u0198\u0003\u0018\f\u0000\u0197\u0193\u0001"+
		"\u0000\u0000\u0000\u0197\u0194\u0001\u0000\u0000\u0000\u0197\u0195\u0001"+
		"\u0000\u0000\u0000\u0197\u0196\u0001\u0000\u0000\u0000\u0198=\u0001\u0000"+
		"\u0000\u0000\u0199\u019b\u0003@ \u0000\u019a\u019c\u0003T*\u0000\u019b"+
		"\u019a\u0001\u0000\u0000\u0000\u019b\u019c\u0001\u0000\u0000\u0000\u019c"+
		"\u019d\u0001\u0000\u0000\u0000\u019d\u019f\u0005L\u0000\u0000\u019e\u01a0"+
		"\u0003T*\u0000\u019f\u019e\u0001\u0000\u0000\u0000\u019f\u01a0\u0001\u0000"+
		"\u0000\u0000\u01a0\u01a1\u0001\u0000\u0000\u0000\u01a1\u01a3\u0003D\""+
		"\u0000\u01a2\u01a4\u0003T*\u0000\u01a3\u01a2\u0001\u0000\u0000\u0000\u01a3"+
		"\u01a4\u0001\u0000\u0000\u0000\u01a4\u01a6\u0001\u0000\u0000\u0000\u01a5"+
		"\u01a7\u0003B!\u0000\u01a6\u01a5\u0001\u0000\u0000\u0000\u01a6\u01a7\u0001"+
		"\u0000\u0000\u0000\u01a7\u01a9\u0001\u0000\u0000\u0000\u01a8\u01aa\u0003"+
		"T*\u0000\u01a9\u01a8\u0001\u0000\u0000\u0000\u01a9\u01aa\u0001\u0000\u0000"+
		"\u0000\u01aa\u01ac\u0001\u0000\u0000\u0000\u01ab\u01ad\u0005M\u0000\u0000"+
		"\u01ac\u01ab\u0001\u0000\u0000\u0000\u01ac\u01ad\u0001\u0000\u0000\u0000"+
		"\u01ad?\u0001\u0000\u0000\u0000\u01ae\u01b4\u0005]\u0000\u0000\u01af\u01b1"+
		"\u0005[\u0000\u0000\u01b0\u01af\u0001\u0000\u0000\u0000\u01b0\u01b1\u0001"+
		"\u0000\u0000\u0000\u01b1\u01b2\u0001\u0000\u0000\u0000\u01b2\u01b4\u0003"+
		"R)\u0000\u01b3\u01ae\u0001\u0000\u0000\u0000\u01b3\u01b0\u0001\u0000\u0000"+
		"\u0000\u01b4A\u0001\u0000\u0000\u0000\u01b5\u01b6\u0005Z\u0000\u0000\u01b6"+
		"\u01b7\u0003R)\u0000\u01b7C\u0001\u0000\u0000\u0000\u01b8\u01cb\u0003"+
		"F#\u0000\u01b9\u01bb\u0003T*\u0000\u01ba\u01b9\u0001\u0000\u0000\u0000"+
		"\u01bb\u01bc\u0001\u0000\u0000\u0000\u01bc\u01ba\u0001\u0000\u0000\u0000"+
		"\u01bc\u01bd\u0001\u0000\u0000\u0000\u01bd\u01be\u0001\u0000\u0000\u0000"+
		"\u01be\u01bf\u0003F#\u0000\u01bf\u01ca\u0001\u0000\u0000\u0000\u01c0\u01c4"+
		"\u0003\"\u0011\u0000\u01c1\u01c3\u0003T*\u0000\u01c2\u01c1\u0001\u0000"+
		"\u0000\u0000\u01c3\u01c6\u0001\u0000\u0000\u0000\u01c4\u01c2\u0001\u0000"+
		"\u0000\u0000\u01c4\u01c5\u0001\u0000\u0000\u0000\u01c5\u01c7\u0001\u0000"+
		"\u0000\u0000\u01c6\u01c4\u0001\u0000\u0000\u0000\u01c7\u01c8\u0003F#\u0000"+
		"\u01c8\u01ca\u0001\u0000\u0000\u0000\u01c9\u01ba\u0001\u0000\u0000\u0000"+
		"\u01c9\u01c0\u0001\u0000\u0000\u0000\u01ca\u01cd\u0001\u0000\u0000\u0000"+
		"\u01cb\u01c9\u0001\u0000\u0000\u0000\u01cb\u01cc\u0001\u0000\u0000\u0000"+
		"\u01ccE\u0001\u0000\u0000\u0000\u01cd\u01cb\u0001\u0000\u0000\u0000\u01ce"+
		"\u01e9\u0003H$\u0000\u01cf\u01e9\u0003N\'\u0000\u01d0\u01e9\u0003P(\u0000"+
		"\u01d1\u01e9\u0005b\u0000\u0000\u01d2\u01e9\u0005a\u0000\u0000\u01d3\u01e9"+
		"\u0003R)\u0000\u01d4\u01e9\u0005W\u0000\u0000\u01d5\u01e9\u0005V\u0000"+
		"\u0000\u01d6\u01e9\u0005N\u0000\u0000\u01d7\u01e9\u0005L\u0000\u0000\u01d8"+
		"\u01e9\u0005M\u0000\u0000\u01d9\u01e9\u0005Y\u0000\u0000\u01da\u01e9\u0005"+
		"P\u0000\u0000\u01db\u01e9\u0005O\u0000\u0000\u01dc\u01e9\u0005Q\u0000"+
		"\u0000\u01dd\u01e9\u0005R\u0000\u0000\u01de\u01e9\u0005U\u0000\u0000\u01df"+
		"\u01e9\u0005X\u0000\u0000\u01e0\u01e9\u0005H\u0000\u0000\u01e1\u01e9\u0005"+
		"I\u0000\u0000\u01e2\u01e9\u0005J\u0000\u0000\u01e3\u01e9\u0005K\u0000"+
		"\u0000\u01e4\u01e9\u0005F\u0000\u0000\u01e5\u01e9\u0005G\u0000\u0000\u01e6"+
		"\u01e9\u0005Z\u0000\u0000\u01e7\u01e9\u0005e\u0000\u0000\u01e8\u01ce\u0001"+
		"\u0000\u0000\u0000\u01e8\u01cf\u0001\u0000\u0000\u0000\u01e8\u01d0\u0001"+
		"\u0000\u0000\u0000\u01e8\u01d1\u0001\u0000\u0000\u0000\u01e8\u01d2\u0001"+
		"\u0000\u0000\u0000\u01e8\u01d3\u0001\u0000\u0000\u0000\u01e8\u01d4\u0001"+
		"\u0000\u0000\u0000\u01e8\u01d5\u0001\u0000\u0000\u0000\u01e8\u01d6\u0001"+
		"\u0000\u0000\u0000\u01e8\u01d7\u0001\u0000\u0000\u0000\u01e8\u01d8\u0001"+
		"\u0000\u0000\u0000\u01e8\u01d9\u0001\u0000\u0000\u0000\u01e8\u01da\u0001"+
		"\u0000\u0000\u0000\u01e8\u01db\u0001\u0000\u0000\u0000\u01e8\u01dc\u0001"+
		"\u0000\u0000\u0000\u01e8\u01dd\u0001\u0000\u0000\u0000\u01e8\u01de\u0001"+
		"\u0000\u0000\u0000\u01e8\u01df\u0001\u0000\u0000\u0000\u01e8\u01e0\u0001"+
		"\u0000\u0000\u0000\u01e8\u01e1\u0001\u0000\u0000\u0000\u01e8\u01e2\u0001"+
		"\u0000\u0000\u0000\u01e8\u01e3\u0001\u0000\u0000\u0000\u01e8\u01e4\u0001"+
		"\u0000\u0000\u0000\u01e8\u01e5\u0001\u0000\u0000\u0000\u01e8\u01e6\u0001"+
		"\u0000\u0000\u0000\u01e8\u01e7\u0001\u0000\u0000\u0000\u01e9G\u0001\u0000"+
		"\u0000\u0000\u01ea\u01eb\u0003R)\u0000\u01eb\u01ed\u0005H\u0000\u0000"+
		"\u01ec\u01ee\u0003J%\u0000\u01ed\u01ec\u0001\u0000\u0000\u0000\u01ed\u01ee"+
		"\u0001\u0000\u0000\u0000\u01ee\u01ef\u0001\u0000\u0000\u0000\u01ef\u01f0"+
		"\u0005I\u0000\u0000\u01f0I\u0001\u0000\u0000\u0000\u01f1\u01fd\u0003L"+
		"&\u0000\u01f2\u01f6\u0003\"\u0011\u0000\u01f3\u01f5\u0003T*\u0000\u01f4"+
		"\u01f3\u0001\u0000\u0000\u0000\u01f5\u01f8\u0001\u0000\u0000\u0000\u01f6"+
		"\u01f4\u0001\u0000\u0000\u0000\u01f6\u01f7\u0001\u0000\u0000\u0000\u01f7"+
		"\u01f9\u0001\u0000\u0000\u0000\u01f8\u01f6\u0001\u0000\u0000\u0000\u01f9"+
		"\u01fa\u0003L&\u0000\u01fa\u01fc\u0001\u0000\u0000\u0000\u01fb\u01f2\u0001"+
		"\u0000\u0000\u0000\u01fc\u01ff\u0001\u0000\u0000\u0000\u01fd\u01fb\u0001"+
		"\u0000\u0000\u0000\u01fd\u01fe\u0001\u0000\u0000\u0000\u01feK\u0001\u0000"+
		"\u0000\u0000\u01ff\u01fd\u0001\u0000\u0000\u0000\u0200\u0202\u0003F#\u0000"+
		"\u0201\u0200\u0001\u0000\u0000\u0000\u0202\u0203\u0001\u0000\u0000\u0000"+
		"\u0203\u0201\u0001\u0000\u0000\u0000\u0203\u0204\u0001\u0000\u0000\u0000"+
		"\u0204M\u0001\u0000\u0000\u0000\u0205\u0208\u0005^\u0000\u0000\u0206\u0208"+
		"\u0003R)\u0000\u0207\u0205\u0001\u0000\u0000\u0000\u0207\u0206\u0001\u0000"+
		"\u0000\u0000\u0208O\u0001\u0000\u0000\u0000\u0209\u020a\u0007\u0001\u0000"+
		"\u0000\u020aQ\u0001\u0000\u0000\u0000\u020b\u020c\u0007\u0002\u0000\u0000"+
		"\u020cS\u0001\u0000\u0000\u0000\u020d\u020f\u0005d\u0000\u0000\u020e\u020d"+
		"\u0001\u0000\u0000\u0000\u020f\u0210\u0001\u0000\u0000\u0000\u0210\u020e"+
		"\u0001\u0000\u0000\u0000\u0210\u0211\u0001\u0000\u0000\u0000\u0211U\u0001"+
		"\u0000\u0000\u0000\u0212\u0213\u0005E\u0000\u0000\u0213W\u0001\u0000\u0000"+
		"\u0000\u0214\u0215\u0005\u0001\u0000\u0000\u0215\u0216\u0003Z-\u0000\u0216"+
		"\u0217\u0005\u0013\u0000\u0000\u0217Y\u0001\u0000\u0000\u0000\u0218\u0219"+
		"\u0005\u0016\u0000\u0000\u0219\u0237\u0003f3\u0000\u021a\u021b\u0005\u0017"+
		"\u0000\u0000\u021b\u0237\u0003f3\u0000\u021c\u0237\u0005\u0018\u0000\u0000"+
		"\u021d\u0237\u0005\u0019\u0000\u0000\u021e\u021f\u0005\u001a\u0000\u0000"+
		"\u021f\u0220\u0003\\.\u0000\u0220\u0221\u0005\u001b\u0000\u0000\u0221"+
		"\u0222\u0003f3\u0000\u0222\u0237\u0001\u0000\u0000\u0000\u0223\u0237\u0005"+
		"\u001c\u0000\u0000\u0224\u0225\u0005\u001d\u0000\u0000\u0225\u0237\u0005"+
		">\u0000\u0000\u0226\u0228\u0005\u001e\u0000\u0000\u0227\u0229\u0005>\u0000"+
		"\u0000\u0228\u0227\u0001\u0000\u0000\u0000\u0228\u0229\u0001\u0000\u0000"+
		"\u0000\u0229\u0237\u0001\u0000\u0000\u0000\u022a\u022b\u0005\u001f\u0000"+
		"\u0000\u022b\u022c\u0005>\u0000\u0000\u022c\u022e\u00053\u0000\u0000\u022d"+
		"\u022f\u0003^/\u0000\u022e\u022d\u0001\u0000\u0000\u0000\u022e\u022f\u0001"+
		"\u0000\u0000\u0000\u022f\u0230\u0001\u0000\u0000\u0000\u0230\u0237\u0005"+
		"4\u0000\u0000\u0231\u0237\u0005 \u0000\u0000\u0232\u0233\u0005!\u0000"+
		"\u0000\u0233\u0237\u0005=\u0000\u0000\u0234\u0235\u0005\"\u0000\u0000"+
		"\u0235\u0237\u0005=\u0000\u0000\u0236\u0218\u0001\u0000\u0000\u0000\u0236"+
		"\u021a\u0001\u0000\u0000\u0000\u0236\u021c\u0001\u0000\u0000\u0000\u0236"+
		"\u021d\u0001\u0000\u0000\u0000\u0236\u021e\u0001\u0000\u0000\u0000\u0236"+
		"\u0223\u0001\u0000\u0000\u0000\u0236\u0224\u0001\u0000\u0000\u0000\u0236"+
		"\u0226\u0001\u0000\u0000\u0000\u0236\u022a\u0001\u0000\u0000\u0000\u0236"+
		"\u0231\u0001\u0000\u0000\u0000\u0236\u0232\u0001\u0000\u0000\u0000\u0236"+
		"\u0234\u0001\u0000\u0000\u0000\u0237[\u0001\u0000\u0000\u0000\u0238\u023d"+
		"\u0005>\u0000\u0000\u0239\u023a\u00052\u0000\u0000\u023a\u023c\u0005>"+
		"\u0000\u0000\u023b\u0239\u0001\u0000\u0000\u0000\u023c\u023f\u0001\u0000"+
		"\u0000\u0000\u023d\u023b\u0001\u0000\u0000\u0000\u023d\u023e\u0001\u0000"+
		"\u0000\u0000\u023e]\u0001\u0000\u0000\u0000\u023f\u023d\u0001\u0000\u0000"+
		"\u0000\u0240\u0245\u0005>\u0000\u0000\u0241\u0242\u00052\u0000\u0000\u0242"+
		"\u0244\u0005>\u0000\u0000\u0243\u0241\u0001\u0000\u0000\u0000\u0244\u0247"+
		"\u0001\u0000\u0000\u0000\u0245\u0243\u0001\u0000\u0000\u0000\u0245\u0246"+
		"\u0001\u0000\u0000\u0000\u0246_\u0001\u0000\u0000\u0000\u0247\u0245\u0001"+
		"\u0000\u0000\u0000\u0248\u0249\u0005\u0002\u0000\u0000\u0249\u024a\u0003"+
		"f3\u0000\u024a\u024b\u0005\u0014\u0000\u0000\u024ba\u0001\u0000\u0000"+
		"\u0000\u024c\u0252\u0005>\u0000\u0000\u024d\u024f\u00053\u0000\u0000\u024e"+
		"\u0250\u0003d2\u0000\u024f\u024e\u0001\u0000\u0000\u0000\u024f\u0250\u0001"+
		"\u0000\u0000\u0000\u0250\u0251\u0001\u0000\u0000\u0000\u0251\u0253\u0005"+
		"4\u0000\u0000\u0252\u024d\u0001\u0000\u0000\u0000\u0252\u0253\u0001\u0000"+
		"\u0000\u0000\u0253c\u0001\u0000\u0000\u0000\u0254\u0259\u0003f3\u0000"+
		"\u0255\u0256\u00052\u0000\u0000\u0256\u0258\u0003f3\u0000\u0257\u0255"+
		"\u0001\u0000\u0000\u0000\u0258\u025b\u0001\u0000\u0000\u0000\u0259\u0257"+
		"\u0001\u0000\u0000\u0000\u0259\u025a\u0001\u0000\u0000\u0000\u025ae\u0001"+
		"\u0000\u0000\u0000\u025b\u0259\u0001\u0000\u0000\u0000\u025c\u0261\u0003"+
		"h4\u0000\u025d\u025e\u0005%\u0000\u0000\u025e\u0260\u0003h4\u0000\u025f"+
		"\u025d\u0001\u0000\u0000\u0000\u0260\u0263\u0001\u0000\u0000\u0000\u0261"+
		"\u025f\u0001\u0000\u0000\u0000\u0261\u0262\u0001\u0000\u0000\u0000\u0262"+
		"g\u0001\u0000\u0000\u0000\u0263\u0261\u0001\u0000\u0000\u0000\u0264\u0269"+
		"\u0003j5\u0000\u0265\u0266\u0005$\u0000\u0000\u0266\u0268\u0003j5\u0000"+
		"\u0267\u0265\u0001\u0000\u0000\u0000\u0268\u026b\u0001\u0000\u0000\u0000"+
		"\u0269\u0267\u0001\u0000\u0000\u0000\u0269\u026a\u0001\u0000\u0000\u0000"+
		"\u026ai\u0001\u0000\u0000\u0000\u026b\u0269\u0001\u0000\u0000\u0000\u026c"+
		"\u026d\u0005#\u0000\u0000\u026d\u0270\u0003j5\u0000\u026e\u0270\u0003"+
		"l6\u0000\u026f\u026c\u0001\u0000\u0000\u0000\u026f\u026e\u0001\u0000\u0000"+
		"\u0000\u0270k\u0001\u0000\u0000\u0000\u0271\u0274\u0003n7\u0000\u0272"+
		"\u0273\u0007\u0003\u0000\u0000\u0273\u0275\u0003n7\u0000\u0274\u0272\u0001"+
		"\u0000\u0000\u0000\u0274\u0275\u0001\u0000\u0000\u0000\u0275m\u0001\u0000"+
		"\u0000\u0000\u0276\u027b\u0003p8\u0000\u0277\u0278\u0007\u0004\u0000\u0000"+
		"\u0278\u027a\u0003p8\u0000\u0279\u0277\u0001\u0000\u0000\u0000\u027a\u027d"+
		"\u0001\u0000\u0000\u0000\u027b\u0279\u0001\u0000\u0000\u0000\u027b\u027c"+
		"\u0001\u0000\u0000\u0000\u027co\u0001\u0000\u0000\u0000\u027d\u027b\u0001"+
		"\u0000\u0000\u0000\u027e\u0283\u0003r9\u0000\u027f\u0280\u0007\u0005\u0000"+
		"\u0000\u0280\u0282\u0003r9\u0000\u0281\u027f\u0001\u0000\u0000\u0000\u0282"+
		"\u0285\u0001\u0000\u0000\u0000\u0283\u0281\u0001\u0000\u0000\u0000\u0283"+
		"\u0284\u0001\u0000\u0000\u0000\u0284q\u0001\u0000\u0000\u0000\u0285\u0283"+
		"\u0001\u0000\u0000\u0000\u0286\u028b\u0003t:\u0000\u0287\u0288\u00050"+
		"\u0000\u0000\u0288\u028a\u0003b1\u0000\u0289\u0287\u0001\u0000\u0000\u0000"+
		"\u028a\u028d\u0001\u0000\u0000\u0000\u028b\u0289\u0001\u0000\u0000\u0000"+
		"\u028b\u028c\u0001\u0000\u0000\u0000\u028cs\u0001\u0000\u0000\u0000\u028d"+
		"\u028b\u0001\u0000\u0000\u0000\u028e\u028f\u00058\u0000\u0000\u028f\u0296"+
		"\u0003t:\u0000\u0290\u0296\u0003v;\u0000\u0291\u0292\u00053\u0000\u0000"+
		"\u0292\u0293\u0003f3\u0000\u0293\u0294\u00054\u0000\u0000\u0294\u0296"+
		"\u0001\u0000\u0000\u0000\u0295\u028e\u0001\u0000\u0000\u0000\u0295\u0290"+
		"\u0001\u0000\u0000\u0000\u0295\u0291\u0001\u0000\u0000\u0000\u0296u\u0001"+
		"\u0000\u0000\u0000\u0297\u029b\u0003z=\u0000\u0298\u029a\u0003x<\u0000"+
		"\u0299\u0298\u0001\u0000\u0000\u0000\u029a\u029d\u0001\u0000\u0000\u0000"+
		"\u029b\u0299\u0001\u0000\u0000\u0000\u029b\u029c\u0001\u0000\u0000\u0000"+
		"\u029cw\u0001\u0000\u0000\u0000\u029d\u029b\u0001\u0000\u0000\u0000\u029e"+
		"\u029f\u00051\u0000\u0000\u029f\u02aa\u0005>\u0000\u0000\u02a0\u02a1\u0005"+
		"5\u0000\u0000\u02a1\u02a2\u0003f3\u0000\u02a2\u02a3\u00056\u0000\u0000"+
		"\u02a3\u02aa\u0001\u0000\u0000\u0000\u02a4\u02a6\u00053\u0000\u0000\u02a5"+
		"\u02a7\u0003d2\u0000\u02a6\u02a5\u0001\u0000\u0000\u0000\u02a6\u02a7\u0001"+
		"\u0000\u0000\u0000\u02a7\u02a8\u0001\u0000\u0000\u0000\u02a8\u02aa\u0005"+
		"4\u0000\u0000\u02a9\u029e\u0001\u0000\u0000\u0000\u02a9\u02a0\u0001\u0000"+
		"\u0000\u0000\u02a9\u02a4\u0001\u0000\u0000\u0000\u02aay\u0001\u0000\u0000"+
		"\u0000\u02ab\u02ac\u0007\u0006\u0000\u0000\u02ac{\u0001\u0000\u0000\u0000"+
		"\u02ad\u02b1\u0005\u0003\u0000\u0000\u02ae\u02b0\u0005@\u0000\u0000\u02af"+
		"\u02ae\u0001\u0000\u0000\u0000\u02b0\u02b3\u0001\u0000\u0000\u0000\u02b1"+
		"\u02af\u0001\u0000\u0000\u0000\u02b1\u02b2\u0001\u0000\u0000\u0000\u02b2"+
		"\u02b4\u0001\u0000\u0000\u0000\u02b3\u02b1\u0001\u0000\u0000\u0000\u02b4"+
		"\u02b5\u0005?\u0000\u0000\u02b5}\u0001\u0000\u0000\u0000U\u0081\u0089"+
		"\u0090\u0098\u00a2\u00a5\u00ab\u00ae\u00b6\u00b9\u00bd\u00c3\u00c7\u00cd"+
		"\u00d3\u00dc\u00e2\u00e6\u00ea\u00ef\u00f3\u00f7\u00fd\u0106\u010d\u0116"+
		"\u011d\u0124\u0129\u012b\u0131\u0136\u013b\u0141\u014d\u0154\u015b\u0160"+
		"\u0165\u0175\u0179\u017d\u0184\u0188\u018e\u0197\u019b\u019f\u01a3\u01a6"+
		"\u01a9\u01ac\u01b0\u01b3\u01bc\u01c4\u01c9\u01cb\u01e8\u01ed\u01f6\u01fd"+
		"\u0203\u0207\u0210\u0228\u022e\u0236\u023d\u0245\u024f\u0252\u0259\u0261"+
		"\u0269\u026f\u0274\u027b\u0283\u028b\u0295\u029b\u02a6\u02a9\u02b1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}