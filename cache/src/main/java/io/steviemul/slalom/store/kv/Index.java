package io.steviemul.slalom.store.kv;

import static io.steviemul.slalom.store.Utils.base64StringToObject;
import static io.steviemul.slalom.store.Utils.objectToBase64String;

import io.steviemul.slalom.store.StoreException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Index<K, V> {

  private static final String FILENAME = "index.db";
  private static final String INDEX_DELIM = ":";
  private static final String NEWLINE = "\n";

  private final ScheduledExecutorService flushScheduler =
      Executors.newSingleThreadScheduledExecutor();

  private final Map<K, V> index = new ConcurrentHashMap<>();
  private final Function<String, V> valueConverter;

  private final File indexFile;
  private boolean dirty = false;

  public Index(File root, Function<String, V> valueConverter) throws StoreException {
    this.indexFile = new File(root, FILENAME);
    this.valueConverter = valueConverter;

    loadIndex();
    startFlushScheduler();
    registerShutdownFlushHook();
  }

  public boolean contains(K key) {
    return index.containsKey(key);
  }

  public V get(K key) {
    return index.get(key);
  }

  public void put(K key, V value) {
    index.put(key, value);
    dirty = true;
  }

  public V remove(K key) {
    return index.remove(key);
  }

  public Set<Map.Entry<K, V>> entrySet() {
    return index.entrySet();
  }

  public void refreshWith(Map<? extends K, ? extends V> m) {
    clear();
    putAll(m);
  }

  public void clear() {
    index.clear();
  }

  public void putAll(Map<? extends K, ? extends V> m) {
    index.putAll(m);
  }

  public void close() {
    flushScheduler.shutdown();
  }

  @SuppressWarnings("unchecked")
  private void loadIndex() {

    if (!indexFile.exists()) return;

    int count = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(this.indexFile))) {
      String line;

      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(INDEX_DELIM);
        if (parts.length == 2) {
          index.put((K) base64StringToObject(parts[0]), valueConverter.apply(parts[1]));
          count++;
        }
      }
    } catch (IOException e) {
      throw new StoreException("Unable to load index", e);
    }

    log.info("Index loaded [numberOfEntries={}]", count);
  }

  private void saveIndex() {

    if (!dirty) return;

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile))) {
      int count = 0;
      for (Map.Entry<K, V> entry : index.entrySet()) {
        writer.write(
            objectToBase64String(entry.getKey()) + INDEX_DELIM + entry.getValue() + NEWLINE);
        count++;
      }

      log.info("Index Saved [numberOfEntries={}]", count);
    } catch (IOException e) {
      log.error("Error flushing index", e);
    }

    dirty = false;
  }

  private void startFlushScheduler() {
    flushScheduler.scheduleAtFixedRate(this::saveIndex, 5, 5, TimeUnit.SECONDS);
  }

  private void registerShutdownFlushHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::saveIndex));
  }
}
