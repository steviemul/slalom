package io.steviemul.slalom.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.steviemul.slalom.model.java.ASTRoot;

public class ASTRootSerializer {

  private static final String MODEL_JAVA = "io.steviemul.slalom.model.java";
  private static final String ARRAY_LIST = "java.util.ArrayList";
  private static final String LINKED_HASH_SET = "java.util.LinkedHashSet";
  private static final String LIST12 = "java.util.ImmutableCollections$List12";

  private static final PolymorphicTypeValidator ptv =
      BasicPolymorphicTypeValidator.builder()
          .allowIfSubType(MODEL_JAVA)
          .allowIfSubType(ARRAY_LIST)
          .allowIfSubType(LINKED_HASH_SET)
          .allowIfSubType(LIST12)
          .build();

  public static String toJson(ASTRoot astRoot) throws Exception {
    return jsonMapper().writerWithDefaultPrettyPrinter().writeValueAsString(astRoot);
  }

  public static ASTRoot fromJsonString(String astRootJsonString) throws Exception {
    return jsonMapper().readValue(astRootJsonString, ASTRoot.class);
  }

  public static ASTRoot fromJsonBytes(byte[] astRootJsonBytes) throws Exception {
    return fromJsonString(new String(astRootJsonBytes));
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
