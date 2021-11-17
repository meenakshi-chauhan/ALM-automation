package com.nagarro.driven.core.util;

import com.nagarro.driven.core.config.CoreConfig;
import java.util.function.BooleanSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to wait the thread.
 *
 * @author nagarro
 */
public class Waiter {

  private static final Logger log = LoggerFactory.getLogger(Waiter.class);
  private final BooleanSupplier condition;
  private long waitSeconds = CoreConfig.getInstance().waitSeconds();
  private long pauseMilliseconds = CoreConfig.getInstance().pauseMilliseconds();

  public Waiter(BooleanSupplier condition) {
    this.condition = condition;
  }

  public static boolean waitFor(BooleanSupplier predicate) {
    return new Waiter(predicate).waitForConditionToBeMet();
  }

  public long getPauseMilliseconds() {
    return pauseMilliseconds;
  }

  /**
   * Set the milliseconds to pause in between every check
   *
   * @param pauseMilliseconds a positive integer
   */
  protected void setPauseMilliseconds(int pauseMilliseconds) {
    this.pauseMilliseconds = pauseMilliseconds;
  }

  /**
   * Set the seconds to wait for the condition to be met.
   *
   * @param waitSeconds a positive integer
   */
  protected void setWaitSeconds(long waitSeconds) {
    this.waitSeconds = waitSeconds;
  }

  /**
   * Wait for a condition to be met. If waitSeconds is set to 10s, a check takes 1s and pause is set
   * to 2000ms, the condition will be checked 4 times:<br>
   * <br>
   *
   * <pre>
   * I---10s--I
   * *..*..*..*
   * (* = check, . = pause)
   * </pre>
   *
   * @return true if the condition has been met, false if there was a timeout
   */
  public boolean waitForConditionToBeMet() {
    log.debug("Waiting {}s for a condition to be met", waitSeconds);
    final long start = System.currentTimeMillis();

    int tries = 0;
    while (System.currentTimeMillis() < start + waitSeconds * 1000) {
      tries++;

      boolean conditionMet;
      try {
        conditionMet = condition.getAsBoolean();
      } catch (final Exception e) {
        conditionMet = false;
      }

      if (conditionMet) {
        log.debug(
            "Condition met after {} retries / {} seconds.",
            tries,
            ((System.currentTimeMillis() - start) / 1000));
        return true;
      } else {
        Sleeper.silentSleep(pauseMilliseconds);
      }
    }

    log.warn(
        "Condition not met after {} retries and {}s waiting time with {} seconds wait in between - time out.",
        tries,
        ((System.currentTimeMillis() - start) / 1000),
        pauseMilliseconds / 1000);

    return false;
  }
}
