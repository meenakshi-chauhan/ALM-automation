package com.nagarro.driven.core.util;

import com.google.gson.Gson;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class SerializationUtil {
  private SerializationUtil() {
    // Utility class
  }

  public static <T> T deserializeFromJson(Gson gson, String json, Class<T> entityClass) {
    checkArgument(
        isNotBlank(json),
        format(
            "Can't deserialize %s instance from an empty JSON string",
            entityClass.getSimpleName()));
    return gson.fromJson(json, entityClass);
  }
}
