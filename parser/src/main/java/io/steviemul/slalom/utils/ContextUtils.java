package io.steviemul.slalom.utils;

import io.steviemul.slalom.parser.ParserContext;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;

@Slf4j
public class ContextUtils {

  private ContextUtils() {}

  public static void logUnhandled(ParserRuleContext ctx) {
    ParserContext parserContext = ParserContext.currentContext();

    log.warn(
        "Found unhandled ParserRuleContext [{}, {}, {}, {}]",
        parserContext.getFilename(),
        ctx.getText(),
        ctx.start.getLine(),
        ctx.start.getCharPositionInLine());
  }
}
