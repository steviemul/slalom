package io.steviemul.slalom.parser.factories;

import static io.steviemul.slalom.constants.ParserConstants.SUPER;
import static io.steviemul.slalom.constants.ParserConstants.THIS;
import static io.steviemul.slalom.utils.ContextUtils.logUnhandled;
import static io.steviemul.slalom.utils.ObjectUtils.isDefined;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.ArrayExpression;
import io.steviemul.slalom.model.java.BinaryExpression;
import io.steviemul.slalom.model.java.CreatorExpression;
import io.steviemul.slalom.model.java.DotExpression;
import io.steviemul.slalom.model.java.Expression;
import io.steviemul.slalom.model.java.IdentifierExpression;
import io.steviemul.slalom.model.java.LambdaExpression;
import io.steviemul.slalom.model.java.LiteralExpression;
import io.steviemul.slalom.model.java.MethodCallExpression;
import io.steviemul.slalom.model.java.Operator;
import io.steviemul.slalom.model.java.ParExpression;
import io.steviemul.slalom.model.java.TernaryExpression;
import io.steviemul.slalom.utils.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpressionFactory {

  public static Expression fromContext(JavaParser.ParExpressionContext ctx) {
    ParExpression expression = new ParExpression();

    expression.expression(fromContext(ctx.expression()));

    return expression;
  }

  public static Expression fromContext(JavaParser.ExpressionContext ctx, Operator op) {

    List<JavaParser.ExpressionContext> expressions = ctx.expression();

    switch (op) {
      case ARRAY:
        ArrayExpression arrayExpression = new ArrayExpression();

        arrayExpression.identifier(fromContext(expressions.get(0)));
        arrayExpression.index(fromContext(expressions.get(1)));

        arrayExpression.position(ctx);
        return arrayExpression;
      case DOT:
        if (expressions.size() == 1) {
          DotExpression dotExpression = new DotExpression();
          dotExpression.identifier(fromContext(expressions.get(0)));
          dotExpression.reference(referenceExpressionFromContext(ctx));
          dotExpression.position(ctx);
          return dotExpression;
        }
      case TERNARY:
        TernaryExpression ternaryExpression = new TernaryExpression();

        ternaryExpression.condition(fromContext(expressions.get(0)));
        ternaryExpression.thenValue(fromContext(expressions.get(1)));
        ternaryExpression.elseValue(fromContext(expressions.get(2)));

        return ternaryExpression;
      default:
        if (expressions.size() == 2) {
          BinaryExpression binaryExpression = new BinaryExpression();
          binaryExpression.operator(op);
          binaryExpression.left(fromContext(expressions.get(0)));
          binaryExpression.right(fromContext(expressions.get(1)));
          binaryExpression.position(ctx);
          return binaryExpression;
        }
    }

    logUnhandled(ctx);

    return new Expression();
  }

  public static Expression fromContext(JavaParser.ExpressionContext ctx) {

    Expression expression = new Expression();

    if (ctx.primary() != null) {
      expression = fromContext(ctx.primary());
    } else if (isArray(ctx)) {
      expression = fromContext(ctx, Operator.ARRAY);
    } else if (isReference(ctx)) {
      expression = fromContext(ctx, Operator.DOT);
    } else if (ctx.identifier() != null) {
      expression = fromContext(ctx.identifier());
    } else if (ctx.methodCall() != null) {
      expression = fromContext(ctx.methodCall());
    } else if (ctx.THIS() != null) {
      expression = new IdentifierExpression().name(THIS);
    } else if (ctx.SUPER() != null) {
      expression = new IdentifierExpression().name(SUPER);
    } else if (ctx.creator() != null) {
      expression = fromContext(ctx.creator());
    } else if (isBinaryExpression(ctx)) {
      expression = fromContext(ctx, Operator.fromToken(ctx.bop.getText()));
    } else if (ctx.lambdaExpression() != null) {
      expression = fromContext(ctx.lambdaExpression());
    } else if (isTernaryExpression(ctx)) {
      expression = fromContext(ctx, Operator.fromToken(ctx.bop.getText()));
    } else {
      logUnhandled(ctx);
    }

    expression.position(ctx);

    return expression;
  }

  public static Expression fromContext(JavaParser.LambdaExpressionContext ctx) {
    LambdaExpression lambdaExpression = new LambdaExpression();

    if (ctx.lambdaBody().expression() != null) {
      lambdaExpression.expression(ExpressionFactory.fromContext(ctx.lambdaBody().expression()));
    } else if (ctx.lambdaBody().block() != null) {
      lambdaExpression.block(StatementFactory.fromContext(ctx.lambdaBody().block()));
    }

    logUnhandled(ctx);

    return lambdaExpression;
  }

  public static Expression referenceExpressionFromContext(JavaParser.ExpressionContext ctx) {

    if (ctx.identifier() != null) {
      return fromContext(ctx.identifier());
    } else if (ctx.methodCall() != null) {
      return fromContext(ctx.methodCall());
    } else if (ctx.THIS() != null) {
      return identifierExpression(THIS, ctx);
    } else if (ctx.SUPER() != null) {
      return identifierExpression(SUPER, ctx);
    }

    return null;
  }

  public static IdentifierExpression identifierExpression(
      String name, JavaParser.ExpressionContext ctx) {
    IdentifierExpression expression = new IdentifierExpression();
    expression.name(name);
    expression.position(ctx);
    return expression;
  }

  public static MethodCallExpression fromContext(JavaParser.MethodCallContext ctx) {
    MethodCallExpression methodCallExpression = new MethodCallExpression();

    if (ctx.identifier() != null) {
      methodCallExpression.method(fromContext(ctx.identifier()));
    } else if (ctx.THIS() != null) {
      methodCallExpression.method(getSpecialMethod(ctx, ctx.THIS().getText()));
    } else if (ctx.SUPER() != null) {
      methodCallExpression.method(getSpecialMethod(ctx, ctx.SUPER().getText()));
    }

    if (ctx.expressionList() != null) {
      methodCallExpression.parameters(
          ctx.expressionList().expression().stream()
              .map(ExpressionFactory::fromContext)
              .collect(Collectors.toList()));
    }

    methodCallExpression.position(ctx);
    return methodCallExpression;
  }

  public static Expression fromContext(JavaParser.CreatorContext ctx) {
    CreatorExpression creatorExpression = new CreatorExpression();

    if (CollectionUtils.hasLength(ctx.createdName().identifier())) {
      creatorExpression.type(fromContext(ctx.createdName().identifier(0)));
    } else {
      logUnhandled(ctx);
    }

    if (isDefined(ctx.classCreatorRest())) {
      creatorExpression.parameters(fromContext(ctx.classCreatorRest().arguments()));
    }

    creatorExpression.position(ctx);
    return creatorExpression;
  }

  public static List<Expression> fromContext(JavaParser.ArgumentsContext ctx) {

    if (ctx.expressionList() != null && ctx.expressionList().expression() != null) {
      return ctx.expressionList().expression().stream()
          .map(ExpressionFactory::fromContext)
          .collect(Collectors.toList());
    }

    return new ArrayList<>();
  }

  public static Expression fromContext(JavaParser.PrimaryContext ctx) {

    Expression expression = new Expression();

    if (ctx.THIS() != null) {
      expression = new IdentifierExpression().name(THIS);
    } else if (ctx.SUPER() != null) {
      expression = new IdentifierExpression().name(SUPER);
    } else if (ctx.expression() != null) {
      expression = fromContext(ctx.expression());
    } else if (ctx.literal() != null) {
      expression = fromContext(ctx.literal());
    } else if (ctx.identifier() != null) {
      expression = fromContext(ctx.identifier());
    }

    expression.position(ctx);

    return expression;
  }

  public static LiteralExpression fromContext(JavaParser.LiteralContext ctx) {
    LiteralExpression expression = new LiteralExpression();

    expression.value(ctx.getText());

    expression.position(ctx);
    return expression;
  }

  public static IdentifierExpression fromContext(JavaParser.IdentifierContext ctx) {
    IdentifierExpression expression = new IdentifierExpression();

    expression.name(ctx.getText());

    expression.position(ctx);
    return expression;
  }

  private static boolean isArray(JavaParser.ExpressionContext ctx) {
    return ctx.LBRACK() != null && ctx.RBRACK() != null && ctx.expression().size() == 2;
  }

  private static boolean isReference(JavaParser.ExpressionContext ctx) {
    return ctx.bop != null && ctx.bop.getText().equals(".") && ctx.expression().size() == 1;
  }

  private static boolean isBinaryExpression(JavaParser.ExpressionContext ctx) {
    return ctx.bop != null && !ctx.bop.getText().equals(".") && ctx.expression().size() == 2;
  }

  private static boolean isTernaryExpression(JavaParser.ExpressionContext ctx) {
    return ctx.bop != null && ctx.bop.getText().equals("?") && ctx.expression().size() == 3;
  }

  private static IdentifierExpression getSpecialMethod(
      JavaParser.MethodCallContext ctx, String methodName) {
    IdentifierExpression identifierExpression = new IdentifierExpression().name(methodName);

    identifierExpression.position(ctx);

    return identifierExpression;
  }
}
