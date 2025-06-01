package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class LambdaExpression extends Expression {

  private List<Expression> parameters = new ArrayList<>();
  private Expression expression = new Expression();
  private BlockStatement block = new BlockStatement();
}
