package io.steviemul.slalom.utils;

public class MemoryUtils {

  private static final int KB = 1024;
  private static final int MB = KB * KB;

  private MemoryUtils() {
  }

  public static long getMaxHeapSizePercentage(int percentage) {
    long bytesPercentage = (Runtime.getRuntime().maxMemory() / 100) * percentage;

    return bytesPercentage / MB;
  }
}
