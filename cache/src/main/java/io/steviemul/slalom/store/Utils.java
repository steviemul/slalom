package io.steviemul.slalom.store;

import java.io.File;

public class Utils {

  private Utils() {
  }

  public static void deleteDirectory(File directory) {

    File[] children = directory.listFiles();

    for (File child : children) {
      if (child.isFile()) {
        child.delete();
      } else {
        deleteDirectory(child);
      }
    }

    directory.delete();
  }
}
