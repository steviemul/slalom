package io.steviemul.slalom.rules.error;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

@Slf4j
public class DefaultErrorListener extends BaseErrorListener {

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
                          Object offendingSymbol,
                          int line,
                          int charPositionInLine,
                          String msg,
                          RecognitionException e) {

    String sourceName = recognizer.getInputStream().getSourceName();

    if (!sourceName.isEmpty()) {
      sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
    }

    log.error("{} line {}:{} {}", sourceName, line, charPositionInLine, msg);

    throw RuleParserException.builder()
        .sourceName(sourceName)
        .line(line)
        .charPositionInLine(charPositionInLine)
        .message(msg)
        .build();
  }
}
