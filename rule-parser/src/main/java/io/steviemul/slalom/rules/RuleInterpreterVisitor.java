package io.steviemul.slalom.rules;

import io.steviemul.slalom.rules.model.EntryRule;
import io.steviemul.slalom.rules.model.Rule;
import io.steviemul.slalom.rules.model.RuleCollection;
import io.steviemul.slalom.rules.model.SinkRule;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class RuleInterpreterVisitor extends RuleParserBaseVisitor<List<RuleCollection>> {

  @Override
  public List<RuleCollection> visitRules(RuleParser.RulesContext ctx) {

    return ctx.ruleList().ruleCollection()
        .stream().map(this::ruleCollectionDefinition)
        .collect(Collectors.toList());
  }

  public RuleCollection ruleCollectionDefinition(RuleParser.RuleCollectionContext ctx) {

    String name = ctx.qualifiedName().getText();
    List<Rule> rules = ctx.ruleDeclaration()
        .stream().map(this::ruleDefinition).collect(Collectors.toList());

    return new RuleCollection(name, rules);
  }

  public Rule ruleDefinition(RuleParser.RuleDeclarationContext ctx) {

    if (Objects.nonNull(ctx.entryDeclaration())) {
      return entryRuleDefinition(ctx.entryDeclaration());
    } else if (Objects.nonNull(ctx.sinkDeclaration())) {
      return sinkRuleDefinition(ctx.sinkDeclaration());
    }

    throw new RuntimeException("Unexpected rule context found");
  }

  public EntryRule entryRuleDefinition(RuleParser.EntryDeclarationContext ctx) {

    return new EntryRule();
  }

  public SinkRule sinkRuleDefinition(RuleParser.SinkDeclarationContext ctx) {
    return new SinkRule();
  }
}
