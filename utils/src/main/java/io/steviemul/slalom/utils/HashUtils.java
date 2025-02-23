package io.steviemul.slalom.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class HashUtils {

  private HashUtils() {}

  public static String sha(String input) {
    return DigestUtils.sha256Hex(input);
  }
}
