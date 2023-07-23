package io.steviemul.slalom.parser;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.antlr.JavaParserBaseListener;
import io.steviemul.slalom.model.java.BlockRef;
import io.steviemul.slalom.model.java.ClassDeclaration;
import io.steviemul.slalom.model.java.CompilationUnit;
import io.steviemul.slalom.model.java.Declaration;
import io.steviemul.slalom.model.java.FieldDeclaration;
import io.steviemul.slalom.model.java.ImportDeclaration;
import io.steviemul.slalom.model.java.LocalVariableDeclarationStatement;
import io.steviemul.slalom.model.java.MethodDeclaration;
import io.steviemul.slalom.model.java.PackageDeclaration;
import io.steviemul.slalom.model.java.Statement;
import io.steviemul.slalom.model.java.StaticBlockDeclaration;
import io.steviemul.slalom.parser.factories.DeclarationFactory;
import io.steviemul.slalom.parser.factories.StatementFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static io.steviemul.slalom.constants.TypeConstants.VOID;
import static io.steviemul.slalom.utils.StringUtils.hasText;

@RequiredArgsConstructor
@Getter
public class ParseTreeListener extends JavaParserBaseListener {

  private final ParseContext parseContext;

  @Override
  public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
    CompilationUnit compilationUnit = new CompilationUnit();

    parseContext.push(compilationUnit);
  }

  @Override
  public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
    CompilationUnit compilationUnit = parseContext.requireType(CompilationUnit.class);

    PackageDeclaration packageDeclaration = new PackageDeclaration()
        .name(ctx.qualifiedName().getText());

    compilationUnit.packageDeclaration(packageDeclaration);
  }

  @Override
  public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
    CompilationUnit compilationUnit = parseContext.requireType(CompilationUnit.class);

    ImportDeclaration importDeclaration = new ImportDeclaration()
        .wildcard(ctx.MUL() != null)
        .staticImport(ctx.STATIC() != null)
        .name(ctx.qualifiedName().getText());

    compilationUnit.importDeclarations().add(importDeclaration);
  }

  @Override
  public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
    CompilationUnit compilationUnit = parseContext.requireType(CompilationUnit.class);

    Declaration declaration = DeclarationFactory.fromContext(ctx);

    compilationUnit.typeDeclaration(declaration);

    parseContext.push(declaration);
  }

  @Override
  public void exitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
    parseContext.popRequiredType(Declaration.class);
  }

  @Override
  public void enterClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
    ClassDeclaration classDeclaration = parseContext.requireType(ClassDeclaration.class);

    Declaration declaration = DeclarationFactory.fromContext(ctx);

    classDeclaration.memberDeclarations().add(declaration);

    parseContext.push(declaration);
  }

  @Override
  public void exitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
    parseContext.popRequiredType(Declaration.class);
  }

  @Override
  public void enterTypeTypeOrVoid(JavaParser.TypeTypeOrVoidContext ctx) {
    Declaration declaration = parseContext.requireType(Declaration.class);

    if (ctx.VOID() != null) {
      declaration.type(VOID);
    }
  }

  @Override
  public void enterTypeType(JavaParser.TypeTypeContext ctx) {
    Declaration declaration = parseContext.requireType(Declaration.class);

    if (!hasText(declaration.type())) {
      declaration.type(ctx.getText());
    }
  }

  @Override
  public void enterBlock(JavaParser.BlockContext ctx) {
    parseContext.requestType(StaticBlockDeclaration.class)
        .ifPresent(d -> parseContext.push(d.block()));

    parseContext.requestType(MethodDeclaration.class)
        .ifPresent(d -> parseContext.push(d.block()));
  }

  @Override
  public void exitBlock(JavaParser.BlockContext ctx) {
    parseContext.popRequiredType(BlockRef.class);
  }

  @Override
  public void enterBlockStatement(JavaParser.BlockStatementContext ctx) {
    BlockRef block = parseContext.requireType(BlockRef.class);

    Statement statement = StatementFactory.fromContext(ctx);

    block.statements().add(statement);

    parseContext.push(statement);
  }

  @Override
  public void exitBlockStatement(JavaParser.BlockStatementContext ctx) {
    parseContext.popRequiredType(Statement.class);
  }

  @Override
  public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
    LocalVariableDeclarationStatement declarationStatement
        = parseContext.requireType(LocalVariableDeclarationStatement.class);

    parseContext.push(declarationStatement.fieldDeclaration());
  }

  @Override
  public void exitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
    parseContext.popRequiredType(FieldDeclaration.class);
  }
}
