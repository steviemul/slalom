package io.steviemul.slalom;

import io.steviemul.slalom.analyser.PseudoCodePrinter;
import io.steviemul.slalom.gui.Visualizer;
import io.steviemul.slalom.model.java.CompilationUnit;
import io.steviemul.slalom.parser.Parser;
import io.steviemul.slalom.resolver.TypeResolver;
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

      CompilationUnit compilationUnit = parser.parse(source);

      compilationUnit.path(path);

      log.info("Parsed [{}], took {}ms", path, new Date().getTime() - start.getTime());

      TypeResolver.resolveTypes(compilationUnit);

      PseudoCodePrinter printer = new PseudoCodePrinter(System.out);

      printer.print(compilationUnit);

    } catch (Exception e) {
      log.error("Error parsing source", e);
    }
  }
}
