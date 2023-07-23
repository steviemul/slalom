package io.steviemul.slalom.model.java;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class FieldDeclaration extends Declaration {
  private List<VariableDeclaration> variableDeclarations = new ArrayList<>();
}
