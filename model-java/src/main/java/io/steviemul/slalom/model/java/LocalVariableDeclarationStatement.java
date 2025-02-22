package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class LocalVariableDeclarationStatement extends Statement {
  private String type;
  private List<VariableDeclaration> variableDeclarations = new ArrayList<>();
  private Expression initializer;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitLocalVariableDeclarationStatement(this);
  }
}
