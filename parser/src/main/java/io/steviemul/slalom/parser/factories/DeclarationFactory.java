package io.steviemul.slalom.parser.factories;

import static io.steviemul.slalom.constants.ParserConstants.VOID;
import static io.steviemul.slalom.utils.ContextUtils.logUnhandled;
import static io.steviemul.slalom.utils.ObjectUtils.isDefined;

import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.AnnotationDeclaration;
import io.steviemul.slalom.model.java.AnnotationElement;
import io.steviemul.slalom.model.java.ClassDeclaration;
import io.steviemul.slalom.model.java.ConstructorDeclaration;
import io.steviemul.slalom.model.java.Declaration;
import io.steviemul.slalom.model.java.Expression;
import io.steviemul.slalom.model.java.FieldDeclaration;
import io.steviemul.slalom.model.java.InterfaceDeclaration;
import io.steviemul.slalom.model.java.MethodDeclaration;
import io.steviemul.slalom.model.java.ModifiableRef;
import io.steviemul.slalom.model.java.Ref;
import io.steviemul.slalom.model.java.StaticBlockDeclaration;
import io.steviemul.slalom.model.java.VariableDeclaration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;

public class DeclarationFactory {

  public static Declaration fromContext(JavaParser.TypeDeclarationContext ctx) {

    Declaration declaration = new Declaration();

    if (ctx.classDeclaration() != null) {
      declaration = fromContext(ctx.classDeclaration());
    } else if (ctx.interfaceDeclaration() != null) {
      declaration = fromContext(ctx.interfaceDeclaration());
    }

    setPosition(declaration, ctx);

    setClassOrInterfaceModifiers(declaration, ctx.classOrInterfaceModifier());

    return declaration;
  }

  public static Declaration fromContext(JavaParser.InterfaceDeclarationContext ctx) {
    InterfaceDeclaration interfaceDeclaration = new InterfaceDeclaration();
    interfaceDeclaration.name(ctx.identifier().getText());

    if (ctx.EXTENDS() != null) {
      interfaceDeclaration.parents(fromContext(ctx.typeList()));
    }

    interfaceDeclaration.memberDeclarations(fromContext(ctx.interfaceBody()));

    return interfaceDeclaration;
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
      setModifiers(declaration, ctx.modifier());
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
      classDeclaration.interfaces(fromContext(ctx.typeList()));
    }

    if (isDefined(ctx.classBody())) {
      classDeclaration.memberDeclarations(fromContext(ctx.classBody()));
    }

    return classDeclaration;
  }

  public static List<String> fromContext(List<JavaParser.TypeListContext> ctx) {
    return ctx.stream()
        .flatMap(tl -> tl.typeType().stream())
        .map(JavaParser.TypeTypeContext::getText)
        .collect(Collectors.toList());
  }

  public static List<Declaration> fromContext(JavaParser.ClassBodyContext ctx) {
    return ctx.classBodyDeclaration().stream()
        .map(DeclarationFactory::fromContext)
        .collect(Collectors.toList());
  }

  public static List<Declaration> fromContext(JavaParser.InterfaceBodyContext ctx) {
    return null;
  }

  public static FieldDeclaration fromContext(JavaParser.FieldDeclarationContext ctx) {
    FieldDeclaration fieldDeclaration = new FieldDeclaration();

    fieldDeclaration.type(ctx.typeType().getText());
    fieldDeclaration.variableDeclarations(fromContext(ctx.variableDeclarators()));

    fieldDeclaration.position(ctx);

    return fieldDeclaration;
  }

  public static List<VariableDeclaration> fromContext(JavaParser.VariableDeclaratorsContext ctx) {
    return ctx.variableDeclarator().stream()
        .map(DeclarationFactory::fromContext)
        .collect(Collectors.toList());
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
      if (ctx.variableInitializer().expression() != null) {
        variableDeclaration.initialValue(
            ExpressionFactory.fromContext(ctx.variableInitializer().expression()));
      } else {
        logUnhandled(ctx);
      }
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

  public static AnnotationDeclaration fromContext(JavaParser.AnnotationContext ctx) {
    AnnotationDeclaration declaration = new AnnotationDeclaration();

    declaration.name(ctx.qualifiedName().getText());

    if (ctx.elementValue() != null) {
      declaration.values(getAnnotationElementValues(ctx.elementValue()));
    }

    if (ctx.elementValuePairs() != null && ctx.elementValuePairs().elementValuePair() != null) {
      declaration.elements(
          ctx.elementValuePairs().elementValuePair().stream()
              .map(DeclarationFactory::fromContext)
              .collect(Collectors.toList()));
    }

    return declaration;
  }

  public static AnnotationElement fromContext(JavaParser.ElementValuePairContext ctx) {
    AnnotationElement element = new AnnotationElement();

    element.position(ctx);
    element.identifier(ExpressionFactory.fromContext(ctx.identifier()));

    element.values(getAnnotationElementValues(ctx.elementValue()));

    if (ctx.elementValue().expression() != null) {
      element.values(List.of(ExpressionFactory.fromContext(ctx.elementValue().expression())));
    } else if (ctx.elementValue().elementValueArrayInitializer() != null) {
      List<JavaParser.ExpressionContext> expressions =
          ctx.elementValue().elementValueArrayInitializer().elementValue().stream()
              .map(JavaParser.ElementValueContext::expression)
              .collect(Collectors.toList());

      element.values(expressions.stream().map(ExpressionFactory::fromContext).toList());
    }

    return element;
  }

  private static void setPosition(Ref ref, ParserRuleContext ctx) {
    ref.position(ctx.start.getLine(), ctx.start.getCharPositionInLine());
  }

  private static void setModifiers(
      ModifiableRef modifiableRef, List<JavaParser.ModifierContext> modifierContexts) {

    for (JavaParser.ModifierContext modifierContext : modifierContexts) {
      if (modifierContext.classOrInterfaceModifier() != null) {
        setClassOrInterfaceModifier(modifiableRef, modifierContext.classOrInterfaceModifier());
      } else if (modifierContext.TRANSIENT() != null) {
        modifiableRef.modifiers().add(modifierContext.TRANSIENT().getText());
      } else if (modifierContext.SYNCHRONIZED() != null) {
        modifiableRef.modifiers().add(modifierContext.SYNCHRONIZED().getText());
      } else if (modifierContext.VOLATILE() != null) {
        modifiableRef.modifiers().add(modifierContext.VOLATILE().getText());
      }
    }
  }

  private static void setClassOrInterfaceModifiers(
      ModifiableRef modifiableRef, List<JavaParser.ClassOrInterfaceModifierContext> modifiers) {
    modifiers.forEach(m -> setClassOrInterfaceModifier(modifiableRef, m));
  }

  private static void setClassOrInterfaceModifier(
      ModifiableRef modifiableRef, JavaParser.ClassOrInterfaceModifierContext ctx) {
    if (ctx.annotation() != null) {
      modifiableRef.annotations().add(fromContext(ctx.annotation()));
    } else {
      modifiableRef.modifiers().add(ctx.getText());
    }
  }

  private static List<Expression> getAnnotationElementValues(JavaParser.ElementValueContext ctx) {
    List<Expression> expressions = new ArrayList<>();

    if (ctx.expression() != null) {
      expressions = List.of(ExpressionFactory.fromContext(ctx.expression()));
    } else if (ctx.elementValueArrayInitializer() != null) {
      expressions =
          ctx.elementValueArrayInitializer().elementValue().stream()
              .map(JavaParser.ElementValueContext::expression)
              .map(ExpressionFactory::fromContext)
              .collect(Collectors.toList());
    }

    return expressions;
  }
}
