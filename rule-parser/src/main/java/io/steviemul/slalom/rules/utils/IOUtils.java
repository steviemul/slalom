package io.steviemul.slalom.rules.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class IOUtils {

  public static String readResource(String resourcePath) throws Exception {
    Path path =
        Paths.get(
            Objects.requireNonNull(IOUtils.class.getClassLoader().getResource(resourcePath))
                .toURI());

    return Files.readString(path);
  }

  public static String readFile(String path) throws Exception {
    return Files.readString(Path.of(path));
  }

  public static void write(OutputStream out, String contents) throws IOException {
    out.write(contents.getBytes(StandardCharsets.UTF_8));
  }

  private IOUtils() {}
}
