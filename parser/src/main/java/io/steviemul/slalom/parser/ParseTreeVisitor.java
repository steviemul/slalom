package io.steviemul.slalom.parser;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.antlr.JavaParserBaseVisitor;
import io.steviemul.slalom.model.java.CompilationUnit;
import io.steviemul.slalom.model.java.ImportDeclaration;
import io.steviemul.slalom.model.java.PackageDeclaration;
import io.steviemul.slalom.parser.factories.DeclarationFactory;

public class ParseTreeVisitor extends JavaParserBaseVisitor<CompilationUnit> {

  private final DeclarationFactory declarationFactory = new DeclarationFactory();

  @Override
  public CompilationUnit visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
    CompilationUnit compilationUnit = new CompilationUnit();

    if (ctx.packageDeclaration() != null) {
      compilationUnit.packageDeclaration(packageDeclaration(ctx.packageDeclaration()));
    }

    ctx.importDeclaration()
        .forEach(i -> compilationUnit.importDeclarations().add(importDeclaration(i)));

    if (ctx.typeDeclaration(0) != null) {
      compilationUnit.typeDeclaration(declarationFactory.fromContext(ctx.typeDeclaration(0)));
    }

    return compilationUnit;
  }

  private PackageDeclaration packageDeclaration(JavaParser.PackageDeclarationContext ctx) {
    return new PackageDeclaration().name(ctx.qualifiedName().getText());
  }

  public ImportDeclaration importDeclaration(JavaParser.ImportDeclarationContext ctx) {
    return new ImportDeclaration()
        .wildcard(ctx.MUL() != null)
        .staticImport(ctx.STATIC() != null)
        .name(ctx.qualifiedName().getText());
  }
}
