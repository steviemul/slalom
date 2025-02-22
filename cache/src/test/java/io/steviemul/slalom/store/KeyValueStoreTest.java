package io.steviemul.slalom.store;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyValueStoreTest {

  @Test
  void basic_test() throws Exception {

    KeyValueStore<String, Person> store = new KeyValueStore<>(".unit-tests");

    Person bob = new Person("Bob", "Test", 30);
    Person alice = new Person("Alice", "Test", 25);

    store.put("bob", bob);
    store.put("alice", alice);

    Person actualBob = store.get("bob");
    Person actualAlice = store.get("alice");

    assertEquals(bob, actualBob);
    assertEquals(alice, actualAlice);
  }
  
  record Person(String firstName, String surname, int age) implements Serializable {
  }
}