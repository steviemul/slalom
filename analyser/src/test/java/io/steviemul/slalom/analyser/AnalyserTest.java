package io.steviemul.slalom.analyser;

import io.steviemul.slalom.utils.FileUtils;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AnalyserTest {

  @Test
  void basic_test() {

    Path exampleDirectory = FileUtils.findModuleDirectory("slalom", "example");
    Path examplesPath = Path.of(exampleDirectory.toString(), "src", "main", "java");

    Analyser analyser = new Analyser(".unit-tests");

    analyser.analyze(examplesPath.toString());
  }
}
