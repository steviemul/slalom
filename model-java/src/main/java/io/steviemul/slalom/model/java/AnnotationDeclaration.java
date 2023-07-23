package io.steviemul.slalom.model.java;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class AnnotationDeclaration extends Ref {
  private Map<String, String> properties = new HashMap<>();
}
