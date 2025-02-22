package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class ParExpression extends Expression {
  private Expression expression;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitParExpression(this);
  }
}
