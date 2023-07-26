package io.steviemul.slalom.parser.factories;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.BlockStatement;
import io.steviemul.slalom.model.java.ExpressionStatement;
import io.steviemul.slalom.model.java.IfStatement;
import io.steviemul.slalom.model.java.LocalVariableDeclarationStatement;
import io.steviemul.slalom.model.java.MethodCallStatement;
import io.steviemul.slalom.model.java.Operator;
import io.steviemul.slalom.model.java.ReturnStatement;
import io.steviemul.slalom.model.java.Statement;

import java.util.stream.Collectors;

import static io.steviemul.slalom.utils.ObjectUtils.isDefined;

public class StatementFactory {

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

    localVariableDeclarationStatement.type(ctx.typeType().getText());

    localVariableDeclarationStatement.variableDeclarations(
        ctx.variableDeclarators().variableDeclarator().stream()
            .map(DeclarationFactory::fromContext)
            .collect(Collectors.toList()));

    return localVariableDeclarationStatement;
  }

  public static Statement fromContext(JavaParser.StatementContext ctx) {

    Statement statement = new Statement();

    if (ctx.block() != null) {
      BlockStatement blockStatement = new BlockStatement();

      for (JavaParser.BlockStatementContext blockStatementContext : ctx.block().blockStatement()) {
        blockStatement.statements().add(fromContext(blockStatementContext));
      }

      return blockStatement;
    } else if (ctx.IF() != null) {
      IfStatement ifStatement = new IfStatement();

      ifStatement.expression(ExpressionFactory.fromContext(ctx.parExpression()));
      ifStatement.thenStatement(fromContext(ctx.statement(0)));

      if (ctx.ELSE() != null) {
        ifStatement.elseStatement(fromContext(ctx.statement(1)));
      }

      return ifStatement;
    } else if (ctx.RETURN() != null) {
      ReturnStatement returnStatement = new ReturnStatement();

      returnStatement.expression(ExpressionFactory.fromContext(ctx.expression(0)));

      return returnStatement;
    } else if (ctx.statementExpression != null) {
      return fromContext(ctx.statementExpression);
    }

    return statement;
  }

  public static ExpressionStatement fromContext(JavaParser.ExpressionContext ctx) {
    ExpressionStatement expressionStatement = new ExpressionStatement();

    if (isDefined(ctx.methodCall())) {
      expressionStatement = fromContext(ctx.methodCall());
    }

    if (ctx.bop != null) {
      expressionStatement.operator(Operator.fromToken(ctx.bop.getText()));
    }

    expressionStatement.expressions(
        ctx.expression().stream().map(ExpressionFactory::fromContext).collect(Collectors.toList()));

    return expressionStatement;
  }

  public static MethodCallStatement fromContext(JavaParser.MethodCallContext ctx) {
    MethodCallStatement methodCallStatement = new MethodCallStatement();

    methodCallStatement.method(ExpressionFactory.fromContext(ctx.identifier()));
    methodCallStatement.parameters(
        ctx.expressionList().expression().stream()
            .map(ExpressionFactory::fromContext)
            .collect(Collectors.toList()));

    return methodCallStatement;
  }
}
