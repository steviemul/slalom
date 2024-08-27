package io.steviemul.slalom.utils;

import java.util.Collection;

public class CollectionUtils {

  public static boolean hasLength(Collection<?> collection) {
    return collection != null && collection.size() > 0;
  }

}
