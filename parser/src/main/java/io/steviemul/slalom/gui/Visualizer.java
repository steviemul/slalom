package io.steviemul.slalom.gui;

import io.steviemul.slalom.antlr.JavaLexer;
import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.utils.ParseTreeViewer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Visualizer {

  public void visualize(String source) {

    JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    JavaParser tokenParser = new JavaParser(tokens);

    ParseTree tree = tokenParser.compilationUnit();

    showViewer(tokenParser.getRuleNames(), tree);
  }

  private void showViewer(String[] ruleNames, ParseTree parseTree) {
    ParseTreeViewer.showTree(ruleNames, parseTree);
  }
}
