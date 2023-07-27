package io.steviemul.slalom.analyser;

import java.util.HashMap;
import java.util.Map;

public class Scope {

  private Map<String, Object> environment = new HashMap<>();
  private final Scope parent;

  public static Scope newScope(Scope parent) {
    return new Scope(parent);
  }

  public Object get(String key) {
    if (environment.containsKey(key)) {
      return environment.get(key);
    } else if (parent != null) {
      return parent.get(key);
    }

    return null;
  }

  public void put(String key, Object value) {
    environment.put(key, value);
  }

  public Scope() {
    this(null);
  }

  private Scope(Scope parent) {
    this.parent = parent;
  }
}