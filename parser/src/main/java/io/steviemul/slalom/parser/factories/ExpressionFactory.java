package io.steviemul.slalom.parser.factories;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.CreatorExpression;
import io.steviemul.slalom.model.java.Expression;
import io.steviemul.slalom.model.java.IdentifierExpression;
import io.steviemul.slalom.model.java.LiteralExpression;
import io.steviemul.slalom.model.java.Operator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.steviemul.slalom.utils.ObjectUtils.isDefined;

public class ExpressionFactory {

  public static Expression fromContext(JavaParser.ParExpressionContext ctx) {
    Expression expression = new Expression();

    expression.expression(fromContext(ctx.expression()));

    return expression;
  }

  public static Expression fromContext(JavaParser.ExpressionContext ctx) {

    Expression expression = new Expression();

    if (ctx.primary() != null) {
      expression = fromContext(ctx.primary());
    }
    else if (ctx.identifier() != null) {
      expression = fromContext(ctx.identifier());
    }
    else if (ctx.creator() != null) {
      expression = fromContext(ctx.creator());
    }

    if (ctx.expression() != null) {
      if (ctx.bop != null) {
        expression.operator(Operator.fromToken(ctx.bop.getText()));
      }
      expression.expressions(ctx.expression().stream()
          .map(ExpressionFactory::fromContext).collect(Collectors.toList()));
    }

    expression.position(ctx.start.getLine(), ctx.start.getCharPositionInLine());

    return expression;
  }

  public static Expression fromContext(JavaParser.CreatorContext ctx) {
    CreatorExpression creatorExpression = new CreatorExpression();

    creatorExpression.type(fromContext(ctx.createdName().identifier(0)));

    if (isDefined(ctx.classCreatorRest())) {
      creatorExpression.expressions(fromContext(ctx.classCreatorRest().arguments()));
    }

    return creatorExpression;
  }

  public static List<Expression> fromContext(JavaParser.ArgumentsContext ctx) {

    if (ctx.expressionList() != null && ctx.expressionList().expression() != null) {
      return ctx.expressionList().expression()
          .stream().map(ExpressionFactory::fromContext)
          .collect(Collectors.toList());
    }

    return new ArrayList<>();
  }

  public static Expression fromContext(JavaParser.PrimaryContext ctx) {

    Expression expression = new Expression();

    if (ctx.literal() != null) {
      expression = fromContext(ctx.literal());
    }
    else if (ctx.identifier() != null) {
      expression = fromContext(ctx.identifier());
    }

    return expression;
  }

  public static LiteralExpression fromContext(JavaParser.LiteralContext ctx) {
    LiteralExpression expression = new LiteralExpression();

    expression.value(ctx.getText());

    return expression;
  }

  public static IdentifierExpression fromContext(JavaParser.IdentifierContext ctx) {
    IdentifierExpression expression = new IdentifierExpression();

    expression.name(ctx.getText());

    return expression;
  }
}
