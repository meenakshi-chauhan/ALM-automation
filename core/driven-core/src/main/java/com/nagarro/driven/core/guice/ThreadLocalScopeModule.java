package com.nagarro.driven.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * A Guice dependency injection (DI) module which is responsible for binding a {@link ThreadLocalScope} to
 * the DI's context. This module must be installed in order to use {@link ThreadLocalScope}
 */
public class ThreadLocalScopeModule extends AbstractModule {
  private final ThreadLocalScope threadLocalScope = new ThreadLocalScope();

  @Override
  protected void configure() {
    bindScope(ThreadLocalScoped.class, provideThreadLocalScope());
  }

  @Provides
  ThreadLocalScope provideThreadLocalScope() {
    return threadLocalScope;
  }
}
