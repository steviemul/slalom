package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ASTRoot extends Ref {
  private String path;
  private PackageDeclaration packageDeclaration;
  private List<ImportDeclaration> importDeclarations = new ArrayList<>();
  private Declaration typeDeclaration;
}