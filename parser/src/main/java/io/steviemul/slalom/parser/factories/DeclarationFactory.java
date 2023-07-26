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
import io.steviemul.slalom.model.java.VariableDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;

import static io.steviemul.slalom.constants.ParserConstants.VOID;
import static io.steviemul.slalom.utils.ObjectUtils.isDefined;

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
    } else if (ctx.STATIC() != null) {
      declaration = new StaticBlockDeclaration();
      ((StaticBlockDeclaration) declaration).block(StatementFactory.fromContext(ctx.block()));
    }

    if (ctx.modifier() != null) {
      setModifiers(
          declaration,
          ctx.modifier().stream()
              .map(JavaParser.ModifierContext::classOrInterfaceModifier)
              .collect(Collectors.toList()));
    }

    setPosition(declaration, ctx);

    return declaration;
  }

  public static Declaration fromContext(JavaParser.MemberDeclarationContext ctx) {

    Declaration declaration = new Declaration();

    if (ctx.fieldDeclaration() != null) {
      declaration = fromContext(ctx.fieldDeclaration());
    } else if (ctx.constructorDeclaration() != null) {
      declaration = fromContext(ctx.constructorDeclaration());
    } else if (ctx.methodDeclaration() != null) {
      declaration = fromContext(ctx.methodDeclaration());
    } else if (ctx.classDeclaration() != null) {
      declaration = fromContext(ctx.classDeclaration());
    }

    return declaration;
  }

  public static ClassDeclaration fromContext(JavaParser.ClassDeclarationContext ctx) {
    ClassDeclaration classDeclaration = new ClassDeclaration();
    classDeclaration.name(ctx.identifier().getText());

    if (isDefined(ctx.EXTENDS())) {
      classDeclaration.parentClass(ctx.typeType().getText());
    }

    if (isDefined(ctx.IMPLEMENTS())) {
      List<String> interfaces =
          ctx.typeList().stream()
              .flatMap(tl -> tl.typeType().stream())
              .map(JavaParser.TypeTypeContext::getText)
              .collect(Collectors.toList());

      classDeclaration.interfaces(interfaces);
    }

    if (isDefined(ctx.classBody())) {
      classDeclaration.memberDeclarations(
          ctx.classBody().classBodyDeclaration().stream()
              .map(DeclarationFactory::fromContext)
              .collect(Collectors.toList()));
    }
    return classDeclaration;
  }

  public static FieldDeclaration fromContext(JavaParser.FieldDeclarationContext ctx) {
    FieldDeclaration fieldDeclaration = new FieldDeclaration();

    fieldDeclaration.type(ctx.typeType().getText());
    fieldDeclaration.variableDeclarations(
        ctx.variableDeclarators().variableDeclarator().stream()
            .map(DeclarationFactory::fromContext)
            .collect(Collectors.toList()));

    fieldDeclaration.position(ctx);

    return fieldDeclaration;
  }

  public static MethodDeclaration fromContext(JavaParser.MethodDeclarationContext ctx) {

    MethodDeclaration methodDeclaration = new MethodDeclaration();

    methodDeclaration.name(ctx.identifier().getText());
    methodDeclaration.type(fromContext(ctx.typeTypeOrVoid()));

    methodDeclaration.parameters(fromContext(ctx.formalParameters()));

    if (isDefined(ctx.methodBody().block())) {
      methodDeclaration.block(StatementFactory.fromContext(ctx.methodBody().block()));
    }

    methodDeclaration.position(ctx);

    return methodDeclaration;
  }

  public static String fromContext(JavaParser.TypeTypeOrVoidContext ctx) {

    if (ctx.VOID() != null) {
      return VOID;
    }

    return ctx.typeType().getText();
  }

  public static VariableDeclaration fromContext(JavaParser.VariableDeclaratorContext ctx) {
    VariableDeclaration variableDeclaration = new VariableDeclaration();

    variableDeclaration.name(ctx.variableDeclaratorId().identifier().getText());

    if (ctx.variableInitializer() != null) {
      variableDeclaration.initialValue(
          ExpressionFactory.fromContext(ctx.variableInitializer().expression()));
    }

    variableDeclaration.position(ctx);

    return variableDeclaration;
  }

  public static VariableDeclaration fromContext(JavaParser.FormalParameterContext ctx) {

    VariableDeclaration variableDeclaration = new VariableDeclaration();

    variableDeclaration.name(ctx.variableDeclaratorId().identifier().getText());
    variableDeclaration.type(ctx.typeType().getText());

    variableDeclaration.position(ctx);

    return variableDeclaration;
  }

  public static ConstructorDeclaration fromContext(JavaParser.ConstructorDeclarationContext ctx) {
    ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration();

    constructorDeclaration.name(ctx.identifier().getText());
    constructorDeclaration.type(ctx.identifier().getText());

    constructorDeclaration.parameters(fromContext(ctx.formalParameters()));

    if (isDefined(ctx.block())) {
      constructorDeclaration.block(StatementFactory.fromContext(ctx.block()));
    }

    constructorDeclaration.position(ctx);

    return constructorDeclaration;
  }

  public static List<VariableDeclaration> fromContext(JavaParser.FormalParametersContext ctx) {

    if (ctx != null && ctx.formalParameterList() != null) {
      return ctx.formalParameterList().formalParameter().stream()
          .map(DeclarationFactory::fromContext)
          .collect(Collectors.toList());
    }

    return new ArrayList<>();
  }

  private static void setPosition(Ref ref, ParserRuleContext ctx) {
    ref.position(ctx.start.getLine(), ctx.start.getCharPositionInLine());
  }

  private static void setModifiers(
      ModifiableRef modifiableRef,
      List<JavaParser.ClassOrInterfaceModifierContext> modifierContexts) {

    for (JavaParser.ClassOrInterfaceModifierContext modifierContext : modifierContexts) {
      modifiableRef.modifiers().add(modifierContext.getText());
    }
  }
}
