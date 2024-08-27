package io.steviemul.slalom.parser;

import io.steviemul.slalom.antlr.JavaLexer;
import io.steviemul.slalom.antlr.JavaParser;
import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.utils.HashUtils;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class Parser {

  public ASTRoot parse(String filename, String source) {

    String sha = HashUtils.sha(source);

    JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    JavaParser tokenParser = new JavaParser(tokens);

    ParseTree parseTree = tokenParser.compilationUnit();

    try (ParserContext parserContext = ParserContext.currentContext()) {
      parserContext.setFilename(filename);

      ParseTreeVisitor parser = new ParseTreeVisitor();

      return parser
          .visit(parseTree)
          .sha(sha);
    }

  }
}
