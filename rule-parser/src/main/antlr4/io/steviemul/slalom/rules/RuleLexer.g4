lexer grammar RuleLexer;

// Keywords
ARG     : 'arg';
META    : 'meta';
ENTRY   : 'entry';
SINK    : 'sink';
ACCESS  : 'access';
TAINTED : 'tainted';
EQ      : 'eq';
EXPR    : 'expr';

// Valid Tokens
AT      : '@';
LPAREN  : '(';
RPAREN  : ')';
LBRACE  : '{';
RBRACE  : '}';
LBRACK  : '[';
RBRACK  : ']';
SEMI    : ';';
COMMA   : ',';
DOT     : '.';
EXCL    : '!';
WC      : '*';
COLON   : ':';
ARRAY   : LBRACK RBRACK;

NEWLINE    : ('\r'? '\n' | '\r')+ -> skip;
WS         : [ \t\r\n]+ -> skip ;

STRING         : '"' .*? '"';
IDENTIFIER     : Letter LetterOrDigit*;
NUMBER         : Digit+;

// Character classes
fragment Letter   : [a-zA-Z];
fragment Digit : [0-9];
fragment LetterOrDigit
  : Letter
  | Digit
  | '-'
  ;
