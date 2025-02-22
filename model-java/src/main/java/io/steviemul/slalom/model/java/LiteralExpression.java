package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class LiteralExpression extends Expression {
  private Object value;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitLiteralExpression(this);
  }
}
