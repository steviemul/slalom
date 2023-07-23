package io.steviemul.slalom.model.java;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class ClassDeclaration extends Declaration {
  private List<Declaration> memberDeclarations = new ArrayList<>();
  private String parentClass;
  private List<String> interfaces = new ArrayList<>();
}
