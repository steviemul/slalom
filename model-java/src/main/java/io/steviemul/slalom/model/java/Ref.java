package io.steviemul.slalom.model.java;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.steviemul.slalom.model.java.visitor.RefVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.ParserRuleContext;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public abstract class Ref {
  private int line;
  private int column;

  public void position(ParserRuleContext ctx) {
    this.position(ctx.start.getLine(), ctx.start.getCharPositionInLine());
  }

  public void position(int line, int column) {
    this.line = line;
    this.column = column;
  }

  public abstract void accept(RefVisitor visitor);
}
