package io.steviemul.slalom.cli.commands;

import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.parser.Parser;
import io.steviemul.slalom.serializer.ASTRootSerializer;
import io.steviemul.slalom.utils.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@CommandLine.Command(
    name = "parse",
    description = "Parses a java source file and outputs in parsed format")
@Slf4j
public class Parse implements Callable<Integer> {

  public static final String FORMAT_JSON = "json";
  public static final String FORMAT_YAML = "yaml";

  @CommandLine.Spec private CommandLine.Model.CommandSpec spec;

  @CommandLine.Parameters(index = "0", description = "The Java source file to parse")
  private File file;

  @CommandLine.Option(
      names = {"-f", "--format"},
      description = "json, yaml, bin")
  private String format = "json";

  @CommandLine.Option(
      names = {"-o", "--output"},
      description = "json, yaml")
  private String outputPath;

  @Override
  public Integer call() {

    try {
      String source = Files.readString(file.toPath());

      Parser parser = new Parser();

      ASTRoot astRoot = parser.parse(file.getAbsolutePath(), source);

      switch (format) {
        case FORMAT_JSON:
          writeOut(ASTRootSerializer.toJson(astRoot));
          break;
        case FORMAT_YAML:
          writeOut(ASTRootSerializer.toYAML(astRoot));
          break;
        default:
          throw new CommandLine.ParameterException(
              spec.commandLine(), String.format("Invalid value '%s' specified for format", format));
      }

    } catch (Exception e) {
      log.error("Error parsing source file [{}]", file.getPath(), e);
      return 1;
    }

    return 0;
  }

  private void writeOut(String output) throws IOException {
    if (outputPath != null && outputPath.length() > 0) {
      try (OutputStream out = new FileOutputStream(outputPath)) {
        IOUtils.write(out, output);
      }
    } else {
      System.out.println(output);
    }
  }
}
