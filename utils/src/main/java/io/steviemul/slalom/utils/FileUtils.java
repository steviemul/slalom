package io.steviemul.slalom.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

  private FileUtils() {
  }

  public static Path findModuleDirectory(String moduleParent, String moduleName) {
    return findModuleDirectory(new File("").getAbsoluteFile(), moduleParent, moduleName);
  }

  public static Path findModuleDirectory(File directory, String moduleParent, String moduleName) {

    if (moduleParent.equals(directory.getName())) {
      for (File child : directory.listFiles()) {
        if (child.isDirectory() && moduleName.equals(child.getName())) {
          return child.toPath();
        }
      }
    }

    return findModuleDirectory(directory.getParentFile(), moduleParent, moduleName);
  }
  
}
