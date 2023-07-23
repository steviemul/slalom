package io.steviemul.slalom.model.java;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public abstract class Ref {
  private int line;
  private int column;

  public void position(int line, int column) {
    this.line = line;
    this.column = column;
  }
}
