package io.steviemul.slalom.model.java;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class MethodCallExpression extends Expression {
  private IdentifierExpression method;
  private List<Expression> parameters = new ArrayList<>();
}
