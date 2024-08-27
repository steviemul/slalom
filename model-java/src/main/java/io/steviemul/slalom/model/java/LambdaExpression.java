package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class LambdaExpression extends Expression {

  private IdentifierExpression identifier;
  private List<Expression> parameters = new ArrayList<>();
  private BlockStatement block = new BlockStatement();

}
