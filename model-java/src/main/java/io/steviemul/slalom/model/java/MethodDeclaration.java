package io.steviemul.slalom.model.java;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class MethodDeclaration extends Declaration {
  private List<VariableDeclaration> parameters = new ArrayList<>();
  private BlockStatement block = new BlockStatement();
}
