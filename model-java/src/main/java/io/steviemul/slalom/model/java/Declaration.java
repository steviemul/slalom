package io.steviemul.slalom.model.java;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class Declaration extends ModifiableRef {
  public static final String UNKNOWN = "UNKNOWN";

  private String name;
  private String path;
  private String fqn;
  private String type;

  public Declaration() {
    this.name(UNKNOWN);
  }
}
