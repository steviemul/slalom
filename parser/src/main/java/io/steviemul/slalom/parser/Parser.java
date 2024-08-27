package io.steviemul.slalom.parser;

import io.steviemul.slalom.antlr.JavaLexer;
import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.ASTRoot;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class Parser {

  public ASTRoot parse(String source) {

    String sha = DigestUtils.sha256Hex(source);

    JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    JavaParser tokenParser = new JavaParser(tokens);

    ParseTree parseTree = tokenParser.compilationUnit();

    ParseTreeVisitor parser = new ParseTreeVisitor();

    return parser.visit(parseTree)
        .sha(sha);
  }
}
