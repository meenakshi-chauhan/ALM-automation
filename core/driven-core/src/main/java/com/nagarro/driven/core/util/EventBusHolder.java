package com.nagarro.driven.core.util;

import com.google.common.eventbus.EventBus;

/**
 * This class is used to hold all the events registered in it.
 *
 * @author nagarro
 */
public class EventBusHolder {

  private static final EventBus INSTANCE = new EventBus();

  private EventBusHolder() {
    // no instantiation allowed
  }

  /**
   * It registers the event in event bus.
   *
   * @param object, the event to be registered
   */
  public static void register(Object object) {
    INSTANCE.register(object);
  }

  /**
   * It post the event registered in event bus.
   *
   * @param event, the event to be post
   */
  public static void post(Object event) {
    INSTANCE.post(event);
  }

  /**
   * It unregisters the event registered in event bus.
   *
   * @param object, the event to be unregistered
   */
  public static void unregister(Object object) {
    INSTANCE.unregister(object);
  }
}
