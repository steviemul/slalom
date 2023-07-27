package io.steviemul.slalom.rules;

import io.steviemul.slalom.rules.model.FlowRule;
import io.steviemul.slalom.rules.model.PatternRule;
import io.steviemul.slalom.rules.model.Rule;
import lombok.Getter;

@Getter
public class RuleInterpreterVisitor extends RuleBaseVisitor<Rule> {

  private Rule rule;

  @Override
  public Rule visitRule(RuleParser.RuleContext ctx) {
    Rule rule = new Rule();

    if (ctx.patternRule() != null) {
      rule = fromContext(ctx.patternRule());
    } else if (ctx.flowRule() != null) {
      rule = fromContext(ctx.flowRule());
    }

    return rule;
  }

  private Rule fromContext(RuleParser.PatternRuleContext ctx) {
    return new PatternRule();
  }

  private Rule fromContext(RuleParser.FlowRuleContext ctx) {
    return new FlowRule();
  }
}
