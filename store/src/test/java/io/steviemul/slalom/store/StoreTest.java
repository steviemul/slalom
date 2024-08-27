package io.steviemul.slalom.store;

import com.couchbase.lite.Collection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StoreTest {

  @Test
  void store_starts() throws Exception {

    Store store = new Store("unit-tests");

    store.start();

    Collection collection = store.getCollection("tests");
    
    assertNotNull(collection);
  }
}