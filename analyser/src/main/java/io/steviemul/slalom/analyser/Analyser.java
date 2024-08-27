package io.steviemul.slalom.analyser;

import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.parser.Parser;
import io.steviemul.slalom.serializer.ASTRootSerializer;
import io.steviemul.slalom.store.Store;
import io.steviemul.slalom.store.exception.StoreException;
import io.steviemul.slalom.utils.HashUtils;
import io.steviemul.slalom.utils.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class Analyser {

  private static final String SLALOM = "slalom";
  private static final String AST_COLLECTION = "ast";
  private static final String JAVA = "java";

  private final Store store;

  public void analyze(String path) {
    File file = new File(path);

    if (!file.exists()) {
      throw new IllegalArgumentException("Specified path does not exist");
    }

    if (file.isFile()) {
      analyseSingleFile(path);
    } else if (file.isDirectory()) {
      analyseFolder(path);
    }

    try {
      int parsedFiles = store.count(AST_COLLECTION);

      log.info("Total parsed files [{}]", parsedFiles);
    } catch (StoreException e) {
      log.error("Error counting parsed files", e);
    }
  }

  public void analyseSingleFile(String path) {

    Parser parser = new Parser();
    ASTRoot astRoot;

    try {
      if (path.endsWith("." + JAVA)) {
        String source = IOUtils.readFile(path);
        String sha = HashUtils.sha(source);

        if (store.exists(AST_COLLECTION, sha)) {
          byte[] contents = store.getBlobContent(AST_COLLECTION, sha);

          astRoot = ASTRootSerializer.fromJsonBytes(contents);

          log.info("AST Successfully loaded from store [{}, {}]",
              astRoot.packageDeclaration().name(),
              astRoot.typeDeclaration().name());

        } else {
          astRoot = parser.parse(path, source);

          String packageName = astRoot.packageDeclaration() != null
              ? astRoot.packageDeclaration().name()
              : "UNKNOWN";

          log.info("AST Successfully parsed from source [{}, {}]",
              packageName,
              astRoot.typeDeclaration().name());
        }

        String astJson = ASTRootSerializer.toJson(astRoot);

        store.save(AST_COLLECTION, astRoot.sha(), astJson.getBytes(StandardCharsets.UTF_8));
      }
    } catch (Exception e) {
      log.error("Error parsing file [{}]", path, e);
    }
  }

  public void analyseFolder(String path) {

    File directory = new File(path);

    if (!directory.exists() || !directory.isDirectory()) {
      throw new IllegalArgumentException("Path specified must exist and be a directory");
    }

    analyseFolder(directory);
  }

  private void analyseFolder(File directory) {

    for (File child : directory.listFiles()) {
      if (child.isFile()) {
        analyseSingleFile(child.getAbsolutePath());
      } else {
        analyseFolder(child.getAbsolutePath());
      }
    }
  }

  public static void main(String args[]) throws Exception {
    Store store = new Store(SLALOM).start();

    Analyser analyser = new Analyser(store);

    analyser.analyze(args[0]);

    store.delete();
  }
}
