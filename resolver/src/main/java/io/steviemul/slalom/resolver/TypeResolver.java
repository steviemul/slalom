package io.steviemul.slalom.resolver;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.model.java.ImportDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

@Slf4j
public class TypeResolver {

  private static final Map<String, List<MethodDefinition>> typeDefinitions = new TreeMap<>();

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

  public static Map<String, List<MethodDefinition>> getTypeDefinitions() {
    return typeDefinitions;
  }

  private static void readMethods(JavaClass javaClass) {

    List<MethodDefinition> methodDefinitions = new ArrayList<>();

    for (Method method : javaClass.getMethods()) {

      if (method.isPublic()) {
        MethodDefinition methodDefinition =
            MethodDefinition.builder()
                .name(method.getName())
                .returnType(method.getReturnType().getClassName())
                .argumentTypes(
                    Arrays.stream(method.getArgumentTypes())
                        .map(Type::getClassName)
                        .collect(Collectors.toList()))
                .build();

        methodDefinitions.sort(Comparator.comparing(MethodDefinition::getName));

        methodDefinitions.add(methodDefinition);
      }
    }

    typeDefinitions.putIfAbsent(javaClass.getClassName(), methodDefinitions);
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
