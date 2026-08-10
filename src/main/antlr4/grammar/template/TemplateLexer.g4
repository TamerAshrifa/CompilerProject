lexer grammar TemplateLexer;

// ----------------------------------------------------------------------
// DEFAULT MODE (DEFAULT_MODE)
// ----------------------------------------------------------------------

// --- Jinja Delimiters ---
JINJA_BLOCK_OPEN   : '{%' -> pushMode(JINJA_MODE);         // Matches '{%' and enters the shared Jinja mode.
JINJA_EXPR_OPEN    : '{{' -> pushMode(JINJA_MODE);         // Matches '{{' and enters the shared Jinja mode.
JINJA_COMMENT_OPEN : '{#' -> pushMode(JINJA_COMMENT_MODE); // Matches '{#' and enters Jinja comment mode.

// --- HTML Tags and Comments ---
TAG_OPEN           : '<' -> pushMode(TAG_MODE);            // Matches '<' and enters tag parsing mode.
HTML_DOCTYPE       : '<!' [dD][oO][cC][tT][yY][pP][eE] .*? '>' ;
HTML_COMMENT       : '<!--' .*? '-->' ;                    // Matches standard HTML comments, e.g., <!-- ... -->.
HTML_CONDITIONAL_COMMENT
                   : '<!' .*? '>' ;                        // Matches IE conditional comments or declarations.
CDATA              : '<![CDATA[' .*? ']]>' ;


STYLE_OPEN         : '<' [sS][tT][yY][lL][eE] ( ~'>' )* '>'
                     -> pushMode(STYLE_MODE);              // Matches a <style> opening tag and enters style mode.

// --- Whitespace and Text ---
SEA_WS             : [ \t\r\n]+ ;                           // Matches one or more whitespace characters.
HTML_TEXT          : ~('<' | '{')+ ;                        // Matches raw HTML text outside tags and Jinja.

// ----------------------------------------------------------------------
// TAG MODE - For parsing inside an HTML tag (e.g., <div class="..">).
// ----------------------------------------------------------------------

mode TAG_MODE;

// --- Tag Delimiters ---
TAG_SLASH_CLOSE    : '/>' -> popMode;                       // Matches '/>' for self-closing tags.
TAG_CLOSE          : '>' -> popMode;                        // Matches '>' and exits tag mode.
TAG_SLASH          : '/';                                   // Matches '/' used in closing tags.
TAG_EQUALS         : '=';                                   // Matches '=' in attribute assignments.

// --- Tag Content ---
TAG_NAME           : [a-zA-Z][a-zA-Z0-9\-_]*;               // Matches an HTML tag or attribute name.

// --- Attribute Values (allows embedded Jinja expressions) ---
ATTVALUE_VALUE
    : '"' (ATTVALUE_CHAR_DQ | JINJA_EXPR_INSIDE_DQ)* '"'    // Double-quoted attribute value.
    | '\'' (ATTVALUE_CHAR_SQ | JINJA_EXPR_INSIDE_SQ)* '\''  // Single-quoted attribute value.
    ;

TAG_WHITESPACE     : [ \t\r\n]+ -> skip;                    // Skips whitespace inside tag declarations.

// --- Fragments for Attribute Values ---
fragment ATTVALUE_CHAR_DQ
    : ~["] ;                                                // Matches any character except double quote.

fragment ATTVALUE_CHAR_SQ
    : ~['] ;                                                // Matches any character except single quote.

// --- Fragments for Embedded Jinja ---
fragment JINJA_EXPR_INSIDE_DQ
    : '{{' .*? '}}' ;                                       // Matches Jinja expression inside double quotes.

fragment JINJA_EXPR_INSIDE_SQ
    : '{{' .*? '}}' ;                                       // Matches Jinja expression inside single quotes.

// ----------------------------------------------------------------------
// JINJA MODES - For parsing content within Jinja delimiters.
// ----------------------------------------------------------------------

// --- Jinja Mode: shared by {% ... %} and {{ ... }}, since both use the
// same expression grammar (identifiers, literals, operators, filters);
// only the closing delimiter differs, so both close-tokens live here and
// the parser tells the two forms apart by which OPEN/CLOSE pair is used. ---
mode JINJA_MODE;

JINJA_BLOCK_CLOSE : '%}' -> popMode;   // Closes '{% ... %}' and returns to the previous mode.
JINJA_EXPR_CLOSE  : '}}' -> popMode;   // Closes '{{ ... }}' and returns to the previous mode.

JJ_WS : [ \t\r\n]+ -> skip;            // Whitespace inside a Jinja tag/expression is insignificant.

// --- Keywords (checked before JJ_IDENTIFIER so they are not swallowed by it) ---
JJ_IF       : 'if' ;
JJ_ELIF     : 'elif' ;
JJ_ELSE     : 'else' ;
JJ_ENDIF    : 'endif' ;
JJ_FOR      : 'for' ;
JJ_IN       : 'in' ;
JJ_ENDFOR   : 'endfor' ;
JJ_BLOCK    : 'block' ;
JJ_ENDBLOCK : 'endblock' ;
JJ_MACRO    : 'macro' ;
JJ_ENDMACRO : 'endmacro' ;
JJ_EXTENDS  : 'extends' ;
JJ_INCLUDE  : 'include' ;
JJ_NOT      : 'not' ;
JJ_AND      : 'and' ;
JJ_OR       : 'or' ;
JJ_TRUE     : 'true' | 'True' ;
JJ_FALSE    : 'false' | 'False' ;
JJ_NONE     : 'none' | 'None' ;

// --- Operators & punctuation ---
JJ_EQ       : '==' ;
JJ_NEQ      : '!=' ;
JJ_LE       : '<=' ;
JJ_GE       : '>=' ;
JJ_LT       : '<' ;
JJ_GT       : '>' ;
JJ_ASSIGN   : '=' ;
JJ_PIPE     : '|' ;
JJ_DOT      : '.' ;
JJ_COMMA    : ',' ;
JJ_LPAREN   : '(' ;
JJ_RPAREN   : ')' ;
JJ_LBRACKET : '[' ;
JJ_RBRACKET : ']' ;
JJ_PLUS     : '+' ;
JJ_MINUS    : '-' ;
JJ_STAR     : '*' ;
JJ_SLASH    : '/' ;
JJ_TILDE    : '~' ;    // Jinja's string-concatenation operator.

JJ_NUMBER     : [0-9]+ ('.' [0-9]+)? ;
JJ_STRING     : '"' (~["\\])* '"'
              | '\'' (~['\\])* '\'' ;
JJ_IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]* ;

// --- Jinja Comment Mode ---
mode JINJA_COMMENT_MODE;

JINJA_COMMENT_CLOSE
    : '#}' -> popMode;                                      // Closes Jinja comment and returns to previous mode.

JINJA_COMMENT_CONTENT
    : . ;                                                   // Matches any character inside a Jinja comment.

// ----------------------------------------------------------------------
// STYLE MODE - For parsing content inside a <style> tag.
// ----------------------------------------------------------------------

mode STYLE_MODE;

// --- Embedded Jinja Delimiters ---
JINJA_BLOCK_OPEN_IN_STYLE
    : '{%' -> pushMode(JINJA_MODE);                         // Allows Jinja blocks inside <style>.

JINJA_EXPR_OPEN_IN_STYLE
    : '{{' -> pushMode(JINJA_MODE);                         // Allows Jinja expressions inside <style>.

JINJA_COMMENT_OPEN_IN_STYLE
    : '{#' -> pushMode(JINJA_COMMENT_MODE);                 // Allows Jinja comments inside <style>.

// --- Style Closing Tag ---
STYLE_CLOSE
    : '</' [sS][tT][yY][lL][eE] [ \t\r\n]* '>'
      -> popMode;                                          // Matches closing </style> tag.

// --- CSS comments ---
CSS_COMMENT
    : '/*' .*? '*/'
    ;

// --- CSS punctuation ---
CSS_LBRACE    : '{' ;
CSS_RBRACE    : '}' ;
CSS_LPAREN    : '(' ;
CSS_RPAREN    : ')' ;
CSS_LBRACKET  : '[' ;
CSS_RBRACKET  : ']' ;
CSS_COLON     : ':' ;
CSS_SEMICOLON : ';' ;
CSS_COMMA     : ',' ;
CSS_GREATER   : '>' ;
CSS_PLUS      : '+' ;
CSS_TILDE     : '~' ;
CSS_PIPE      : '|' ;
CSS_CARET     : '^' ;
CSS_DOLLAR    : '$' ;
CSS_STAR      : '*' ;
CSS_DOT       : '.' ;
CSS_HASH      : '#' ;
CSS_EQUALS    : '=' ;
CSS_SLASH     : '/' ;
CSS_BANG      : '!' ;
CSS_MINUS     : '-' ;

// --- CSS values and identifiers ---
CSS_AT_KEYWORD
    : '@' CSS_IDENT_START CSS_IDENT_CONT*
    ;

CSS_CUSTOM_PROPERTY
    : '--' CSS_IDENT_START CSS_IDENT_CONT*
    ;

CSS_HEX_COLOR
    : '#' CSS_HEX_DIGIT CSS_HEX_DIGIT CSS_HEX_DIGIT (CSS_HEX_DIGIT CSS_HEX_DIGIT CSS_HEX_DIGIT)?
    ;

CSS_PERCENTAGE
    : CSS_NUMBER_BODY '%'
    ;

CSS_DIMENSION
    : CSS_NUMBER_BODY CSS_IDENT_START CSS_IDENT_CONT*
    ;

CSS_NUMBER
    : CSS_NUMBER_BODY
    ;

CSS_STRING
    : '"' (CSS_STRING_ESCAPE_SEQ | ~["\\\r\n])* '"'
    | '\'' (CSS_STRING_ESCAPE_SEQ | ~['\\\r\n])* '\''
    ;

CSS_IDENT
    : CSS_IDENT_START CSS_IDENT_CONT*
    ;

CSS_WS
    : [ \t\r\n]+
    ;

// --- CSS fallback ---
CSS_DELIM
    : .
    ;

fragment CSS_NUMBER_BODY
    : [0-9]+ ('.' [0-9]+)? ([eE] [+-]? [0-9]+)?
    ;

fragment CSS_IDENT_START
    : [a-zA-Z_]
    ;

fragment CSS_IDENT_CONT
    : [a-zA-Z0-9_-]
    ;

fragment CSS_HEX_DIGIT
    : [0-9a-fA-F]
    ;

fragment CSS_STRING_ESCAPE_SEQ
    : '\\' .
    ;






