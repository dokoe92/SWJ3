package swj3.pc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskExecutor {

  private final ExecutorService threadPool;

  public TaskExecutor(int poolSize) {
    threadPool = Executors.newFixedThreadPool(poolSize);
  }

  public void submitTask(Runnable task) {
    threadPool.execute(task);
  }

  public void shutdown() throws InterruptedException {
    threadPool.shutdownNow();
    threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
  }
}
