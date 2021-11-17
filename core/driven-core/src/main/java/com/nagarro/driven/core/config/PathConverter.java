package com.nagarro.driven.core.config;

import java.lang.reflect.Method;
import java.nio.file.Path;
import org.aeonbits.owner.Converter;

public class PathConverter implements Converter<Path> {

  public Path convert(Method targetMethod, String text) {
    return Path.of(text);
  }
}
