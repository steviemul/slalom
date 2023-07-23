package io.steviemul.slalom;

import io.steviemul.slalom.gui.Visualizer;
import io.steviemul.slalom.model.java.CompilationUnit;
import io.steviemul.slalom.parser.Parser;
import io.steviemul.slalom.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  private static final String SOURCE_SAMPLE = "examples/java/Sample.txt";

  public static void main(String[] args) {

    try {
      String source = IOUtils.readResource(SOURCE_SAMPLE);

      Visualizer visualizer = new Visualizer();

      visualizer.visualize(source);

      Parser parser = new Parser();

      CompilationUnit compilationUnit = parser.parse(source);

      System.out.println(compilationUnit);
    }
    catch (Exception e) {
      log.error("Error parsing source", e);
    }
  }
}
