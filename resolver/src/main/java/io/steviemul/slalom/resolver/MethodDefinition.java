package io.steviemul.slalom.resolver;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter(onMethod = @__(@JsonProperty))
@Builder
public class MethodDefinition {

  private String name;
  private String returnType;
  private List<String> argumentTypes;
}
