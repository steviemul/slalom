package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class LambdaExpression extends Expression {

  private IdentifierExpression identifier;
  private List<Expression> parameters = new ArrayList<>();
  private BlockStatement block = new BlockStatement();
}
