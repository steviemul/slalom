package io.steviemul.slalom.cache;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

@Slf4j
public class ObjectStore<K, V> {

  private static final String CACHE_ROOT = ".cache";
  private static final int MAX_DISK_CACHE_SIZE_MB = 1024;

  private final Cache<K, V> internalCache;

  public ObjectStore(String name, Class<K> keyType, Class<V> valueType) {
    this.internalCache = createInternalCache(-1, -1, name, keyType, valueType);
  }

  public ObjectStore(
      long maxMemoryObjects, int diskWeight, String name, Class<K> keyType, Class<V> valueType) {
    this.internalCache =
        createInternalCache(maxMemoryObjects, diskWeight, name, keyType, valueType);
  }

  public V get(K key) {
    return internalCache.get(key);
  }

  public V put(K key, V value) {
    internalCache.put(key, value);
    return value;
  }

  public void clear() {
    internalCache.clear();
  }

  public boolean containsKey(K key) {
    return internalCache.containsKey(key);
  }

  private Cache<K, V> createInternalCache(
      long maxMemoryObjects, int diskWeight, String name, Class<K> keyType, Class<V> valueType) {

    if (maxMemoryObjects <= 0) {
      return createHeapCache(name, keyType, valueType);
    }

    return createDiskBackedCache(maxMemoryObjects, diskWeight, name, keyType, valueType);
  }

  private Cache<K, V> createHeapCache(String name, Class<K> keyType, Class<V> valueType) {

    CacheManager cacheManager =
        CacheManagerBuilder.newCacheManagerBuilder()
            .withCache(
                name,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    keyType, valueType, ResourcePoolsBuilder.heap(Integer.MAX_VALUE)))
            .build(true);

    log.info(
        "Initialized memory cache with options [name={}, keyType={}, valueType={}]",
        name,
        keyType,
        valueType);

    closeCacheManagerOnShutdown(cacheManager);

    return cacheManager.getCache(name, keyType, valueType);
  }

  private Cache<K, V> createDiskBackedCache(
      long maxMemorySize, int diskWeight, String name, Class<K> keyType, Class<V> valueType) {

    String cacheLocation = CACHE_ROOT + File.separator + name;

    PersistentCacheManager persistentCacheManager =
        CacheManagerBuilder.newCacheManagerBuilder()
            .with(CacheManagerBuilder.persistence(cacheLocation))
            .withCache(
                name,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    keyType,
                    valueType,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(1000, EntryUnit.ENTRIES)
                        .disk(MAX_DISK_CACHE_SIZE_MB, MemoryUnit.MB, true)))
            .build(true);

    log.info(
        "Initialized disk backed cache with options [maxMemorySize={}MB, name={}, keyType={}, valueType={}]",
        maxMemorySize,
        name,
        keyType,
        valueType);

    closeCacheManagerOnShutdown(persistentCacheManager);

    return persistentCacheManager.getCache(name, keyType, valueType);
  }

  private void closeCacheManagerOnShutdown(CacheManager cacheManager) {
    Runtime.getRuntime().addShutdownHook(new Thread(cacheManager::close));
  }
}
