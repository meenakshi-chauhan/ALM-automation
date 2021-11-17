package com.nagarro.driven.core.util;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.nagarro.driven.core.config.CoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadExecutor {

  private static final Logger log = LoggerFactory.getLogger(ThreadExecutor.class);


  private static ThreadExecutor obj;
  private ExecutorService executorService;
  private HashMap<String, Future<?>> futures;
  private CoreConfig.CoreConfigSpec coreConfig;

  private ThreadExecutor(){
   executorService = Executors.newCachedThreadPool();
   futures = new HashMap<>();
   coreConfig = CoreConfig.getInstance();
  }

  public static ThreadExecutor getInstance()
  {
    if (obj==null)
      obj = new ThreadExecutor();
    return obj;
  }

  /**
   * Initiates an orderly shutdown in which previously submitted tasks are executed, but no new
   * tasks will be accepted.Invocation has no additional effect if already shut down.
   */
  public void shutDown() {
    log.info("Shutting Down Thread Executor");
    executorService.shutdown();
  }

  /**
   * Attempts to stop all actively executing tasks, halts the processing of waiting tasks. This
   * method does not wait for actively executing tasks to terminate. Use awaitTermination to do
   * that.
   *
   * <p>There are no guarantees beyond best-effort attempts to stop processing actively executing
   * tasks
   */
  public void forceShutDown() {
    log.info("Shutting Down Thread Executor forcefully");
    executorService.shutdownNow();
  }

  /**
   * Blocks until all tasks have completed execution after a shutdown request, or the timeout
   * occurs, or the current thread is interrupted, whichever happens first.
   *
   * @return termination status
   */
  public boolean awaitTermination(){
    try {
      log.info("Waiting for tasks to complete");
      shutDown();
      return executorService.awaitTermination(coreConfig.threadExecutorWaitSeconds(), TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error("Timeout while waiting for task termination");
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /**
   * Executes the given command at some time in the future. The command may execute in a new thread,
   * in a pooled thread, or in the calling thread, at the discretion of the Executor implementation.
   *
   * @param command to execute
   */
  public void execute(Runnable command) {
    log.info("Adding task to executor");
    executorService.execute(command);
  }

  /**
   * Submits a Runnable task for execution
   *
   * @param command to execute
   * @param name for future reference
   */
  public void submit(Runnable command, String name) {
    log.info("Adding task {} to executor",name);
    futures.put(name, executorService.submit(command));
  }

  /**
   * Checks if task is already present
   *
   * @param name of the task
   * @return is thread present
   */
  public boolean isTaskPresent(String name) {
    log.info("Waiting for tasks to complete");
    return  (futures.containsKey(name));
  }

  /**
   * Checks if task is currently running
   *
   * @param name of the task
   * @return true if task is running
   */
  public boolean isTaskRunning(String name) {
    return Optional.ofNullable(futures.get(name)).map(future->!future.isDone()).orElse(false);
  }
}
