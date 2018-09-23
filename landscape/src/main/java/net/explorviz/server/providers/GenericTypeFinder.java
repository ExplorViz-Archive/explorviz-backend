package net.explorviz.server.providers;

import java.util.HashMap;
import java.util.Map;

public final class GenericTypeFinder {

  /*
   * The primary use case for this class is the runtime detection for generic parameters, e.g,
   * JSONAPIListProvider.readFrom(). There, we need to find the correct type for converting JSONAPI
   * payload to Java objects. Unfortunately, the passed parameter "type" is of type List<?>, but the
   * JSONAPI converter requires the type annotated class, e.g., Landscape. This class circumvents
   * this problem.
   */

  private static Map<String, Class<?>> typeMap = new HashMap<>();

  private GenericTypeFinder() {
    // do not instantiate
  }

  public static Class<?> giveClassForString(final String stringThatContainsClassName) {

    for (final Map.Entry<String, Class<?>> entry : typeMap.entrySet()) {

      if (stringThatContainsClassName.contains(entry.getKey())) {
        return entry.getValue();
      }
    }

    return null;

  }

  public static Map<String, Class<?>> getTypeMap() {
    return typeMap;
  }

}
