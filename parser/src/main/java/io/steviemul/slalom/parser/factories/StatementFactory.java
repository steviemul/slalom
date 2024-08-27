package io.steviemul.slalom.parser.factories;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.BlockStatement;
import io.steviemul.slalom.model.java.EnhancedForStatement;
import io.steviemul.slalom.model.java.ForStatement;
import io.steviemul.slalom.model.java.StandardForStatement;
import io.steviemul.slalom.model.java.StatementExpression;
import io.steviemul.slalom.model.java.IfStatement;
import io.steviemul.slalom.model.java.LocalVariableDeclarationStatement;
import io.steviemul.slalom.model.java.ReturnStatement;
import io.steviemul.slalom.model.java.Statement;
import io.steviemul.slalom.model.java.VariableDeclaration;
import io.steviemul.slalom.model.java.WhileStatement;
import io.steviemul.slalom.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

import static io.steviemul.slalom.utils.ContextUtils.logUnhandled;

@Slf4j
public class StatementFactory {

  private static final String VAR_DECL = "VAR";

  public static BlockStatement fromContext(JavaParser.BlockContext ctx) {

    BlockStatement blockStatement = new BlockStatement();

    blockStatement.statements(
        ctx.blockStatement().stream()
            .map(StatementFactory::fromContext)
            .collect(Collectors.toList()));

    blockStatement.position(ctx.start.getLine(), ctx.start.getCharPositionInLine());

    return blockStatement;
  }

  public static Statement fromContext(JavaParser.BlockStatementContext ctx) {

    Statement statement = new Statement();

    if (ctx.localVariableDeclaration() != null) {
      statement = fromContext(ctx.localVariableDeclaration());
    } else if (ctx.statement() != null) {
      statement = fromContext(ctx.statement());
    }

    statement.position(ctx.start.getLine(), ctx.start.getCharPositionInLine());

    return statement;
  }

  public static LocalVariableDeclarationStatement fromContext(
      JavaParser.LocalVariableDeclarationContext ctx) {
    LocalVariableDeclarationStatement localVariableDeclarationStatement =
        new LocalVariableDeclarationStatement();

    if (ctx.VAR() != null) {
      localVariableDeclarationStatement.type(VAR_DECL);
    } else if (ctx.typeType() != null) {
      localVariableDeclarationStatement.type(ctx.typeType().getText());
    }

    if (ctx.expression() != null) {
      localVariableDeclarationStatement.initializer(ExpressionFactory.fromContext(ctx.expression()));
    } else if (ctx.variableDeclarators() != null) {
      localVariableDeclarationStatement.variableDeclarations(
          ctx.variableDeclarators().variableDeclarator().stream()
              .map(DeclarationFactory::fromContext)
              .collect(Collectors.toList()));
    }

    return localVariableDeclarationStatement;
  }

  public static Statement fromContext(JavaParser.StatementContext ctx) {

    Statement statement = new Statement();

    if (ctx.block() != null) {
      return blockStatement(ctx);
    } else if (ctx.IF() != null) {
      return ifStatement(ctx);
    } else if (ctx.WHILE() != null) {
      return whileStatement(ctx);
    } else if (ctx.FOR() != null) {
      return forStatement(ctx);
    } else if (ctx.RETURN() != null) {
      return returnStatement(ctx);
    } else if (ctx.statementExpression != null) {
      return fromContext(ctx.statementExpression);
    }

    logUnhandled(ctx);

    return statement;
  }

  public static StatementExpression fromContext(JavaParser.ExpressionContext ctx) {
    StatementExpression statementExpression = new StatementExpression();

    statementExpression.expression(ExpressionFactory.fromContext(ctx));

    return statementExpression;
  }

  public static BlockStatement blockStatement(JavaParser.StatementContext ctx) {
    BlockStatement blockStatement = new BlockStatement();

    ctx.block().blockStatement().forEach(blockStatementContext -> {
      blockStatement.statements().add(fromContext(blockStatementContext));
    });

    blockStatement.position(ctx);
    return blockStatement;
  }

  public static IfStatement ifStatement(JavaParser.StatementContext ctx) {
    IfStatement ifStatement = new IfStatement();

    ifStatement.expression(ExpressionFactory.fromContext(ctx.parExpression()));
    ifStatement.thenStatement(fromContext(ctx.statement(0)));

    if (ctx.ELSE() != null) {
      ifStatement.elseStatement(fromContext(ctx.statement(1)));
    }

    ifStatement.position(ctx);
    return ifStatement;
  }

  public static WhileStatement whileStatement(JavaParser.StatementContext ctx) {
    WhileStatement whileStatement = new WhileStatement();

    whileStatement.expression(ExpressionFactory.fromContext(ctx.parExpression()));
    whileStatement.statement(fromContext(ctx.statement(0)));
    whileStatement.position(ctx);

    return whileStatement;
  }

  public static ReturnStatement returnStatement(JavaParser.StatementContext ctx) {
    ReturnStatement returnStatement = new ReturnStatement();

    if (CollectionUtils.hasLength(ctx.expression())) {
      returnStatement.expression(ExpressionFactory.fromContext(ctx.expression(0)));
    }

    returnStatement.position(ctx);
    return returnStatement;
  }

  public static ForStatement forStatement(JavaParser.StatementContext ctx) {
    ForStatement forStatement = ctx.forControl().enhancedForControl() != null
        ? enhancedForStatement(ctx.forControl().enhancedForControl())
        : standardForStatement(ctx.forControl());

    forStatement.statement(fromContext(ctx.statement(0)));
    forStatement.position(ctx);

    return forStatement;
  }

  private static ForStatement standardForStatement(JavaParser.ForControlContext ctx) {
    StandardForStatement forStatement = new StandardForStatement();

    if (ctx.forInit().localVariableDeclaration() != null) {
      forStatement.localVariableDeclaration(
          StatementFactory.fromContext(ctx.forInit().localVariableDeclaration()));
    }


    if (ctx.forInit().expressionList() != null) {
      forStatement.initExpressions(
          ctx.forInit().expressionList().expression().stream()
              .map(ExpressionFactory::fromContext)
              .collect(Collectors.toList()));
    }

    forStatement.expression(ExpressionFactory.fromContext(ctx.expression()));

    if (ctx.forUpdate != null) {
      forStatement.updateExpressions(ctx.forUpdate.expression().stream()
          .map(ExpressionFactory::fromContext)
          .collect(Collectors.toList()));
    }

    return forStatement;
  }

  private static ForStatement enhancedForStatement(JavaParser.EnhancedForControlContext ctx) {
    EnhancedForStatement forStatement = new EnhancedForStatement();

    VariableDeclaration variableDeclaration = new VariableDeclaration();

    variableDeclaration.name(ctx.variableDeclaratorId().identifier().getText());

    forStatement.variableDeclaration(variableDeclaration);
    forStatement.expression(ExpressionFactory.fromContext(ctx.expression()));

    return forStatement;
  }
}
