package io.steviemul.slalom;

import io.steviemul.slalom.cache.ObjectStore;
import io.steviemul.slalom.gui.Visualizer;
import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.parser.Parser;
import io.steviemul.slalom.resolver.TypeResolver;
import io.steviemul.slalom.serializer.ASTRootSerializer;
import io.steviemul.slalom.utils.IOUtils;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  private static final String AST_CACHE = ".ast_cache";

  public static void main(String[] args) {

    try {
      String path = args[0];
      String source = IOUtils.readFile(path);

      Visualizer visualizer = new Visualizer();

      visualizer.visualize(source);

      Parser parser = new Parser();

      Date start = new Date();

      ASTRoot astRoot = parser.parse(path, source);

      astRoot.path(path);

      log.info("Parsed [{}], took {}ms", path, new Date().getTime() - start.getTime());

      TypeResolver.addCompilationUnit(astRoot);

      ObjectStore<String, byte[]> astCache =
          new ObjectStore<>(1000, 2, AST_CACHE, String.class, byte[].class);

      String astJson = ASTRootSerializer.toJson(astRoot);

      astCache.put(astRoot.sha(), astJson.getBytes(StandardCharsets.UTF_8));

    } catch (Exception e) {
      log.error("Error parsing source", e);
    }
  }
}
