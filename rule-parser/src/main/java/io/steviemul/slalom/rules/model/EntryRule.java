package io.steviemul.slalom.rules.model;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class EntryRule extends Rule {
  private List<Argument> arguments;
}
