package io.steviemul.slalom.rules;

import io.steviemul.slalom.rules.model.Rule;
import io.steviemul.slalom.rules.utils.IOUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RuleInterpreterTest {

  private static final String SAMPLE_RULE_PATH = "Sample.rule";

  @Test
  void ruleInterpreter_parsesRule() throws Exception {

    // Given
    RuleInterpreter interpreter = new RuleInterpreter();

    String ruleContents = IOUtils.readResource(SAMPLE_RULE_PATH);

    // When
    Rule rule = interpreter.parse(ruleContents);

    // Then
    assertNotNull(rule);
  }
}