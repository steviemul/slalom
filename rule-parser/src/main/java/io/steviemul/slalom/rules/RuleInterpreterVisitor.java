package io.steviemul.slalom.rules;

import io.steviemul.slalom.rules.model.Argument;
import io.steviemul.slalom.rules.model.EntryRule;
import io.steviemul.slalom.rules.model.Rule;
import io.steviemul.slalom.rules.model.RuleCollection;
import io.steviemul.slalom.rules.model.SinkRule;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class RuleInterpreterVisitor extends RuleParserBaseVisitor<List<RuleCollection>> {

  @Override
  public List<RuleCollection> visitRules(RuleParser.RulesContext ctx) {

    return ctx.ruleList().ruleCollection().stream()
        .map(this::ruleCollectionDefinition)
        .collect(Collectors.toList());
  }

  public RuleCollection ruleCollectionDefinition(RuleParser.RuleCollectionContext ctx) {

    String name = ctx.qualifiedName().getText();
    List<Rule> rules =
        ctx.ruleDeclaration().stream().map(this::ruleDefinition).collect(Collectors.toList());

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

    EntryRule entryRule = new EntryRule();

    entryRule.name(ctx.qualifiedName().getText());

    return entryRule;
  }

  public SinkRule sinkRuleDefinition(RuleParser.SinkDeclarationContext ctx) {

    return new SinkRule();
  }

  private static Argument fromContext(RuleParser.ArgDefinitionContext ctx) {
    return null;
  }
}
