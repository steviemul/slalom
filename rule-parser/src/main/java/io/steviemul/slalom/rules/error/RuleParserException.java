package io.steviemul.slalom.rules.error;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Builder
@ToString
public class RuleParserException extends RuntimeException {
  private final String sourceName;
  private final int line;
  private final int charPositionInLine;
  private final String message;
}
