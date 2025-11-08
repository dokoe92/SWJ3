package swj3.pc;

public class Consumer implements Runnable {
  private final TaskQueue taskQueue;

  public Consumer(TaskQueue taskQueue) {
    this.taskQueue = taskQueue;
  }

  @Override
  public void run() {
    Thread consumerThread = Thread.currentThread();

    try {
      while (!consumerThread.isInterrupted()) {
        Task task = taskQueue.take();

        System.out.println("Consumer (%s)%n --> %s".formatted(consumerThread.getName(), task));
        task.execute();
        System.out.println("Consumer (%s)%n <-- %s".formatted(consumerThread.getName(), task));
      }
    } catch (InterruptedException e) {
      System.out.println("Consumer (%s)%n interrupted (exception)".formatted(consumerThread.getName()));
      Thread.currentThread().interrupt();
    }
  }


}
