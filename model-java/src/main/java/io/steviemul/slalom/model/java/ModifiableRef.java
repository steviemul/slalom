package io.steviemul.slalom.model.java;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class ModifiableRef extends Ref {
  private Set<String> modifiers = new LinkedHashSet<>();
  private List<AnnotationDeclaration> annotations = new ArrayList<>();
}
