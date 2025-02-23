package io.steviemul.slalom.model.java.visitor;

import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.model.java.AnnotationDeclaration;
import io.steviemul.slalom.model.java.ArrayExpression;
import io.steviemul.slalom.model.java.BinaryExpression;
import io.steviemul.slalom.model.java.BlockStatement;
import io.steviemul.slalom.model.java.ClassDeclaration;
import io.steviemul.slalom.model.java.CreatorExpression;
import io.steviemul.slalom.model.java.Declaration;
import io.steviemul.slalom.model.java.DotExpression;
import io.steviemul.slalom.model.java.Expression;
import io.steviemul.slalom.model.java.FieldDeclaration;
import io.steviemul.slalom.model.java.IdentifierExpression;
import io.steviemul.slalom.model.java.IfStatement;
import io.steviemul.slalom.model.java.LiteralExpression;
import io.steviemul.slalom.model.java.LocalVariableDeclarationStatement;
import io.steviemul.slalom.model.java.MethodCallExpression;
import io.steviemul.slalom.model.java.MethodDeclaration;
import io.steviemul.slalom.model.java.ParExpression;
import io.steviemul.slalom.model.java.Ref;
import io.steviemul.slalom.model.java.ReturnStatement;
import io.steviemul.slalom.model.java.Statement;
import io.steviemul.slalom.model.java.StatementExpression;
import io.steviemul.slalom.model.java.StaticBlockDeclaration;
import io.steviemul.slalom.model.java.UnknownExpression;
import io.steviemul.slalom.model.java.VariableDeclaration;
import io.steviemul.slalom.model.java.WhileStatement;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ASTWalker {

  private final ASTRoot astRoot;
  private final RefVisitor visitor;

  public void walk() {
    accept(astRoot);
    accept(astRoot.packageDeclaration());
    astRoot.importDeclarations().forEach(this::accept);
    accept(astRoot.typeDeclaration());
  }

  private void accept(Ref ref) {
    ref.accept(visitor);
  }

  private void accept(Declaration declaration) {
    if (declaration instanceof ClassDeclaration) {
      accept((ClassDeclaration) declaration);
    } else if (declaration instanceof FieldDeclaration) {
      accept((FieldDeclaration) declaration);
    } else if (declaration instanceof MethodDeclaration) {
      accept((MethodDeclaration) declaration);
    } else if (declaration instanceof AnnotationDeclaration) {
      accept((AnnotationDeclaration) declaration);
    } else if (declaration instanceof StaticBlockDeclaration) {
      accept((StaticBlockDeclaration) declaration);
    }
  }

  private void accept(Statement statement) {
    if (statement instanceof BlockStatement) {
      accept((BlockStatement) statement);
    } else if (statement instanceof IfStatement) {
      accept((IfStatement) statement);
    } else if (statement instanceof WhileStatement) {
      accept((WhileStatement) statement);
    } else if (statement instanceof LocalVariableDeclarationStatement) {
      accept((LocalVariableDeclarationStatement) statement);
    } else if (statement instanceof ReturnStatement) {
      accept((ReturnStatement) statement);
    } else if (statement instanceof StatementExpression) {
      accept((StatementExpression) statement);
    }
  }

  private void accept(Expression expression) {

    if (expression instanceof ArrayExpression) {
      accept((ArrayExpression) expression);
    } else if (expression instanceof BinaryExpression) {
      accept((BinaryExpression) expression);
    } else if (expression instanceof CreatorExpression) {
      accept((CreatorExpression) expression);
    } else if (expression instanceof DotExpression) {
      accept((DotExpression) expression);
    } else if (expression instanceof IdentifierExpression) {
      accept((IdentifierExpression) expression);
    } else if (expression instanceof LiteralExpression) {
      accept((LiteralExpression) expression);
    } else if (expression instanceof MethodCallExpression) {
      accept((MethodCallExpression) expression);
    } else if (expression instanceof ParExpression) {
      accept((ParExpression) expression);
    } else if (expression instanceof UnknownExpression) {
      accept((UnknownExpression) expression);
    }
  }

  private void accept(ClassDeclaration classDeclaration) {
    classDeclaration.accept(visitor);
    classDeclaration.memberDeclarations().forEach(this::accept);
  }

  private void accept(FieldDeclaration fieldDeclaration) {
    fieldDeclaration.accept(visitor);

    fieldDeclaration.variableDeclarations().forEach(this::accept);
  }

  private void accept(MethodDeclaration methodDeclaration) {
    methodDeclaration.accept(visitor);
    methodDeclaration.parameters().forEach(this::accept);

    Optional.ofNullable(methodDeclaration.block()).ifPresent(this::accept);
  }

  private void accept(VariableDeclaration variableDeclaration) {
    variableDeclaration.accept(visitor);
  }

  private void accept(AnnotationDeclaration annotationDeclaration) {
    annotationDeclaration.accept(visitor);
  }

  private void accept(StaticBlockDeclaration staticBlockDeclaration) {
    staticBlockDeclaration.accept(visitor);
    Optional.ofNullable(staticBlockDeclaration.block()).ifPresent(this::accept);
  }

  private void accept(BlockStatement blockStatement) {
    blockStatement.accept(visitor);
    blockStatement.statements().forEach(this::accept);
  }

  private void accept(IfStatement ifStatement) {
    ifStatement.accept(visitor);

    Optional.ofNullable(ifStatement.expression()).ifPresent(this::accept);
    Optional.ofNullable(ifStatement.thenStatement()).ifPresent(this::accept);
    Optional.ofNullable(ifStatement.elseStatement()).ifPresent(this::accept);
  }

  private void accept(WhileStatement whileStatement) {
    whileStatement.accept(visitor);

    Optional.ofNullable(whileStatement.expression()).ifPresent(this::accept);
    Optional.ofNullable(whileStatement.statement()).ifPresent(this::accept);
  }

  private void accept(LocalVariableDeclarationStatement localVariableDeclarationStatement) {
    localVariableDeclarationStatement.accept(visitor);
    localVariableDeclarationStatement.variableDeclarations().forEach(this::accept);
  }

  private void accept(ReturnStatement returnStatement) {
    returnStatement.accept(visitor);
    Optional.ofNullable(returnStatement.expression()).ifPresent(this::accept);
  }

  private void accept(StatementExpression statementExpression) {
    statementExpression.accept(visitor);
  }

  private void accept(ArrayExpression arrayExpression) {
    arrayExpression.accept(visitor);
    Optional.ofNullable(arrayExpression.identifier()).ifPresent(this::accept);
  }

  private void accept(BinaryExpression binaryExpression) {
    binaryExpression.accept(visitor);
  }

  private void accept(CreatorExpression creatorExpression) {
    creatorExpression.accept(visitor);
  }

  private void accept(DotExpression dotExpression) {
    dotExpression.accept(visitor);
  }

  private void accept(IdentifierExpression identifierExpression) {
    identifierExpression.accept(visitor);
  }

  private void accept(LiteralExpression literalExpression) {
    literalExpression.accept(visitor);
  }

  private void accept(MethodCallExpression methodCallExpression) {
    methodCallExpression.accept(visitor);
  }

  private void accept(ParExpression parExpression) {
    parExpression.accept(visitor);
  }

  private void accept(UnknownExpression unknownExpression) {
    unknownExpression.accept(visitor);
  }
}
