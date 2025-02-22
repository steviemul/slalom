package io.steviemul.slalom.rules.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Rule {
  private String name;
}
