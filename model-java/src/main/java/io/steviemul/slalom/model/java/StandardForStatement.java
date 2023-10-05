package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class StandardForStatement extends ForStatement {
  private LocalVariableDeclarationStatement localVariableDeclaration;
  private List<Expression> initExpressions;
  private Expression expression;
  private List<Expression> updateExpressions;
}
