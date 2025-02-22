package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class UnknownExpression extends Expression {
  private final String text;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitUnknownExpression(this);
  }
}
