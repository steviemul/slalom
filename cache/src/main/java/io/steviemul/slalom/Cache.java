package io.steviemul.slalom;

import io.steviemul.slalom.store.LRUStore;
import io.steviemul.slalom.event.LRUMapListener;
import io.steviemul.slalom.store.OfflineStore;
import io.steviemul.slalom.store.NoopStore;
import io.steviemul.slalom.store.Store;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class Cache<K, V> implements LRUMapListener<K, V>, Store<K, V> {

  private final LRUStore<K, V> memoryStore;
  private final Store<K, V> backingStore;

  public Cache() {
    memoryStore = new LRUStore<>();
    backingStore = new NoopStore<>();
  }

  public Cache(int maxMemoryObjects, String offlineLocation) {
    memoryStore = new LRUStore<>(maxMemoryObjects);
    memoryStore.addEventListener(this);

    log.info("Memory store initialized with capacity [capacity={}]", maxMemoryObjects);

    backingStore = new OfflineStore<>(offlineLocation);

    log.info("Backing store initialized with location [location={}]", offlineLocation);
  }

  @Override
  public boolean contains(K key) {
    return memoryStore.containsKey(key)
        || backingStore.contains(key);
  }

  @Override
  public V get(K key) {
    if (memoryStore.containsKey(key)) {
      log.info("Memory store hit [key={}]", key);

      return memoryStore.get(key);
    }

    V value = backingStore.remove(key);

    if (value != null) {
      log.info("Backing store hit [key={}]", key);

      memoryStore.put(key, value);
    }

    return value;
  }

  @Override
  public V put(K key, V value) {
    return memoryStore.put(key, value);
  }

  @Override
  public V remove(K key) {
    return memoryStore.remove(key);
  }

  @Override
  public void clear() {
    memoryStore.clear();
    backingStore.clear();
  }

  @Override
  public void objectEvicted(Map.Entry<K, V> entry) {
    backingStore.put(entry.getKey(), entry.getValue());

    log.info("Object evicted from memory store and persisted to backing store [key={}]", entry.getKey());
  }
}
