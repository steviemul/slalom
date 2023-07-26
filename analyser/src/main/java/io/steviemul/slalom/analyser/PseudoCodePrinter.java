package io.steviemul.slalom.analyser;

import io.steviemul.slalom.model.java.BlockStatement;
import io.steviemul.slalom.model.java.ClassDeclaration;
import io.steviemul.slalom.model.java.CompilationUnit;
import io.steviemul.slalom.model.java.ConstructorDeclaration;
import io.steviemul.slalom.model.java.CreatorExpression;
import io.steviemul.slalom.model.java.Declaration;
import io.steviemul.slalom.model.java.Expression;
import io.steviemul.slalom.model.java.StatementExpression;
import io.steviemul.slalom.model.java.FieldDeclaration;
import io.steviemul.slalom.model.java.IdentifierExpression;
import io.steviemul.slalom.model.java.IfStatement;
import io.steviemul.slalom.model.java.LiteralExpression;
import io.steviemul.slalom.model.java.LocalVariableDeclarationStatement;
import io.steviemul.slalom.model.java.MethodCallExpression;
import io.steviemul.slalom.model.java.MethodDeclaration;
import io.steviemul.slalom.model.java.ParExpression;
import io.steviemul.slalom.model.java.ReturnStatement;
import io.steviemul.slalom.model.java.Statement;
import io.steviemul.slalom.model.java.StaticBlockDeclaration;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import io.steviemul.slalom.model.java.VariableDeclaration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PseudoCodePrinter {

  private final OutputStream out;
  private int indent = 0;

  public void print(CompilationUnit compilationUnit) {

    println("Path : ", compilationUnit.path());
    println("package ", compilationUnit.packageDeclaration().name());

    compilationUnit.importDeclarations().forEach(i -> println("import ", i.name()));

    printDeclaration(compilationUnit.typeDeclaration());
  }

  private void printDeclaration(Declaration declaration) {

    if (declaration instanceof ClassDeclaration) {
      printDeclaration((ClassDeclaration) declaration);
    } else if (declaration instanceof StaticBlockDeclaration) {
      printDeclaration((StaticBlockDeclaration) declaration);
    } else if (declaration instanceof FieldDeclaration) {
      printDeclaration((FieldDeclaration) declaration);
    } else if (declaration instanceof ConstructorDeclaration) {
      printDeclaration((ConstructorDeclaration) declaration);
    } else if (declaration instanceof MethodDeclaration) {
      printDeclaration((MethodDeclaration) declaration);
    }
  }

  private void printDeclaration(ConstructorDeclaration constructorDeclaration) {
    print("Constructor ", "\n");
    tab(1);
    constructorDeclaration.modifiers().forEach(m -> print(m, " "));
    print(constructorDeclaration.type(), " ");
    print("( ");
    constructorDeclaration.parameters().forEach(this::printDeclaration);
    print(" )");
    printStatement(constructorDeclaration.block());
    newline();
  }

  private void printDeclaration(MethodDeclaration methodDeclaration) {
    print("Method", "\n");
    tab(1);
    methodDeclaration.modifiers().forEach(m -> print(m, " "));
    print(methodDeclaration.type(), " ");
    print(methodDeclaration.name());
    print("( ");
    methodDeclaration.parameters().forEach(this::printDeclaration);
    print(" )");
    printStatement(methodDeclaration.block());
    newline();
  }

  private void printDeclaration(StaticBlockDeclaration staticBlockDeclaration) {
    print("Static", "\n");
    tab(1);
    printStatement(staticBlockDeclaration.block());
    newline();
  }

  private void printDeclaration(FieldDeclaration fieldDeclaration) {
    print("Field", "\n");
    tab(1);
    fieldDeclaration.modifiers().forEach(m -> print(m, " "));
    print(fieldDeclaration.type(), " ");

    fieldDeclaration
        .variableDeclarations()
        .forEach(
            v -> {
              printDeclaration(v);
              print(", ");
            });

    newline();
  }

  private void printDeclaration(ClassDeclaration classDeclaration) {

    newline();
    classDeclaration.modifiers().forEach(m -> print(m, " "));

    print(classDeclaration.name());

    if (classDeclaration.parentClass() != null) {
      print(" extends ", classDeclaration.parentClass());
    }

    if (classDeclaration.interfaces() != null & classDeclaration.interfaces().size() > 0) {
      print(" implements ");
      print(String.join(",", classDeclaration.interfaces()));
    }

    newline();

    classDeclaration.memberDeclarations().forEach(this::printDeclaration);
  }

  private void printStatement(BlockStatement block) {
    block.statements().forEach(this::printStatement);
  }

  private void printStatement(Statement statement) {

    if (statement instanceof StatementExpression) {
      printStatement((StatementExpression) statement);
    } else if (statement instanceof IfStatement) {
      printStatement((IfStatement) statement);
    } else if (statement instanceof LocalVariableDeclarationStatement) {
      printStatement((LocalVariableDeclarationStatement) statement);
    } else if (statement instanceof BlockStatement) {
      printStatement((BlockStatement) statement);
    } else if (statement instanceof ReturnStatement) {
      printStatement((ReturnStatement) statement);
    }
  }

  private void printStatement(ReturnStatement statement) {
    print("return ");
    printExpression(statement.expression());
  }

  private void printStatement(StatementExpression statement) {}

  private void printStatement(MethodCallExpression statement) {
    printExpression(statement.method());
    print("( ");
    statement.parameters().forEach(this::printExpression);
    print(" )");
    newline();
  }

  private void printStatement(IfStatement statement) {

    print("if ");
    printExpression(statement.expression());
    print(" then ");
    printStatement(statement.thenStatement());

    if (statement.elseStatement() != null) {
      print(" else ");
      printStatement(statement.elseStatement());
    }
  }

  private void printStatement(LocalVariableDeclarationStatement statement) {
    print(" ", statement.type(), " ");
    statement
        .variableDeclarations()
        .forEach(
            v -> {
              printDeclaration(v);
              print(", ");
            });

    newline();
  }

  private void printDeclaration(VariableDeclaration declaration) {
    print(declaration.name());

    if (declaration.initialValue() != null) {
      print(" = ");
      printExpression(declaration.initialValue());
    }
  }

  private void printExpression(Expression expression) {
    if (expression instanceof ParExpression) {
      printExpression(((ParExpression) expression).expression());
    } else if (expression instanceof IdentifierExpression) {
      printExpression(((IdentifierExpression) expression));
    } else if (expression instanceof LiteralExpression) {
      printExpression((LiteralExpression) expression);
    } else if (expression instanceof CreatorExpression) {
      printExpression((CreatorExpression) expression);
    }
  }

  private void printExpression(CreatorExpression expression) {
    print("new ");
    printExpression(expression.type());
    print("( ");
    expression.parameters().forEach(this::printExpression);
    print(" )");
  }

  private void printExpression(IdentifierExpression expression) {
    print(expression.name(), " ");
  }

  private void printExpression(LiteralExpression expression) {
    print(expression.value(), " ");
  }

  private void newline() {
    print("\n");
  }

  private void tab() {
    print("  ".repeat(indent));
  }

  private void tab(int count) {
    indent = count;
    tab();
  }

  private void println(Object... messages) {
    print(messages);
    newline();
  }

  private void print(Object... messages) {
    try {
      for (Object message : messages) {
        out.write(message.toString().getBytes(StandardCharsets.UTF_8));
      }
    } catch (Exception ignored) {
    }
  }
}
