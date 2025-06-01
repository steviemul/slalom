package io.steviemul.slalom.cli.commands;

import io.steviemul.slalom.analyser.Analyser;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@CommandLine.Command(name = "analyse", description = "Analyses a file or directory")
@Slf4j
public class Analyse implements Callable<Integer> {

  @CommandLine.Parameters(paramLabel = "<file>", description = "The path to analyse")
  private String fileLocation;

  @CommandLine.Option(
      names = {"-l", "--cache-limit"},
      defaultValue = "70",
      description = "The percentage of heap size to allocate to the in-memory cache")
  private Integer cachePercentageLimit;

  @CommandLine.Option(
      names = {"-w", "--disk-weight"},
      defaultValue = "2",
      description = "The size of the disk cache")
  private Integer diskWeight;

  @CommandLine.Option(
      names = {"-n", "--cache-name"},
      description = "Name of the offline cache",
      defaultValue = ".ast")
  private String cacheName;

  @Override
  public Integer call() throws Exception {

    Analyser analyser = createAnalyser();

    analyser.analyze(fileLocation);

    return 0;
  }

  private Analyser createAnalyser() {
    return new Analyser(cachePercentageLimit, diskWeight, cacheName);
  }
}
