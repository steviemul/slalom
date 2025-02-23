package io.steviemul.slalom.rules.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RuleCollection {

  private final String name;
  private final List<Rule> rules;

  private Map<String, String> meta = new HashMap<>();

  public void addMeta(String key, String value) {
    meta.put(key, value);
  }
}
