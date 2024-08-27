package io.steviemul.slalom.analyser;

import io.steviemul.slalom.store.Store;
import io.steviemul.slalom.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class AnalyserTest {

  private Store store = new Store("unit-tests");

  @BeforeEach
  void setup() throws Exception {
    store.start();
  }

  @Test
  void basic_test() {

    Path exampleDirectory = FileUtils.findModuleDirectory("slalom", "example");
    Path examplesPath = Path.of(exampleDirectory.toString(),
        "src", "main", "java");

    Analyser analyser = new Analyser(store);

    analyser.analyze(examplesPath.toString());

  }

  @Test
  void webgoat_test() {

    Analyser analyser = new Analyser(store);

    analyser.analyze("/Users/stephenmulrennan/dev/git/WebGoat");
  }

  @AfterEach
  void teardown() {

    store.delete();
  }
}