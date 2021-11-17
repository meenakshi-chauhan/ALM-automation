package com.nagarro.driven.core.guice;

import com.google.inject.*;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;

/**
 * A custom Guice scope which extends a ThreadLocal thus allowing a thread-specific dependency
 * injection. This scope must be used only after entering it and should never be used after exiting
 * it. {@link ThreadLocalScoped} should be used inside the type binding or as a class annotation in
 * order to refer to this scope
 */
public class ThreadLocalScope extends ThreadLocal<Map<Key<?>, Object>> implements Scope {

  public void enter() {
    checkState(get() == null, "A scoping block is already in progress");
    set(newHashMap());
  }

  public void exit() {
    checkState(get() != null, "No scoping block in progress");
    remove();
  }

  public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
    return () -> {
      Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

      @SuppressWarnings("unchecked")
      T current = (T) scopedObjects.get(key);
      if (current == null && !scopedObjects.containsKey(key)) {
        current = unscoped.get();

        // Proxies exist only to serve circular dependencies
        if (Scopes.isCircularProxy(current)) {
          return current;
        }

        scopedObjects.put(key, current);
      }
      return current;
    };
  }

  private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
    Map<Key<?>, Object> scopedObjects = get();
    if (scopedObjects == null) {
      throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
    }
    return scopedObjects;
  }
}
