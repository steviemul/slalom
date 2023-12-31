package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UnknownExpression extends Expression {
  private final String text;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitUnknownExpression(this);
  }
}
