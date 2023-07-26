package io.steviemul.slalom.model.java;

public enum Operator {
  ADD,
  ARRAY,
  DIV,
  DOT,
  EQUAL,
  GREATER,
  MINUS,
  MUL,
  UNKNOWN;

  public static Operator fromToken(String token) {
    switch (token) {
      case "+":
        return ADD;
      case "[]":
        return ARRAY;
      case "/":
        return DIV;
      case ".":
        return DOT;
      case "=":
        return EQUAL;
      case ">":
        return GREATER;
      case "-":
        return MINUS;
      case "*":
        return MUL;
      default:
        return UNKNOWN;
    }
  }
}
