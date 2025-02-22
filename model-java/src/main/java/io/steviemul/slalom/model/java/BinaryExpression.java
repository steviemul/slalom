package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class BinaryExpression extends Expression {
  private Operator operator;
  private Expression left;
  private Expression right;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitBinaryExpression(this);
  }
}
