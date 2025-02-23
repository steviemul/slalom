package io.steviemul.slalom.rules;

import io.steviemul.slalom.rules.error.DefaultErrorListener;
import io.steviemul.slalom.rules.model.RuleCollection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

@RequiredArgsConstructor
public class RuleInterpreter {

  private final String sourceName;

  public List<RuleCollection> parse(String source) {

    RuleLexer ruleLexer = new RuleLexer(CharStreams.fromString(source, sourceName));

    ruleLexer.removeErrorListeners();
    ruleLexer.addErrorListener(new DefaultErrorListener());

    CommonTokenStream tokens = new CommonTokenStream(ruleLexer);

    RuleParser tokenParser = new RuleParser(tokens);

    tokenParser.removeErrorListeners();
    tokenParser.addErrorListener(new DefaultErrorListener());

    ParseTree parseTree = tokenParser.rules();

    RuleInterpreterVisitor parser = new RuleInterpreterVisitor();

    return parser.visit(parseTree);
  }
}
