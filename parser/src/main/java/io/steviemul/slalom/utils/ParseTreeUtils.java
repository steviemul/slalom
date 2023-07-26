package io.steviemul.slalom.utils;

import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParseTreeUtils {

  public static boolean inArray(ParserRuleContext ctx) {

    boolean leftBracket = getLeftSibling(ctx).map(s -> s.getText().equals("[")).orElse(false);

    boolean rightBracket = getRightSibling(ctx).map(s -> s.getText().equals("]")).orElse(false);

    return leftBracket && rightBracket;
  }

  public static Optional<ParseTree> getSibling(ParserRuleContext ctx, int offset) {

    if (ctx.getParent() != null) {
      int ruleIndex = ctx.getParent().children.indexOf(ctx);
      int siblingIndex = ruleIndex + +offset;

      if (siblingIndex > -1 && siblingIndex < ctx.getParent().children.size()) {
        return Optional.of(ctx.getParent().getChild(ParseTree.class, siblingIndex));
      }
    }

    return Optional.empty();
  }

  public static Optional<ParseTree> getLeftSibling(ParserRuleContext ctx) {
    return getSibling(ctx, -1);
  }

  public static Optional<ParseTree> getRightSibling(ParserRuleContext ctx) {
    return getSibling(ctx, 1);
  }

  public static <T> Optional<T> getLeftSibling(ParserRuleContext ctx, Class<T> expectedType) {

    return getLeftSibling(ctx)
        .filter(rc -> expectedType.isAssignableFrom(rc.getClass()))
        .map(expectedType::cast)
        .stream()
        .findFirst();
  }

  public static <T> Optional<T> getRightSibling(ParserRuleContext ctx, Class<T> expectedType) {

    return getRightSibling(ctx)
        .filter(rc -> expectedType.isAssignableFrom(rc.getClass()))
        .map(expectedType::cast)
        .stream()
        .findFirst();
  }

  private ParseTreeUtils() {}
  ;
}
