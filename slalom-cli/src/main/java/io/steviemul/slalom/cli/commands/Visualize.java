package io.steviemul.slalom.cli.commands;

import io.steviemul.slalom.antlr.JavaLexer;
import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.utils.IOUtils;
import io.steviemul.slalom.utils.ParseTreeViewer;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "visualize",
    description = "Parses a source file displays its parse tree in a gui")
@Slf4j
public class Visualize implements Callable<Integer> {

  @CommandLine.Parameters(paramLabel = "<file>", description = "The source file to visualize")
  private File file;

  @Override
  public Integer call() {

    try {
      String source = IOUtils.readFile(file.getAbsolutePath());

      visualize(source);
    } catch (Exception e) {
      log.error("Error visualizing source code", e);
    }

    return 0;
  }

  private void visualize(String source) {

    JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    JavaParser tokenParser = new JavaParser(tokens);

    ParseTree tree = tokenParser.compilationUnit();

    ParseTreeViewer.showTree(tokenParser.getRuleNames(), tree);
  }
  
}
