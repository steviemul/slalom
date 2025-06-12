package io.steviemul.slalom.cli;

import io.steviemul.slalom.cli.commands.Analyse;
import io.steviemul.slalom.cli.commands.Parse;
import io.steviemul.slalom.cli.commands.Visualize;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@CommandLine.Command(
    name = "slalom-cli",
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "Slalom CLI",
    subcommands = {Parse.class, Analyse.class, Visualize.class})
@Slf4j
public class Cli {

  public static void main(String... args) {
    int exitCode = new CommandLine(new Cli()).execute(args);
    System.exit(exitCode);
  }
}
