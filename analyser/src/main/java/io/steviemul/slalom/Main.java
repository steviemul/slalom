package io.steviemul.slalom;

import io.steviemul.slalom.gui.Visualizer;
import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.parser.Parser;
import io.steviemul.slalom.resolver.TypeResolver;
import io.steviemul.slalom.serializer.ASTRootSerializer;
import io.steviemul.slalom.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Main {

  public static void main(String[] args) {

    try {
      String path = args[0];
      String source = IOUtils.readFile(path);

      Visualizer visualizer = new Visualizer();

      visualizer.visualize(source);

      Parser parser = new Parser();

      Date start = new Date();

      ASTRoot astRoot = parser.parse(source);

      astRoot.path(path);

      log.info("Parsed [{}], took {}ms", path, new Date().getTime() - start.getTime());

      TypeResolver.addCompilationUnit(astRoot);

      String yamlOutput = ASTRootSerializer.toYAML(astRoot);

      System.out.println(yamlOutput);
    } catch (Exception e) {
      log.error("Error parsing source", e);
    }
  }
}
