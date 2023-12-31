package io.steviemul.slalom.model.java;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
public abstract class ModifiableRef extends Ref {
  private Set<String> modifiers = new LinkedHashSet<>();
  private List<AnnotationDeclaration> annotations = new ArrayList<>();
}
