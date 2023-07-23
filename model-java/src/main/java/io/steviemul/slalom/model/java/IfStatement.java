package io.steviemul.slalom.model.java;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class IfStatement extends Statement {

  private String expression;
  private String thenStatement;
  private String elseStatement;
}
