package io.steviemul.slalom;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheTest {

  private static final String OFFLINE_LOCATION = ".unit-tests-cache";

  @Test
  void test_basic_cache() {

    int capacity = 1000;

    Cache cache = new Cache(capacity, OFFLINE_LOCATION);

    for (int i = 0; i < capacity; i++) {
      String key = "key" + i;
      String value = "value" + i;

      cache.put(key, value);

      Object storedValue = cache.get(key);

      assertEquals(value, storedValue);
    }

    for (int i = capacity; i < (capacity * 2); i++) {
      String key = "key" + i;
      String value = "value" + i;

      cache.put(key, value);

      Object storedValue = cache.get(key);

      assertEquals(value, storedValue);
    }

    cache.clear();
  }
}