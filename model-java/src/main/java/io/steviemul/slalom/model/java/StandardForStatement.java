package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Data
@Accessors(fluent = true)
public class StandardForStatement extends ForStatement {
  private LocalVariableDeclarationStatement localVariableDeclaration;
  private List<Expression> initExpressions;
  private Expression expression;
  private List<Expression> updateExpressions;
}
