package io.steviemul.slalom.rules.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
public class EntryRule extends Rule {
  private List<Argument> arguments;
}
