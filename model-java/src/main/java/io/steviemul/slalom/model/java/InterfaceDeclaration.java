package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class InterfaceDeclaration extends Declaration {
  private List<String> parents = new ArrayList<>();
  private List<Declaration> memberDeclarations = new ArrayList<>();

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitInterfaceDeclaration(this);
  }
}
