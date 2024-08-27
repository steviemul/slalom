parser grammar RuleParser;

options {
  tokenVocab=RuleLexer;
}

/** Parser Rules **/
rules
  : ruleList EOF
  ;

ruleList
  : ruleCollection*
  ;

ruleCollection
  : qualifiedName LBRACE metaDeclaration? ruleDeclaration* RBRACE
  ;

ruleDeclaration
  : (entryDeclaration | sinkDeclaration)
  ;

entryDeclaration
  : ENTRY AT? qualifiedName (LPAREN argList* RPAREN) entryBody?
  ;

metaDeclaration
  : META LBRACE keyValueDeclaration* RBRACE
  ;

keyValueDeclaration
  : key COLON value SEMI
  ;

entryBody
  : LBRACE accessDeclaration* RBRACE
  ;

accessDeclaration
  : ACCESS LBRACK stringList RBRACK SEMI
  ;

sinkDeclaration
  : SINK qualifiedName (LPAREN argList* RPAREN) sinkBody?
  ;

sinkBody
  : LBRACE sinkDefinition* RBRACE
  ;

sinkDefinition
  : (argDefinition)+
  ;

argDefinition
  : ARG LPAREN argIndex RPAREN
    LBRACE
    (
      matchDefinition
      | expressionDefinition
      | taintedDefinition
      | literalDefinition
    )+ RBRACE
  ;

argIndex
  : NUMBER
  ;

matchDefinition
  : EQ STRING SEMI
  ;

expressionDefinition
  : EXPR STRING SEMI
  ;

taintedDefinition
  : TAINTED LBRACK taintConditionList RBRACK SEMI
  ;

taintConditionList
  : taintCondition (COMMA taintCondition)*
  ;

taintCondition
  : EXCL? identifier
  ;

literalDefinition
  : EXCL? LITERAL SEMI
  ;

stringList
  : identifier (COMMA identifier)*
  ;

argList
  : paramType (COMMA paramType)*
  ;

paramType
  : qualifiedName ARRAY?
  ;

qualifiedName
  : identifier (DOT identifier)*
  ;

identifier
  : IDENTIFIER
  ;

key
  : STRING
  ;

value
  : STRING
  ;