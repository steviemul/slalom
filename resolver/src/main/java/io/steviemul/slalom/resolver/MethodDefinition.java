package io.steviemul.slalom.resolver;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MethodDefinition {

  private String name;
  private String returnType;
  private List<String> argumentTypes;
}
