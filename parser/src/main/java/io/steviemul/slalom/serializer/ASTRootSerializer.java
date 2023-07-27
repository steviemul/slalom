package io.steviemul.slalom.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.steviemul.slalom.model.java.ASTRoot;

public class ASTRootSerializer {

  private static final PolymorphicTypeValidator ptv =
      BasicPolymorphicTypeValidator.builder()
          .allowIfSubType("io.steviemul.slalom.model.java")
          .allowIfSubType("java.util.ArrayList")
          .allowIfSubType("java.util.LinkedHashSet")
          .build();

  public static String toJson(ASTRoot astRoot) throws Exception {
    return jsonMapper().writerWithDefaultPrettyPrinter().writeValueAsString(astRoot);
  }

  public static ASTRoot fromJsonString(String astRootJsonString) throws Exception {
    return jsonMapper().readValue(astRootJsonString, ASTRoot.class);
  }

  public static String toYAML(ASTRoot astRoot) throws Exception {
    return yamlMapper().writerWithDefaultPrettyPrinter().writeValueAsString(astRoot);
  }

  public static ASTRoot fromYAMLString(String astRootYamlString) throws Exception {
    return yamlMapper().readValue(astRootYamlString, ASTRoot.class);
  }

  private static ObjectMapper jsonMapper() {
    return new ObjectMapper()
        .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
  }

  private static ObjectMapper yamlMapper() {
    return new ObjectMapper(new YAMLFactory())
        .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
  }
}
