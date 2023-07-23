package io.steviemul.slalom.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class IOUtils {

  public static String readResource(String resourcePath) throws Exception {
    Path path = Paths.get(Objects.requireNonNull(IOUtils.class.getClassLoader()
        .getResource(resourcePath)).toURI());

    return Files.readString(path);
  }

  private IOUtils() {}
}
