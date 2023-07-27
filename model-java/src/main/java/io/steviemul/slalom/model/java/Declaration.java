package io.steviemul.slalom.model.java;

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
public class Declaration extends ModifiableRef {
  private String name;
  private String path;
  private String fqn;
  private String type;
}
