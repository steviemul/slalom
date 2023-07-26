package io.steviemul.slalom.model.java;

public enum Operator {
  DOT,
  EQUAL,
  GREATER,
  UNKNOWN;

  public static Operator fromToken(String token) {
    switch (token) {
      case ".":
        return DOT;
      case "=":
        return EQUAL;
      case ">":
        return GREATER;
      default:
        return UNKNOWN;
    }
  }
}
