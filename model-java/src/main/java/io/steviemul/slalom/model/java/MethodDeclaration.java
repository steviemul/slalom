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
public class MethodDeclaration extends Declaration {
  private List<VariableDeclaration> parameters = new ArrayList<>();
  private BlockStatement block = new BlockStatement();
}
