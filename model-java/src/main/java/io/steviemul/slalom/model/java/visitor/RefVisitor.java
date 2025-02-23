package io.steviemul.slalom.model.java.visitor;

import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.model.java.AnnotationDeclaration;
import io.steviemul.slalom.model.java.AnnotationElement;
import io.steviemul.slalom.model.java.ArrayExpression;
import io.steviemul.slalom.model.java.BinaryExpression;
import io.steviemul.slalom.model.java.BlockStatement;
import io.steviemul.slalom.model.java.ClassDeclaration;
import io.steviemul.slalom.model.java.ConstructorDeclaration;
import io.steviemul.slalom.model.java.CreatorExpression;
import io.steviemul.slalom.model.java.Declaration;
import io.steviemul.slalom.model.java.DotExpression;
import io.steviemul.slalom.model.java.Expression;
import io.steviemul.slalom.model.java.FieldDeclaration;
import io.steviemul.slalom.model.java.IdentifierExpression;
import io.steviemul.slalom.model.java.IfStatement;
import io.steviemul.slalom.model.java.ImportDeclaration;
import io.steviemul.slalom.model.java.InterfaceDeclaration;
import io.steviemul.slalom.model.java.LiteralExpression;
import io.steviemul.slalom.model.java.LocalVariableDeclarationStatement;
import io.steviemul.slalom.model.java.MemberDeclaration;
import io.steviemul.slalom.model.java.MethodCallExpression;
import io.steviemul.slalom.model.java.MethodDeclaration;
import io.steviemul.slalom.model.java.PackageDeclaration;
import io.steviemul.slalom.model.java.ParExpression;
import io.steviemul.slalom.model.java.ReturnStatement;
import io.steviemul.slalom.model.java.Statement;
import io.steviemul.slalom.model.java.StatementExpression;
import io.steviemul.slalom.model.java.StaticBlockDeclaration;
import io.steviemul.slalom.model.java.UnknownExpression;
import io.steviemul.slalom.model.java.VariableDeclaration;
import io.steviemul.slalom.model.java.WhileStatement;

public interface RefVisitor {
  void visitASTRoot(ASTRoot astRoot);

  void visitDeclaration(Declaration declaration);

  void visitCreatorExpression(CreatorExpression creatorExpression);

  void visitMethodDeclaration(MethodDeclaration methodDeclaration);

  void visitImportDeclaration(ImportDeclaration importDeclaration);

  void visitPackageDeclaration(PackageDeclaration packageDeclaration);

  void visitAnnotationDeclaration(AnnotationDeclaration annotationDeclaration);

  void visitAnnotationElement(AnnotationElement annotationElement);

  void visitArrayExpression(ArrayExpression arrayExpression);

  void visitBinaryExpression(BinaryExpression binaryExpression);

  void visitBlockStatement(BlockStatement blockStatement);

  void visitClassDeclaration(ClassDeclaration classDeclaration);

  void visitConstructorDeclaration(ConstructorDeclaration constructorDeclaration);

  void visitDotExpression(DotExpression dotExpression);

  void visitFieldDeclaration(FieldDeclaration fieldDeclaration);

  void visitIdentifierDeclaration(IdentifierExpression identifierExpression);

  void visitIfStatement(IfStatement ifStatement);

  void visitWhileStatement(WhileStatement whileStatement);

  void visitInterfaceDeclaration(InterfaceDeclaration interfaceDeclaration);

  void visitLiteralExpression(LiteralExpression literalExpression);

  void visitLocalVariableDeclarationStatement(
      LocalVariableDeclarationStatement localVariableDeclarationStatement);

  void visitMemberDeclaration(MemberDeclaration memberDeclaration);

  void visitMethodCallExpression(MethodCallExpression methodCallExpression);

  void visitParExpression(ParExpression parExpression);

  void visitReturnStatement(ReturnStatement returnStatement);

  void visitStatementExpression(StatementExpression statementExpression);

  void visitStaticBlockDeclaration(StaticBlockDeclaration staticBlockDeclaration);

  void visitVariableDeclaration(VariableDeclaration variableDeclaration);

  void visitUnknownExpression(UnknownExpression unknownExpression);

  void visitExpression(Expression expression);

  void visitStatement(Statement statement);
}
