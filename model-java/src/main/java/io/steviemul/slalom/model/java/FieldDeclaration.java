package io.steviemul.slalom.model.java;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class FieldDeclaration extends Declaration {
  private List<VariableDeclaration> variableDeclarations = new ArrayList<>();

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitFieldDeclaration(this);
  }
}
