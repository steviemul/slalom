package io.steviemul.slalom.parser.factories;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.ClassDeclaration;
import io.steviemul.slalom.model.java.ConstructorDeclaration;
import io.steviemul.slalom.model.java.Declaration;
import io.steviemul.slalom.model.java.FieldDeclaration;
import io.steviemul.slalom.model.java.MethodDeclaration;
import io.steviemul.slalom.model.java.ModifiableRef;
import io.steviemul.slalom.model.java.Ref;
import io.steviemul.slalom.model.java.StaticBlockDeclaration;
import java.util.List;
import java.util.stream.Collectors;

import io.steviemul.slalom.model.java.VariableDeclaration;
import org.antlr.v4.runtime.ParserRuleContext;

public class DeclarationFactory {

  public static Declaration fromContext(JavaParser.TypeDeclarationContext ctx) {

    Declaration declaration = new Declaration();

    if (ctx.classDeclaration() != null) {
      declaration = fromContext(ctx.classDeclaration());
    }

    setPosition(declaration, ctx);
    setModifiers(declaration, ctx.classOrInterfaceModifier());

    return declaration;
  }

  public static Declaration fromContext(JavaParser.ClassBodyDeclarationContext ctx) {
    Declaration declaration = new Declaration();

    if (ctx.memberDeclaration() != null) {
      declaration = fromContext(ctx.memberDeclaration());
    }
    else if (ctx.STATIC() != null) {
      declaration = new StaticBlockDeclaration();
    }

    if (ctx.modifier() != null) {
      setModifiers(declaration,
          ctx.modifier()
              .stream().map(JavaParser.ModifierContext::classOrInterfaceModifier)
              .collect(Collectors.toList()));
    }

    setPosition(declaration, ctx);

    return declaration;
  }

  public static Declaration fromContext(JavaParser.MemberDeclarationContext ctx) {

    Declaration declaration = new Declaration();

    if (ctx.fieldDeclaration() != null) {
      declaration = fromContext(ctx.fieldDeclaration());
    }
    else if (ctx.constructorDeclaration() != null) {
      declaration = fromContext(ctx.constructorDeclaration());
    }
    else if (ctx.methodDeclaration() != null) {
      declaration = fromContext(ctx.methodDeclaration());
    }
    else if (ctx.classDeclaration() != null) {
      declaration = fromContext(ctx.classDeclaration());
    }

    return declaration;
  }

  public static ClassDeclaration fromContext(JavaParser.ClassDeclarationContext ctx) {
    ClassDeclaration classDeclaration = new ClassDeclaration();
    classDeclaration.name(ctx.identifier().getText());

    if (ctx.EXTENDS() != null) {
      classDeclaration.parentClass(ctx.typeType().getText());
    }

    if (ctx.IMPLEMENTS() != null) {
      List<String> interfaces = ctx.typeList().stream()
          .flatMap(tl -> tl.typeType().stream())
          .map(JavaParser.TypeTypeContext::getText)
          .collect(Collectors.toList());

      classDeclaration.interfaces(interfaces);
    }

    return classDeclaration;
  }

  public static FieldDeclaration fromContext(JavaParser.FieldDeclarationContext ctx) {
    FieldDeclaration fieldDeclaration = new FieldDeclaration();

    return fieldDeclaration;
  }

  public static MethodDeclaration fromContext(JavaParser.MethodDeclarationContext ctx) {

    MethodDeclaration methodDeclaration = new MethodDeclaration();

    methodDeclaration.name(ctx.identifier().getText());

    return methodDeclaration;
  }

  public static VariableDeclaration fromContext(JavaParser.VariableDeclaratorContext ctx) {
    VariableDeclaration variableDeclaration = new VariableDeclaration();

    variableDeclaration.name(ctx.variableDeclaratorId().identifier().getText());

    return variableDeclaration;
  }

  public static VariableDeclaration fromContext(JavaParser.FormalParameterContext ctx) {

    VariableDeclaration variableDeclaration = new VariableDeclaration();

    variableDeclaration.name(ctx.variableDeclaratorId().identifier().getText());

    return variableDeclaration;
  }

  public static ConstructorDeclaration fromContext(JavaParser.ConstructorDeclarationContext ctx) {
    return new ConstructorDeclaration();
  }

  private static void setPosition(Ref ref, ParserRuleContext ctx) {
    ref.position(ctx.start.getLine(), ctx.start.getCharPositionInLine());
  }

  private static void setModifiers(ModifiableRef modifiableRef,
                                   List<JavaParser.ClassOrInterfaceModifierContext> modifierContexts) {

    for (JavaParser.ClassOrInterfaceModifierContext modifierContext : modifierContexts) {
      modifiableRef.modifiers().add(modifierContext.getText());
    }
  }
}
