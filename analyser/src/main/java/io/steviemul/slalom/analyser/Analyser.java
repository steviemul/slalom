package io.steviemul.slalom.analyser;

import static io.steviemul.slalom.utils.MemoryUtils.getMaxHeapSizePercentage;

import io.steviemul.slalom.cache.ObjectStore;
import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.model.java.PackageDeclaration;
import io.steviemul.slalom.parser.Parser;
import io.steviemul.slalom.serializer.ASTRootSerializer;
import io.steviemul.slalom.utils.HashUtils;
import io.steviemul.slalom.utils.IOUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Analyser {

  private static final String UNKNOWN = "UNKNOWN";
  private static final String JAVA = "java";

  private Set<String> shas = new HashSet<>();

  private final ObjectStore<String, byte[]> cache;

  public Analyser(String cacheName) {
    cache = new ObjectStore<>(cacheName, String.class, byte[].class);
  }

  public Analyser(int cachePercentageLimit, int diskWeight, String cacheName) {
    long cacheSizeLimit = getMaxHeapSizePercentage(cachePercentageLimit);

    cache = new ObjectStore<>(cacheSizeLimit, diskWeight, cacheName, String.class, byte[].class);
  }

  public void analyze(String path) {
    File file = new File(path);

    if (!file.exists()) {
      throw new IllegalArgumentException("Specified path does not exist");
    }

    if (file.isFile()) {
      parseSingleFile(path);
    } else if (file.isDirectory()) {
      parseFilesInFolder(path);
    }

    link();
  }

  private void link() {

    for (String sha : shas) {
      try {
        byte[] contents = cache.get(sha);

        ASTRoot ast = ASTRootSerializer.fromJsonBytes(contents);

        System.out.println(ast);
      } catch (Exception e) {
        log.error("Error loading class definition from cache", e);
      }
    }
  }

  private void parseSingleFile(String path) {

    Parser parser = new Parser();
    ASTRoot astRoot;

    try {
      if (path.endsWith("." + JAVA)) {
        String source = IOUtils.readFile(path);
        String sha = HashUtils.sha(source);

        if (cache.containsKey(sha)) {
          byte[] contents = cache.get(sha);

          astRoot = ASTRootSerializer.fromJsonBytes(contents);

          log.info(
              "AST Successfully loaded from store [{}, {}]",
              getPackageName(astRoot),
              astRoot.typeDeclaration().name());

        } else {
          astRoot = parser.parse(path, source);

          log.info(
              "AST Successfully parsed from source [{}, {}]",
              getPackageName(astRoot),
              astRoot.typeDeclaration().name());
        }

        byte[] astJsonBytes = ASTRootSerializer.toJsonBytes(astRoot);

        cache.put(astRoot.sha(), astJsonBytes);
        shas.add(astRoot.sha());
      }
    } catch (Exception e) {
      log.error("Error parsing file [{}]", path, e);
    }
  }

  private void parseFilesInFolder(String path) {

    File directory = new File(path);

    if (!directory.exists() || !directory.isDirectory()) {
      throw new IllegalArgumentException("Path specified must exist and be a directory");
    }

    parseFilesInFolder(directory);
  }

  private void parseFilesInFolder(File directory) {

    for (File child : Objects.requireNonNullElse(directory.listFiles(), new File[0])) {
      if (child.isFile()) {
        parseSingleFile(child.getAbsolutePath());
      } else {
        parseFilesInFolder(child.getAbsolutePath());
      }
    }
  }

  private String getPackageName(ASTRoot astRoot) {
    return Optional.ofNullable(astRoot.packageDeclaration())
        .map(PackageDeclaration::name)
        .orElse(UNKNOWN);
  }
}
