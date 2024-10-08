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
public class ASTRoot extends Ref {
  private String path;
  private String sha;
  private PackageDeclaration packageDeclaration;
  private List<ImportDeclaration> importDeclarations = new ArrayList<>();
  private Declaration typeDeclaration;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitASTRoot(this);
  }
}
