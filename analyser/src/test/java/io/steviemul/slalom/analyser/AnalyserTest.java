package io.steviemul.slalom.analyser;

import io.steviemul.slalom.Cache;
import io.steviemul.slalom.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class AnalyserTest {

  private final Cache<String, byte[]> cache = new Cache<>();

  @Test
  void basic_test() {

    Path exampleDirectory = FileUtils.findModuleDirectory("slalom", "example");
    Path examplesPath = Path.of(exampleDirectory.toString(),
        "src", "main", "java");

    Analyser analyser = new Analyser(cache);

    analyser.analyze(examplesPath.toString());

  }

  @Test
  void webgoat_test() {

    Analyser analyser = new Analyser(cache);

    analyser.analyze("/Users/stephenmulrennan/dev/git/WebGoat");
  }

  @AfterEach
  void teardown() {
    cache.clear();
  }
}