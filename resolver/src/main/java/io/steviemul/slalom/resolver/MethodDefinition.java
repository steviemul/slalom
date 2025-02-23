package io.steviemul.slalom.resolver;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter(onMethod = @__(@JsonProperty))
@Builder
public class MethodDefinition {

  private String name;
  private String returnType;
  private List<String> argumentTypes;
}
