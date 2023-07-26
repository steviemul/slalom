package io.steviemul.slalom.parser;

import io.steviemul.slalom.model.java.Ref;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

@Slf4j
public class ParseContext {

  private final Stack<Ref> context = new Stack<>();

  public Ref peek() {
    return context.peek();
  }

  public Ref push(Ref ref) {
    return context.push(ref);
  }

  public Ref pop() {
    return context.pop();
  }

  public <T> T popRequiredType(Class<T> expectedType) {
    T contextItem = requireType(expectedType);
    pop();
    return contextItem;
  }

  public <T> Optional<T> popRequestedType(Class<T> expectedType) {

    Optional<T> item = requestType(expectedType);

    if (item.isPresent()) {
      return Optional.of(popRequiredType(expectedType));
    }

    return Optional.empty();
  }

  public <T> T requireType(Class<T> expectedType) {
    try {
      return expectedType.cast(context.peek());
    } catch (ClassCastException e) {
      String msg =
          String.format(
              "Context Type Mismatch [%s, %s]",
              expectedType.getName(), context.peek().getClass().getName());

      log.error("Illegal State. Context is [{}]", currentState());

      throw new IllegalStateException(msg);
    }
  }

  public <T> Optional<T> requestType(Class<T> expectedType) {
    try {
      return Optional.of(expectedType.cast(context.peek()));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private String currentState() {

    return "Context - \n"
        + context.stream().map(Ref::toString).collect(Collectors.joining("\n -->"));
  }
}
