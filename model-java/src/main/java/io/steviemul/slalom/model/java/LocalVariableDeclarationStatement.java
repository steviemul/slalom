package io.steviemul.slalom.model.java;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class LocalVariableDeclarationStatement extends Statement {
  private FieldDeclaration fieldDeclaration = new FieldDeclaration();
}
