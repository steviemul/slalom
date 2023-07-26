package io.steviemul.slalom.model.java;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class BinaryExpression extends Expression {
  private Operator operator;
  private Expression left;
  private Expression right;
}
