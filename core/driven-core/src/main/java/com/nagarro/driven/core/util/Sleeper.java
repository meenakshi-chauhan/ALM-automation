package com.nagarro.driven.core.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to sleep the thread.
 *
 * @author nagarro
 */
public final class Sleeper {

	private static final Logger log = LoggerFactory.getLogger(Sleeper.class);

	/* Default constructor of sleeper. */
	private Sleeper() {
		// no instantiation allowed
	}

	/**
	 * Causes the currently executing thread to sleep for the specified number of
	 * milliseconds.
	 *
	 * @param time
	 *            Milliseconds to sleep
	 */
	public static void silentSleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException interruptedExp) {
			log.warn("Error while sleeping, reinterrupting", interruptedExp);
			Thread.currentThread().interrupt();
		}
	}

	public static void sleep(long ms, SleepReason reason) {
		final StringBuilder sb = new StringBuilder("Sleeping for ").append(ms).append("ms");

		if (ms > 10 * 1000) {
			// log a "until" statement if we're sleeping longer than 10 seconds
			sb.append(", until ").append(LocalDateTime.now().plus(ms, ChronoUnit.MILLIS));
		}

		if (reason != null) {
			sb.append(", because ").append(reason.getReason());
		}

		log.trace("{}", sb);
		silentSleep(ms);
	}

	public static void sleep(int seconds, SleepReason reason) {
		sleep(seconds * 1000L, reason);
	}

}
