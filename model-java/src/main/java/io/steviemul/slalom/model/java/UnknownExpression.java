package io.steviemul.slalom.model.java;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
@RequiredArgsConstructor
public class UnknownExpression extends Expression {
  private final String text;
}
