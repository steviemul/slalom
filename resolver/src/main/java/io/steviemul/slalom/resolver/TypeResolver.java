package io.steviemul.slalom.resolver;

import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.model.java.ImportDeclaration;
import io.steviemul.slalom.model.java.MethodDeclaration;
import io.steviemul.slalom.model.java.VariableDeclaration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class TypeResolver {

  @Getter
  private static final Map<String, List<MethodDeclaration>> typeDefinitions = new TreeMap<>();

  public static void resolveTypes(ASTRoot ASTRoot) {

    Date start = new Date();

    for (ImportDeclaration importDeclaration : ASTRoot.importDeclarations()) {

      if (!importDeclaration.wildcard()) {
        lookupClass(importDeclaration.name()).ifPresent(TypeResolver::readMethods);
      } else {
        lookupClasses(importDeclaration.name()).forEach(TypeResolver::readMethods);
      }
    }

    for (String langClass : LangClasses.LANG_CLASSES) {
      lookupClass("java.lang." + langClass).ifPresent(TypeResolver::readMethods);
    }

    log.info("Resolved types, took {}ms", new Date().getTime() - start.getTime());
  }

  private static void readMethods(JavaClass javaClass) {

    List<MethodDeclaration> methodDeclarations = Arrays.stream(javaClass.getMethods())
        .map(TypeResolver::fromMethod).collect(Collectors.toList());

    typeDefinitions.putIfAbsent(javaClass.getClassName(), methodDeclarations);
  }

  private static MethodDeclaration fromMethod(Method method) {
    MethodDeclaration methodDeclaration = new MethodDeclaration();

    if (method.isPublic()) methodDeclaration.modifiers().add("public");
    if (method.isPrivate()) methodDeclaration.modifiers().add("private");
    if (method.isStatic()) methodDeclaration.modifiers().add("static");
    if (method.isFinal()) methodDeclaration.modifiers().add("final");

    methodDeclaration.name(method.getName());
    methodDeclaration.type(method.getReturnType().getClassName());
    methodDeclaration.parameters(
        Arrays.stream(method.getArgumentTypes()).map(
            TypeResolver::fromType
        ).collect(Collectors.toList()));

    return methodDeclaration;
  }

  private static VariableDeclaration fromType(Type type) {
    VariableDeclaration variableDeclaration = new VariableDeclaration();

    variableDeclaration.type(type.getClassName());

    return variableDeclaration;
  }

  private static Optional<JavaClass> lookupClass(String className) {

    try {
      JavaClass javaClass = Repository.lookupClass(className);

      return Optional.of(javaClass);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static Optional<JavaClass> lookupClass(Class<?> klass) {

    try {
      JavaClass javaClass = Repository.lookupClass(klass);

      return Optional.of(javaClass);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static List<JavaClass> lookupClasses(String packageName) {
    Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));

    return reflections.getSubTypesOf(Object.class).stream()
        .map(TypeResolver::lookupClass)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  private static Optional<JavaClass> readClass(InputStream in, String filename) {

    try {
      ClassParser classParser = new ClassParser(in, filename);

      JavaClass javaClass = classParser.parse();

      return Optional.of(javaClass);
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
