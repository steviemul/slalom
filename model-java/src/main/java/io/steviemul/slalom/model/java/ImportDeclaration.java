package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
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
public class ImportDeclaration extends Ref {

  private String name;
  private boolean wildcard;
  private boolean staticImport;

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitImportDeclaration(this);
  }
}
