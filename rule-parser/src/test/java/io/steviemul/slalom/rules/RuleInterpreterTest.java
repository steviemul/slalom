package io.steviemul.slalom.rules;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.steviemul.slalom.rules.model.RuleCollection;
import io.steviemul.slalom.rules.utils.IOUtils;
import java.util.List;
import org.junit.jupiter.api.Test;

class RuleInterpreterTest {

  private static final String SECURITY_RULES = "security.rule";
  private static final String ENTRYPOINTS = "entrypoints.rule";

  @Test
  void ruleInterpreter_parsesEntryPoints() throws Exception {

    // Given
    RuleInterpreter interpreter = new RuleInterpreter(ENTRYPOINTS);

    String ruleContents = IOUtils.readResource(ENTRYPOINTS);

    // When
    List<RuleCollection> rules = interpreter.parse(ruleContents);

    // Then
    assertNotNull(rules);
  }

  @Test
  void ruleInterpreter_parsesSecurityRules() throws Exception {

    // Given
    RuleInterpreter interpreter = new RuleInterpreter(SECURITY_RULES);

    String ruleContents = IOUtils.readResource(SECURITY_RULES);

    // When
    List<RuleCollection> rules = interpreter.parse(ruleContents);

    // Then
    assertNotNull(rules);
  }
}
