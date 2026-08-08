parser grammar TemplateParser;

options {
    tokenVocab = TemplateLexer;
}

// ----------------------------------------------------------------------
// Root rule: represents the full HTML document
// ----------------------------------------------------------------------
htmlDocument
    : htmlElements* EOF
      #Document
    ;



// ----------------------------------------------------------------------
// Top-level HTML elements
// ----------------------------------------------------------------------
htmlElements
    : htmlMisc* htmlElement htmlMisc*
      #Elements
    ;

// ----------------------------------------------------------------------
// Single HTML element
// ----------------------------------------------------------------------
htmlElement
    : TAG_OPEN TAG_NAME htmlAttribute* (
        TAG_CLOSE (htmlContent TAG_OPEN TAG_SLASH TAG_NAME TAG_CLOSE)?
        | TAG_SLASH_CLOSE
      )                       #TagElement
    | style                   #StyleElement
    | jinjaBlock              #JinjaBlockElement
    | jinjaExpr               #JinjaExprElement
    | jinjaComment            #JinjaCommentElement
    ;

// ----------------------------------------------------------------------
// Content inside an HTML element
// ----------------------------------------------------------------------
htmlContent
    : htmlChardata?
      (
        ( htmlElement
        | CDATA
        | htmlComment
        | jinjaBlock
        | jinjaExpr
        | jinjaComment
        )
        htmlChardata?
      )*
      #Content
    ;

// ----------------------------------------------------------------------
// HTML attribute
// ----------------------------------------------------------------------
htmlAttribute
    : TAG_NAME (TAG_EQUALS ATTVALUE_VALUE)?
      #Attribute
    ;

// ----------------------------------------------------------------------
// Character data
// ----------------------------------------------------------------------
htmlChardata
    : HTML_TEXT   #TextContent
    | SEA_WS      #WhitespaceContent
    ;

// ----------------------------------------------------------------------
// Misc content between elements
// ----------------------------------------------------------------------
htmlMisc
    : htmlDoctype #MiscDoctype
    | htmlComment #MiscComment
    | SEA_WS      #MiscWhitespace
    | HTML_TEXT   #MiscText
    ;

htmlDoctype
  : HTML_DOCTYPE
  ;

// ----------------------------------------------------------------------
// HTML comments
// ----------------------------------------------------------------------
htmlComment
    : HTML_COMMENT               #StandardComment
    | HTML_CONDITIONAL_COMMENT   #ConditionalComment
    ;

// ----------------------------------------------------------------------
// Style block
// ----------------------------------------------------------------------
style
  : STYLE_OPEN cssStylesheet STYLE_CLOSE
      #StyleBlock
    ;

// ----------------------------------------------------------------------
// CSS stylesheet
// ----------------------------------------------------------------------
cssStylesheet
  : cssStylesheetItem*
  ;

cssStylesheetItem
  : cssWhitespace
  | cssComment
  | cssRule
  ;

cssRule
  : cssAtRule
  | cssQualifiedRule
  ;

cssAtRule
  : CSS_AT_KEYWORD (cssWhitespace? cssAtRulePrelude)* cssWhitespace? (cssBlock | CSS_SEMICOLON)
  ;

cssAtRulePrelude
  : cssComponentValue
  ;

cssQualifiedRule
  : cssSelectorList cssWhitespace? cssBlock
  ;

cssSelectorList
  : cssSelector (cssComma cssWhitespace* cssSelector)*
  ;

cssComma
  : CSS_COMMA
  ;

cssSelector
  : cssCompoundSelector (
    cssWhitespace+ cssCompoundSelector
    | cssWhitespace* cssCombinator cssWhitespace* cssCompoundSelector
    )*
  ;

cssCombinator
  : CSS_GREATER
  | CSS_PLUS
  | CSS_TILDE
  ;

cssCompoundSelector
  : cssTypeSelector? cssSimpleSelector*
  ;

cssTypeSelector
  : CSS_STAR
  | cssIdent
  ;

cssSimpleSelector
  : cssIdSelector
  | cssClassSelector
  | cssAttributeSelector
  | cssPseudoSelector
  ;

cssIdSelector
  : CSS_HASH cssIdent
  ;

cssClassSelector
  : CSS_DOT cssIdent
  ;

cssAttributeSelector
  : CSS_LBRACKET cssWhitespace* cssIdent (cssWhitespace* cssAttributeMatcher cssWhitespace* cssAttributeValue)? cssWhitespace* CSS_RBRACKET
  ;

cssAttributeMatcher
  : CSS_EQUALS
  | CSS_TILDE CSS_EQUALS
  | CSS_PIPE CSS_EQUALS
  | CSS_CARET CSS_EQUALS
  | CSS_DOLLAR CSS_EQUALS
  | CSS_STAR CSS_EQUALS
  ;

cssAttributeValue
  : cssIdent
  | CSS_STRING
  ;

cssPseudoSelector
  : CSS_COLON CSS_COLON? cssIdent (CSS_LPAREN cssComponentValue* CSS_RPAREN)?
  ;

cssBlock
  : CSS_LBRACE cssBlockItem* CSS_RBRACE
  ;

cssBlockItem
  : cssWhitespace
  | cssComment
  | cssDeclaration
  | cssRule
  ;

cssDeclaration
  : cssPropertyName cssWhitespace? CSS_COLON cssWhitespace? cssValueSequence cssWhitespace? cssImportant? cssWhitespace? CSS_SEMICOLON?
  ;

cssPropertyName
  : CSS_CUSTOM_PROPERTY
  | CSS_MINUS? cssIdent
  ;

cssImportant
  : CSS_BANG cssIdent
  ;

cssValueSequence
  : cssComponentValue (cssWhitespace+ cssComponentValue | cssComma cssWhitespace* cssComponentValue)*
  ;

cssComponentValue
  : cssFunctionCall
  | cssColor
  | cssMeasurement
  | CSS_STRING
  | CSS_NUMBER
  | cssIdent
  | CSS_HASH
  | CSS_DOT
  | CSS_COMMA
  | CSS_COLON
  | CSS_SEMICOLON
  | CSS_SLASH
  | CSS_PLUS
  | CSS_GREATER
  | CSS_TILDE
  | CSS_PIPE
  | CSS_STAR
  | CSS_EQUALS
  | CSS_LPAREN
  | CSS_RPAREN
  | CSS_LBRACKET
  | CSS_RBRACKET
  | CSS_LBRACE
  | CSS_RBRACE
  | CSS_BANG
  | CSS_DELIM
  ;

cssFunctionCall
  : cssIdent CSS_LPAREN cssFunctionArguments? CSS_RPAREN
  ;

cssFunctionArguments
  : cssFunctionArgument (cssComma cssWhitespace* cssFunctionArgument)*
  ;

cssFunctionArgument
  : cssComponentValue+
  ;

cssColor
  : CSS_HEX_COLOR
  | cssIdent
  ;

cssMeasurement
  : CSS_DIMENSION
  | CSS_PERCENTAGE
  ;

cssIdent
  : CSS_IDENT
  | CSS_CUSTOM_PROPERTY
  ;

cssWhitespace
  : CSS_WS+
  ;

cssComment
  : CSS_COMMENT
  ;

// ----------------------------------------------------------------------
// Jinja block
// ----------------------------------------------------------------------
// ----------------------------------------------------------------------
// Jinja block: {% ... %}. The tag's actual keyword/condition/expression
// syntax is recognized here by real grammar rules (not by capturing raw
// characters and pattern-matching them in Java afterwards).
// ----------------------------------------------------------------------
jinjaBlock
    : JINJA_BLOCK_OPEN jinjaTag JINJA_BLOCK_CLOSE
      #JinjaBlockRule
    ;

jinjaTag
    : JJ_IF jinjaOrExpr                                          # IfTag
    | JJ_ELIF jinjaOrExpr                                        # ElifTag
    | JJ_ELSE                                                    # ElseTag
    | JJ_ENDIF                                                   # EndIfTag
    | JJ_FOR jinjaForTargets JJ_IN jinjaOrExpr                    # ForTag
    | JJ_ENDFOR                                                  # EndForTag
    | JJ_BLOCK JJ_IDENTIFIER                                     # BlockTag
    | JJ_ENDBLOCK JJ_IDENTIFIER?                                 # EndBlockTag
    | JJ_MACRO JJ_IDENTIFIER JJ_LPAREN jinjaParamList? JJ_RPAREN  # MacroTag
    | JJ_ENDMACRO                                                # EndMacroTag
    | JJ_EXTENDS JJ_STRING                                       # ExtendsTag
    | JJ_INCLUDE JJ_STRING                                       # IncludeTag
    ;

jinjaForTargets
    : JJ_IDENTIFIER (JJ_COMMA JJ_IDENTIFIER)*
    ;

jinjaParamList
    : JJ_IDENTIFIER (JJ_COMMA JJ_IDENTIFIER)*
    ;

// ----------------------------------------------------------------------
// Jinja expression: {{ ... }}
// ----------------------------------------------------------------------
jinjaExpr
    : JINJA_EXPR_OPEN jinjaOrExpr JINJA_EXPR_CLOSE
      #JinjaExpression
    ;

jinjaFilterCall
    : JJ_IDENTIFIER (JJ_LPAREN jinjaArgList? JJ_RPAREN)?
    ;

jinjaArgList
    : jinjaOrExpr (JJ_COMMA jinjaOrExpr)*
    ;

// --- Expression grammar shared by tag conditions/iterables and {{ }} ---
// (precedence, loosest to tightest: or, and, not, comparison, +/-/~, * //,
// filters, unary minus, attribute/subscript/call trailers, atoms)
jinjaOrExpr
    : jinjaAndExpr (JJ_OR jinjaAndExpr)*
    ;

jinjaAndExpr
    : jinjaNotExpr (JJ_AND jinjaNotExpr)*
    ;

jinjaNotExpr
    : JJ_NOT jinjaNotExpr
    | jinjaComparisonExpr
    ;

jinjaComparisonExpr
    : jinjaAdditiveExpr ((JJ_EQ | JJ_NEQ | JJ_LE | JJ_GE | JJ_LT | JJ_GT) jinjaAdditiveExpr)?
    ;

jinjaAdditiveExpr
    : jinjaMultiplicativeExpr ((JJ_PLUS | JJ_MINUS | JJ_TILDE) jinjaMultiplicativeExpr)*
    ;

jinjaMultiplicativeExpr
    : jinjaFilteredPrimary ((JJ_STAR | JJ_SLASH) jinjaFilteredPrimary)*
    ;

jinjaFilteredPrimary
    : jinjaPrimary (JJ_PIPE jinjaFilterCall)*
    ;

jinjaPrimary
    : JJ_MINUS jinjaPrimary
    | jinjaAtomTrailer
    | JJ_LPAREN jinjaOrExpr JJ_RPAREN
    ;

jinjaAtomTrailer
    : jinjaAtom jinjaTrailer*
    ;

jinjaTrailer
    : JJ_DOT JJ_IDENTIFIER
    | JJ_LBRACKET jinjaOrExpr JJ_RBRACKET
    | JJ_LPAREN jinjaArgList? JJ_RPAREN
    ;

jinjaAtom
    : JJ_IDENTIFIER
    | JJ_NUMBER
    | JJ_STRING
    | JJ_TRUE
    | JJ_FALSE
    | JJ_NONE
    ;

// ----------------------------------------------------------------------
// Jinja comment
// ----------------------------------------------------------------------
jinjaComment
    : JINJA_COMMENT_OPEN JINJA_COMMENT_CONTENT* JINJA_COMMENT_CLOSE
      #JinjaCommentRule
    ;

