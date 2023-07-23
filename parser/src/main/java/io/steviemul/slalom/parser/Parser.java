package io.steviemul.slalom.parser;

import io.steviemul.slalom.antlr.JavaLexer;
import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.CompilationUnit;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Parser {

  public CompilationUnit parse(String source) {

    JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    JavaParser tokenParser = new JavaParser(tokens);

    ParseTree parseTree = tokenParser.compilationUnit();
    ParseTreeWalker walker = new ParseTreeWalker();

    ParseContext parseContext = new ParseContext();
    ParseTreeListener parseTreeListener = new ParseTreeListener(parseContext);

    walker.walk(parseTreeListener, parseTree);

    return parseContext.popRequiredType(CompilationUnit.class);
  }
}
