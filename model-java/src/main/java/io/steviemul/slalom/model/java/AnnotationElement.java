package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class AnnotationElement extends Declaration {
  private IdentifierExpression identifier;
  private List<Expression> values;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitAnnotationElement(this);
  }
}
