package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
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
public class LocalVariableDeclarationStatement extends Statement {
  private String type;
  private List<VariableDeclaration> variableDeclarations = new ArrayList<>();

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitLocalVariableDeclarationStatement(this);
  }
}
