package io.steviemul.slalom.store;

public interface Store<K, V> {

  boolean contains(K key);

  V get(K key);

  V put(K key, V value);

  V remove(K key);

  void clear();
}
