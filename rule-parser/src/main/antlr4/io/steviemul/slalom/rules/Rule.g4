grammar Rule;

rule
    : flowRule
    | patternRule EOF
    ;

patternRule
    : qualifiedName '.' methodCall;

qualifiedName
    : IDENTIFIER ('.' IDENTIFIER)*;

flowRule
    : sourceDeclaration?
      event*
      sinkDeclaration?
    ;

sourceDeclaration
    : SOURCE '[' patternRule ']';

sinkDeclaration
    : SINK '[' patternRule ']';

event
    : prefix=('+' | '-') IDENTIFIER;

methodCall
    : IDENTIFIER '(' argList? ')';

argList
    : typeOrLiteral (',' typeOrLiteral)*;

typeOrLiteral
    : literal
    | IDENTIFIER;

literal
    : Number+
    | STRING_LITERAL;

STRING_LITERAL : '"' AlphaNumeric* '"';

IDENTIFIER: Alpha AlphaNumeric*;

// Lexer
Alpha : [a-zA-Z];
Number : [0-9];
AlphaNumeric
    : Alpha
    | Number;

WS: [ \t\r\n]+ -> skip;

// Keywords
SINK:                'sink';
SOURCE:              'source';

// Separators
LPAREN:             '(';
RPAREN:             ')';
SEMI:               ';';
COMMA:              ',';
DOT:                '.';