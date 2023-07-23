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
public class CompilationUnit extends Ref {
  private PackageDeclaration packageDeclaration;
  private List<ImportDeclaration> importDeclarations = new ArrayList<>();
  private Declaration typeDeclaration;
}
