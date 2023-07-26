package io.steviemul.slalom.model.java;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@ToString
public class FieldDeclaration extends Declaration {
  private List<VariableDeclaration> variableDeclarations = new ArrayList<>();
}