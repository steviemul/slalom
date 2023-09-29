package io.steviemul.slalom.parser.factories;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.BlockStatement;
import io.steviemul.slalom.model.java.ForStatement;
import io.steviemul.slalom.model.java.StatementExpression;
import io.steviemul.slalom.model.java.IfStatement;
import io.steviemul.slalom.model.java.LocalVariableDeclarationStatement;
import io.steviemul.slalom.model.java.ReturnStatement;
import io.steviemul.slalom.model.java.Statement;
import io.steviemul.slalom.model.java.WhileStatement;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
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

    log.warn("Found unhandled statement [{}]", ctx.getText());

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

    returnStatement.expression(ExpressionFactory.fromContext(ctx.expression(0)));

    returnStatement.position(ctx);
    return returnStatement;
  }

  public static ForStatement forStatement(JavaParser.StatementContext ctx) {
    ForStatement forStatement = new ForStatement();

    forStatement.statement(fromContext(ctx.statement(0)));
    forStatement.position(ctx);

    return forStatement;
  }

  
}
