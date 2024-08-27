package io.steviemul.slalom.parser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParserContext implements AutoCloseable {

  private static ThreadLocal<ParserContext> localParserContext = new ThreadLocal<>();

  private String filename;

  public static ParserContext currentContext() {
    if (localParserContext.get() == null) {
      localParserContext.set(new ParserContext());
    }

    return localParserContext.get();
  }

  @Override
  public void close() {
    localParserContext.set(new ParserContext());
  }
}
