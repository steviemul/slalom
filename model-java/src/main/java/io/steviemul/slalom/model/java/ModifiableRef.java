package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public abstract class ModifiableRef extends Ref {
  private Set<String> modifiers = new LinkedHashSet<>();
  private List<AnnotationDeclaration> annotations = new ArrayList<>();
}
