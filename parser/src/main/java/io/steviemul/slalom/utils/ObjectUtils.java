package io.steviemul.slalom.utils;

public class ObjectUtils {

  public static boolean isDefined(Object... objects) {

    for (Object object : objects) {
      if (object == null) return false;
    }

    return true;
  }
}
