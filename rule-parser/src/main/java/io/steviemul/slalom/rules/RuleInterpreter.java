package io.steviemul.slalom.rules;

import io.steviemul.slalom.rules.model.Rule;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class RuleInterpreter {

  public Rule parse(String source) {

    RuleLexer ruleLexer = new RuleLexer(CharStreams.fromString(source));
    CommonTokenStream tokens = new CommonTokenStream(ruleLexer);

    RuleParser tokenParser = new RuleParser(tokens);

    ParseTree parseTree = tokenParser.rule_();

    RuleInterpreterVisitor parser = new RuleInterpreterVisitor();

    return parser.visit(parseTree);
  }
}
