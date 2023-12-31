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
public class ClassDeclaration extends Declaration {
  private List<Declaration> memberDeclarations = new ArrayList<>();
  private String parentClass;
  private List<String> interfaces = new ArrayList<>();

  @Override
  public void accept(RefVisitor visitor) {
    visitor.visitClassDeclaration(this);
  }
}
