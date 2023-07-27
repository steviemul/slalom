package io.steviemul.slalom.parser;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.antlr.JavaParserBaseVisitor;
import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.model.java.ImportDeclaration;
import io.steviemul.slalom.model.java.PackageDeclaration;
import io.steviemul.slalom.parser.factories.DeclarationFactory;

public class ParseTreeVisitor extends JavaParserBaseVisitor<ASTRoot> {

  @Override
  public ASTRoot visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
    ASTRoot ASTRoot = new ASTRoot();

    if (ctx.packageDeclaration() != null) {
      ASTRoot.packageDeclaration(packageDeclaration(ctx.packageDeclaration()));
    }

    ctx.importDeclaration().forEach(i -> ASTRoot.importDeclarations().add(importDeclaration(i)));

    if (ctx.typeDeclaration(0) != null) {
      ASTRoot.typeDeclaration(DeclarationFactory.fromContext(ctx.typeDeclaration(0)));
    }

    return ASTRoot;
  }

  private PackageDeclaration packageDeclaration(JavaParser.PackageDeclarationContext ctx) {
    return new PackageDeclaration().name(ctx.qualifiedName().getText());
  }

  public ImportDeclaration importDeclaration(JavaParser.ImportDeclarationContext ctx) {
    ImportDeclaration importDeclaration =
        new ImportDeclaration()
            .wildcard(ctx.MUL() != null)
            .staticImport(ctx.STATIC() != null)
            .name(ctx.qualifiedName().getText());

    importDeclaration.position(ctx);

    return importDeclaration;
  }
}
