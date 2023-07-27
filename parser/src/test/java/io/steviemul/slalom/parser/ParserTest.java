package io.steviemul.slalom.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.steviemul.slalom.model.java.ASTRoot;
import io.steviemul.slalom.serializer.ASTRootSerializer;
import io.steviemul.slalom.utils.IOUtils;
import org.junit.jupiter.api.Test;

public class ParserTest {

  private static final String SOURCE_PATH =
      "../example/src/main/java/io/steviemul/slalom/example/Sample.java";

  @Test
  void json_serializes_deserializes_correctly() throws Exception {

    // Given
    Parser parser = new Parser();

    String source = IOUtils.readFile(SOURCE_PATH);

    // When
    ASTRoot expectedAstRoot = parser.parse(source);

    String expectedParsed = ASTRootSerializer.toJson(expectedAstRoot);

    ASTRoot actualASTRoot = ASTRootSerializer.fromJsonString(expectedParsed);

    // Then
    assertEquals(expectedAstRoot, actualASTRoot);
  }

  @Test
  void json_serializes_deserializes_difference() throws Exception {

    // Given
    Parser parser = new Parser();

    String source = IOUtils.readFile(SOURCE_PATH);

    // When
    ASTRoot expectedAstRoot = parser.parse(source);

    String expectedParsed = ASTRootSerializer.toJson(expectedAstRoot);

    ASTRoot actualASTRoot = ASTRootSerializer.fromJsonString(expectedParsed);

    actualASTRoot.path("UNKNOWN");

    // Then
    assertNotEquals(expectedAstRoot, actualASTRoot);
  }

  @Test
  void yaml_serializes_deserializes_correctly() throws Exception {

    // Given
    Parser parser = new Parser();

    String source = IOUtils.readFile(SOURCE_PATH);

    // When
    ASTRoot expectedAstRoot = parser.parse(source);

    String expectedYamlParsed = ASTRootSerializer.toYAML(expectedAstRoot);

    ASTRoot actualASTRoot = ASTRootSerializer.fromYAMLString(expectedYamlParsed);

    // Then
    assertEquals(expectedAstRoot, actualASTRoot);
  }

  @Test
  void yaml_serializes_deserializes_difference() throws Exception {

    // Given
    Parser parser = new Parser();

    String source = IOUtils.readFile(SOURCE_PATH);

    // When
    ASTRoot expectedAstRoot = parser.parse(source);

    String expectedYamlParsed = ASTRootSerializer.toYAML(expectedAstRoot);

    ASTRoot actualASTRoot = ASTRootSerializer.fromYAMLString(expectedYamlParsed);

    actualASTRoot.path("UNKNOWN");

    // Then
    assertNotEquals(expectedAstRoot, actualASTRoot);
  }
}
