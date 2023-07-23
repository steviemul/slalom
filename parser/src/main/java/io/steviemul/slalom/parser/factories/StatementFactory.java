package io.steviemul.slalom.parser.factories;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.IfStatement;
import io.steviemul.slalom.model.java.LocalVariableDeclarationStatement;
import io.steviemul.slalom.model.java.Statement;

import static io.steviemul.slalom.model.java.Declaration.UNKNOWN;

public class StatementFactory {

  public static Statement fromContext(JavaParser.BlockStatementContext ctx) {

    Statement statement = new Statement();

    if (ctx.localVariableDeclaration() != null) {
      statement = fromContext(ctx.localVariableDeclaration());
    }
    else if (ctx.statement() != null) {
      statement = fromContext(ctx.statement());
    }

    statement.position(ctx.start.getLine(), ctx.start.getCharPositionInLine());

    return statement;
  }

  public static LocalVariableDeclarationStatement fromContext(JavaParser.LocalVariableDeclarationContext ctx) {
    return new LocalVariableDeclarationStatement();
  }

  public static Statement fromContext(JavaParser.StatementContext ctx) {

    Statement statement = new Statement();

    if (ctx.IF() != null) {
      IfStatement ifStatement = new IfStatement();

      ifStatement.expression(ctx.parExpression().getText());
      ifStatement.thenStatement(ctx.statement(0).getText());

      if (ctx.ELSE() != null) {
        ifStatement.elseStatement(ctx.statement(1).getText());
      }
      statement = ifStatement;
    }

    return statement;
  }

  private StatementFactory() {}
}
