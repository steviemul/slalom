package io.steviemul.slalom.parser;

import io.steviemul.slalom.antlr.JavaLexer;
import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.CompilationUnit;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

@Slf4j
public class Parser {

  public CompilationUnit parse(String source) {

    JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    JavaParser tokenParser = new JavaParser(tokens);

    ParseTree parseTree = tokenParser.compilationUnit();

    ParseTreeVisitor parser = new ParseTreeVisitor();

    return parser.visit(parseTree);
  }
}
