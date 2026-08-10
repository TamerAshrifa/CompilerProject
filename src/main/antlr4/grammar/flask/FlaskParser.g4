parser grammar FlaskParser;

options {
    tokenVocab = FlaskLexer;
}

program
    : (NEWLINE | statement)* EOF
    ;

statement
    : simple_statement
    | compound_statement
    ;

simple_statement
    : small_stmt NEWLINE
    ;

small_stmt
    : importStatement
    | assignmentStatement
    | annotatedAssignmentStatement
    | expression_statement
    | returnStatement
    | passStatement
    | breakStatement
    | continueStatement
    | delStatement
    | assertStatement
    | globalStatement
    | nonlocalStatement
    | raiseStatement
    ;

returnStatement
    : RETURN expression_list?
    ;

passStatement
    : PASS
    ;

breakStatement
    : BREAK
    ;

continueStatement
    : CONTINUE
    ;

delStatement
    : DEL targetList
    ;

targetList
    : target (COMMA target)*
    ;

assertStatement
    : ASSERT expression (COMMA expression)?
    ;

globalStatement
    : GLOBAL IDENTIFIER (COMMA IDENTIFIER)*
    ;

raiseStatement
    : RAISE (expression (FROM expression)?)?
    ;

nonlocalStatement
    : NONLOCAL IDENTIFIER (COMMA IDENTIFIER)*
    ;

compound_statement
    : decoratedDef
    | functionDef
    | asyncFunctionDef
    | ifStatement
    | forStatement
    | asyncForStatement
    | whileStatement
    | withStatement
    | asyncWithStatement
    | tryStatement
    | classStatement
    ;

asyncFunctionDef
    : ASYNC functionDef
    ;

asyncForStatement
    : ASYNC FOR targetList IN expression COLON suite (ELSE COLON suite)?
    ;

asyncWithStatement
    : ASYNC WITH withItem (COMMA withItem)* COLON suite
    ;

ifStatement
    : IF expression COLON suite
      (ELIF expression COLON suite)*
      (ELSE COLON suite)?
    ;

forStatement
    : FOR targetList IN expression COLON suite (ELSE COLON suite)?
    ;

whileStatement
    : WHILE expression COLON suite (ELSE COLON suite)?
    ;

withStatement
    : WITH withItem (COMMA withItem)* COLON suite
    ;

withItem
    : expression (AS targetList)?
    ;

tryStatement
    : TRY COLON suite
      (
        exceptClause+ (ELSE COLON suite)? (FINALLY COLON suite)?
      | FINALLY COLON suite
      )
    ;

exceptClause
    : EXCEPT expression (AS IDENTIFIER)? COLON suite
    | EXCEPT COLON suite
    ;

classStatement
    : CLASS IDENTIFIER (LPAREN expression_list? RPAREN)? COLON suite
    ;

expression_statement
    : expression
    ;

decoratedDef
    : decorator+ (asyncFunctionDef | functionDef)
    ;

decorator
    : AT dottedName (LPAREN arglist? RPAREN)? NEWLINE
    ;

functionDef
    : DEF IDENTIFIER LPAREN parameters? RPAREN COLON suite  // ← استخدم suite
    ;

parameters
    : parameter (COMMA parameter)* COMMA?
    ;

parameter
    : POWER IDENTIFIER (COLON expression)?             // **kwargs
    | MUL IDENTIFIER (COLON expression)?                // *args
    | IDENTIFIER (COLON expression)? (ASSIGN expression)?
    ;

annotatedAssignmentStatement
    : target COLON expression (ASSIGN expression)?
    ;

suite
    : simple_statement                    // ← one-liner: def foo(): return 5
    | NEWLINE INDENT statement+ DEDENT    // ← block
    ;

block
    : suite  // ← استخدم suite بدلاً من تعريف مباشر
    ;

importStatement
    : importNameStatement
    | importFromStatement
    ;

importNameStatement
    : IMPORT importAsName (COMMA importAsName)*
    ;

importFromStatement
    : FROM relativeImportTarget IMPORT (importAsNames | MUL)
    ;

relativeImportTarget
    : DOT+ dottedName?
    | dottedName
    ;

importAsName
    : dottedName (AS IDENTIFIER)?
    ;

importAsNames
    : importAsName (COMMA importAsName)*
    ;

dottedName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

assignmentStatement
    : target (ASSIGN | augmentedAssignmentOp) expression
    ;

target
    : IDENTIFIER (target_trailer)*
    ;

target_trailer
    : DOT IDENTIFIER
    | LBRACK subscriptList RBRACK
    ;

subscriptList
    : subscript (COMMA subscript)* COMMA?
    ;

subscript
    : expression? (COLON expression? (COLON expression?)?)?
    ;

augmentedAssignmentOp
    : ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN | DIV_ASSIGN
    ;

expression
    : lambdef
    | yield_expression
    | or_boolean_expression
    ;

lambdef
    : LAMBDA lambdaParameters? COLON expression
    ;

lambdaParameters
    : lambdaParameter (COMMA lambdaParameter)*
    ;

lambdaParameter
    : POWER IDENTIFIER
    | MUL IDENTIFIER
    | IDENTIFIER (ASSIGN expression)?
    ;

yield_expression
    : YIELD (FROM expression | expression_list)?
    ;

or_boolean_expression
    : and_boolean_expression (OR and_boolean_expression)*
    ;

and_boolean_expression
    : not_boolean_expression (AND not_boolean_expression)*
    ;

not_boolean_expression
    : NOT not_boolean_expression
    | comparison_expression
    ;

comparison_expression
    : additive_expression (comp_op additive_expression)*
    ;

comp_op
    : EQ | NEQ | LT | GT | LTE | GTE | IN | IS
    ;

additive_expression
    : multiplicative_expression ((ADD | SUB) multiplicative_expression)*
    ;

multiplicative_expression
    : unary_expression ((MUL | DIV | MOD) unary_expression)*
    ;

unary_expression
    : AWAIT unary_expression
    | (ADD | SUB) unary_expression
    | power_expression
    ;

power_expression
    : atom_expression (POWER power_expression)?
    ;

atom_expression
    : atom (trailer)*
    ;

atom
    : IDENTIFIER
    | NUMBER
    | STRING
    | TRUE
    | FALSE
    | NONE
    | LPAREN expression RPAREN
    | LBRACK listOrComprehension? RBRACK
    | LBRACE NEWLINE? dict_or_set? NEWLINE? RBRACE
    ;

listOrComprehension
    : expression comp_for      // list comprehension: [expr for target in iterable ...]
    | expression_list          // plain list literal: [a, b, c]
    ;

// One or more chained "for target in iterable [if cond]*" clauses, e.g.
// "for row in matrix for x in row if x > 0". Shared between list, set and
// dict comprehensions via dict_or_set below.
comp_for
    : FOR targetList IN or_boolean_expression comp_iter?
    ;

comp_iter
    : comp_for
    | comp_if
    ;

comp_if
    : IF or_boolean_expression comp_iter?
    ;

trailer
    : DOT IDENTIFIER
    | LPAREN arglist? RPAREN
    | LBRACK expression RBRACK
    ;

dict_or_set
    : dict_item comp_for       // dict comprehension: {key: value for target in iterable ...}
    | expression comp_for      // set comprehension: {expr for target in iterable ...}
    | dict_items
    | expression_list
    ;

dict_items
    : dict_item (COMMA NEWLINE? dict_item)* COMMA?
    ;

dict_item
    : expression COLON expression
    ;

expression_list
    : expression (COMMA expression)* COMMA?
    ;

arglist
    : argument (COMMA argument)* COMMA?
    ;

argument
    : expression comp_for      // generator expression: sum(x for x in items)
    | expression
    | IDENTIFIER ASSIGN expression
    | MUL expression
    | POWER expression
    ;

