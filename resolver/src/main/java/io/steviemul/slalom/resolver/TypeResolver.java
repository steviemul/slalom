package io.steviemul.slalom.resolver;

import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.model.java.Declaration;
import io.steviemul.slalom.model.java.ImportDeclaration;
import io.steviemul.slalom.model.java.MethodDeclaration;
import io.steviemul.slalom.model.java.VariableDeclaration;
import io.steviemul.slalom.model.java.visitor.AbstractRefVisitor;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

@Slf4j
@Getter
public class TypeResolver extends AbstractRefVisitor {

  private static final Map<String, Declaration> classes = new TreeMap<>();
  private static final Map<String, List<MethodDeclaration>> typeDefinitions = new TreeMap<>();

  static {
    initClasses();
  }

  public static void addCompilationUnit(ASTRoot astRoot) {

    Date start = new Date();

    String fqn = astRoot.packageDeclaration().name() + "." + astRoot.typeDeclaration().name();

    classes.put(fqn, astRoot.typeDeclaration());

    for (ImportDeclaration importDeclaration : astRoot.importDeclarations()) {

      if (!importDeclaration.wildcard()) {
        lookupClass(importDeclaration.name()).ifPresent(TypeResolver::addClassDeclaration);
      } else {
        lookupClasses(importDeclaration.name()).forEach(TypeResolver::addClassDeclaration);
      }
    }

    log.info("Resolved types, took {}ms", new Date().getTime() - start.getTime());
  }

  private static void initClasses() {

    for (String langClass : LangClasses.LANG_CLASSES) {
      String fqn = "java.lang." + langClass;

      lookupClass(fqn).ifPresent(TypeResolver::addClassDeclaration);
    }
  }

  private static void addClassDeclaration(JavaClass javaClass) {

    ShadowClass shadowClass = new ShadowClass();

    String fqn = javaClass.getClassName();

    shadowClass.fqn(fqn);
    shadowClass.name(getClassName(fqn));

    Arrays.stream(javaClass.getMethods())
        .forEach(
            method -> {
              shadowClass.memberDeclarations().add(fromMethod(method));
            });

    classes.put(fqn, shadowClass);
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
        Arrays.stream(method.getArgumentTypes())
            .map(TypeResolver::fromType)
            .collect(Collectors.toList()));

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

  private static String getClassName(String fqn) {

    String[] parts = fqn.split("\\.");

    if (parts.length > 0) {
      return parts[parts.length - 1];
    }

    return fqn;
  }
}
