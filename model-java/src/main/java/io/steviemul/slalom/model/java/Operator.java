package io.steviemul.slalom.model.java;

public enum Operator {
  ADD,
  ARRAY,
  ASSIGN,
  DIV,
  DOT,
  EQUAL,
  GREATER,
  LESS,
  MINUS,
  MOD,
  MUL,
  UNKNOWN;

  public static Operator fromToken(String token) {
    switch (token) {
      case "+":
        return ADD;
      case "[]":
        return ARRAY;
      case "=":
        return ASSIGN;
      case "/":
        return DIV;
      case ".":
        return DOT;
      case "==":
        return EQUAL;
      case ">":
        return GREATER;
      case "<":
        return LESS;
      case "-":
        return MINUS;
      case "%":
        return MOD;
      case "*":
        return MUL;
      default:
        return UNKNOWN;
    }
  }
}
