package io.steviemul.slalom.store;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static io.steviemul.slalom.store.Utils.deleteDirectory;

@Slf4j
public class KeyValueStore<K, V> implements Store<K, V> {

  private static final String BASE_ROOT_FOLDER = ".cache";
  private static final String INDEX_DELIM = ":";
  private static final String LOG_DELIM = "!";
  private static final String RW_MODE = "rw";
  private static final String NEWLINE = "\n";
  private static final String OPERATION_PUT = "PUT";
  private static final String OPERATION_REMOVE = "REMOVE";

  private static final String DATA_FILE = "data.db";
  private static final String INDEX_FILE = "index.db";
  private static final String LOG_FILE = "ops.log";
  private static final String TEMP_DATA_FILE = "temp_data.db";

  private static final int MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

  private final File root;
  private final RandomAccessFile dataFile;
  private final FileChannel dataChannel;

  private final Map<K, Long> index = new ConcurrentHashMap<>();
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final ScheduledExecutorService compactionScheduler = Executors.newSingleThreadScheduledExecutor();
  private final BufferedWriter logWriter;
  private final boolean durable;

  private MappedByteBuffer dataBuffer;

  public KeyValueStore(String name) throws StoreException {
    this(name, true);
  }

  public KeyValueStore(String name, boolean durable) throws StoreException {

    try {
      this.durable = durable;
      this.root = initRoot(name);
      this.dataFile = new RandomAccessFile(new File(root, DATA_FILE), RW_MODE);

      log.info("Data file initialized [location={}]", DATA_FILE);

      this.dataChannel = dataFile.getChannel();
      this.dataBuffer = dataChannel.map(FileChannel.MapMode.READ_WRITE, 0, MAX_FILE_SIZE);
      this.logWriter = new BufferedWriter(new FileWriter(new File(root, LOG_FILE), true));

      loadIndex();
      recoverFromLog();
      startBackgroundCompaction();
    } catch (IOException e) {
      throw new StoreException("Unable to create key value store", e);
    }
  }

  @Override
  public boolean contains(K key) {
    return index.containsKey(key);
  }

  @Override
  @SuppressWarnings("unchecked")
  public V get(K key) {
    lock.readLock().lock();

    try {
      Long position = index.get(key);

      if (position == null)
        return null;

      int savedPosition = dataBuffer.position();
      dataBuffer.position(position.intValue());

      byte[] keyBytes = getCurrentBufferValue();
      byte[] valueBytes = getCurrentBufferValue();

      dataBuffer.position(savedPosition);

      return (V) bytesToObject(valueBytes);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public V put(K key, V value) throws StoreException {
    return put(key, value, true);
  }

  private V put(K key, V value, boolean addToLog) throws StoreException {
    lock.writeLock().lock();

    try {
      long position = dataBuffer.position();
      byte[] keyBytes = objectToBytes(key);
      byte[] valueBytes = objectToBytes(value);

      if (position + keyBytes.length + valueBytes.length + 10 > MAX_FILE_SIZE) {
        log.error("Data file is full, consider compacting or increasing size");
        return null;
      }

      // TODO deserializing twice here
      if (addToLog) {
        addToLog(OPERATION_PUT, key, value);
      }

      putInBuffer(keyBytes);
      putInBuffer(valueBytes);

      index.put(key, position);
      saveIndex();

      return value;
    } catch (IOException e) {
      throw new StoreException("Unable to put object", e);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public V remove(K key) throws StoreException {
    lock.writeLock().lock();

    try {
      V value = get(key);

      if (value != null && index.remove(key) != null) {
        addToLog(OPERATION_REMOVE, key, null);
        saveIndex();
      }

      return value;
    } catch (IOException e) {
      throw new StoreException("Unable to remove object", e);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void clear() {
    deleteDirectory(root);
  }

  private byte[] getCurrentBufferValue() {
    int size = dataBuffer.getInt();
    byte[] bytes = new byte[size];
    dataBuffer.get(bytes);

    return bytes;
  }

  private void putInBuffer(byte[] bytes) {
    dataBuffer.putInt(bytes.length);
    dataBuffer.put(bytes);
  }

  private File initRoot(String name) {
    File root = new File(BASE_ROOT_FOLDER, name);

    if (root.exists() && root.isFile()) {
      throw new IllegalArgumentException("Invalid root location");
    }

    root.mkdirs();

    log.info("Root initialized [location={}]", root.getAbsolutePath());

    return root;
  }

  @SuppressWarnings("unchecked")
  private void loadIndex() throws IOException {
    File file = new File(root, INDEX_FILE);

    if (!file.exists())
      return;

    int count = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;

      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(INDEX_DELIM);
        if (parts.length == 2) {
          index.put((K) base64StringToObject(parts[0]), Long.parseLong(parts[1]));
          count++;
        }
      }
    }

    log.info("Index loaded [numberOfEntries={}]", count);
  }

  private void saveIndex() throws IOException {

    if (!durable)
      return;

    File indexFile = new File(root, INDEX_FILE);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile))) {
      int count = 0;
      for (Map.Entry<K, Long> entry : index.entrySet()) {
        writer.write(objectToBase64String(entry.getKey()) + INDEX_DELIM + entry.getValue() + NEWLINE);
        count++;
      }

      log.info("Index Saved [numberOfEntries={}]", count);
    }
  }

  private void addToLog(String operation, K key, V value) throws IOException {

    if (!durable)
      return;

    synchronized (logWriter) {
      String keyAsString = objectToBase64String(key);
      String valueAsString = value != null ? objectToBase64String(value) : "";

      logWriter.write(operation + LOG_DELIM + keyAsString + LOG_DELIM + valueAsString + NEWLINE);
      logWriter.flush();
    }
  }

  @SuppressWarnings("unchecked")
  private void recoverFromLog() throws IOException, StoreException {

    File logFile = new File(root, LOG_FILE);

    if (!logFile.exists())
      return;

    try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
      String line;
      int puts = 0, removes = 0;

      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(LOG_DELIM, 3);

        if (parts.length < 2)
          continue;

        String op = parts[0];
        K key = (K) base64StringToObject(parts[1]);
        V value = (V) (parts.length == 3 ? base64StringToObject(parts[2]) : null);

        if (OPERATION_PUT.equals(op)) {
          puts++;
          put(key, value, false);
        } else if (OPERATION_REMOVE.equals(op)) {
          removes++;
          remove(key);
        }
      }

      log.info("Recovered from log file[puts={}, removes={}]", puts, removes);
    }

    new FileOutputStream(new File(root, LOG_FILE)).close();
  }

  private void startBackgroundCompaction() {

    compactionScheduler.scheduleAtFixedRate(() -> {
      try {
        compactDataFile();
      } catch (Exception e) {
        log.error("Error during compaction", e);
      }
    }, 60, 60, TimeUnit.SECONDS);

    log.info("Compaction process initialized in background");
  }


  private void compactDataFile() throws IOException {
    lock.writeLock().lock();

    try {
      File tempFile = new File(root, TEMP_DATA_FILE);

      try (RandomAccessFile tempRaf = new RandomAccessFile(tempFile, RW_MODE)) {
        FileChannel tempChannel = tempRaf.getChannel();

        MappedByteBuffer newBuffer = tempChannel.map(FileChannel.MapMode.READ_WRITE, 0, MAX_FILE_SIZE);
        Map<K, Long> newIndex = new HashMap<>();

        long newPosition = 0;

        for (Map.Entry<K, Long> entry : index.entrySet()) {
          dataBuffer.position(entry.getValue().intValue());

          int keySize = dataBuffer.getInt();
          byte[] keyBytes = new byte[keySize];
          dataBuffer.get(keyBytes);

          int valueSize = dataBuffer.getInt();
          byte[] valueBytes = new byte[valueSize];
          dataBuffer.get(valueBytes);

          newBuffer.position((int) newPosition);
          newBuffer.putInt(keySize);
          newBuffer.put(keyBytes);
          newBuffer.putInt(valueSize);
          newBuffer.put(valueBytes);

          newIndex.put(entry.getKey(), newPosition);
          newPosition = newBuffer.position();
        }

        dataChannel.close();
        tempChannel.close();
        Files.deleteIfExists(new File(root, DATA_FILE).toPath());
        tempFile.renameTo(new File(root, DATA_FILE));

        this.dataBuffer = dataChannel.map(FileChannel.MapMode.READ_WRITE, 0, MAX_FILE_SIZE);
        index.clear();
        index.putAll(newIndex);
        saveIndex();

        log.info("Compaction complete");
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  public void close() throws IOException {
    dataChannel.close();
    logWriter.close();
    compactionScheduler.shutdown();
  }

  private String encodeBytes(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  private byte[] decodeBytes(String string) {
    return Base64.getDecoder().decode(string);
  }

  private String objectToBase64String(Object obj) {
    return encodeBytes(objectToBytes(obj));
  }

  private Object base64StringToObject(String string) {
    return bytesToObject(decodeBytes(string));
  }

  private byte[] objectToBytes(Object obj) {

    try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
      try (ObjectOutputStream oOut = new ObjectOutputStream(bOut)) {
        oOut.writeObject(obj);
        oOut.flush();
      }

      bOut.close();
      return bOut.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Unable to serialize object", e);
    }
  }

  private Object bytesToObject(byte[] objectBytes) {
    try (ByteArrayInputStream bIn = new ByteArrayInputStream(objectBytes)) {
      try (ObjectInputStream oIn = new ObjectInputStream(bIn)) {
        return oIn.readObject();
      }
    } catch (Exception e) {
      throw new RuntimeException("Unable to deserialize object", e);
    }
  }
}
