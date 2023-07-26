package io.steviemul.slalom.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import io.steviemul.slalom.model.java.ASTRoot;

public class ASTRootSerializer {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final PolymorphicTypeValidator ptv =
      BasicPolymorphicTypeValidator.builder()
          .allowIfSubType("io.steviemul.slalom.model.java")
          .allowIfSubType("java.util.ArrayList")
          .allowIfSubType("java.util.LinkedHashSet")
          .build();

  static {
    objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
  }

  public static String toJson(ASTRoot astRoot) throws Exception {
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(astRoot);
  }

  public static ASTRoot fromJsonString(String astRootJsonString) throws Exception {
    return objectMapper.readValue(astRootJsonString, ASTRoot.class);
  }
}
