package swj3.pc;

public class Main {

  private static void produceFactorialTask(TaskQueue taskQueue) {
    String threadName = Thread.currentThread().getName();
    try {
      for (int i = 0; i < 100; i++) {
        int number = (int) (Math.random() * 100) + 1;
        Task task = new FactorialTask(number);
        System.out.println("Producer (%s)%n ++> %s".formatted(threadName, task));
        taskQueue.put(task);

        Thread.sleep(1_500);
      }
    } catch (InterruptedException e) {
      System.out.printf("Producer (%s)%n interrupted (exception)".formatted(threadName));
    }
  }

  private static void producePiApproximationTask(TaskQueue taskQueue) {
    String threadName = Thread.currentThread().getName();
    try {
      for (int i = 0; i < 100; i++) {
        int terms = (int) (Math.random() * 2000) + 1;
        int precision = terms < 200 ? 6 : terms < 1000 ? 9 : 11;
        Task task = new PiApproximationTask(terms, precision);
        System.out.println("Producer (%s)%n ++> %s".formatted(threadName, task));
        taskQueue.put(task);

        Thread.sleep(1_500);
      }
    } catch (InterruptedException e) {
      System.out.printf("Producer (%s)%n interrupted (exception)".formatted(threadName));
    }
  }

  private static void testTaskQueue() throws InterruptedException {
    TaskQueue taskQueue = new TaskQueue(5);

    Runnable consumer1 = new Consumer(taskQueue);
    Thread consumerThread1 = Thread.ofPlatform()
            .daemon()
            .name("consumer-thread-1")
            .start(consumer1);

    Runnable consumer2 = new Consumer(taskQueue);
    Thread consumerThread2 = Thread.ofPlatform()
            .daemon()
            .name("consumer-thread-2")
            .start(consumer2);

    Thread producerThread1 = Thread.ofPlatform()
            .daemon() // beendet sich wenn alle fertig
            .name("producer-thread-1")
            .start(() -> produceFactorialTask(taskQueue));

    Thread producerThread2 = Thread.ofPlatform()
            .daemon() // beendet sich wenn alle fertig
            .name("producer-thread-2")
            .start(() -> producePiApproximationTask(taskQueue));

    Thread.sleep(30_000);


    producerThread1.interrupt();
    producerThread2.interrupt();
    consumerThread1.interrupt();
    consumerThread2.interrupt();

    producerThread1.join();
    producerThread2.join();
    consumerThread1.join();
    consumerThread2.join();
  }

  public static void testTaskExecutor() throws InterruptedException {
    TaskExecutor taskExecutor = new TaskExecutor(3);
    String threadName = Thread.currentThread().getName();

    System.out.println("-------- producing tasks ---------");

    for (int i = 0; i < 10; i++) {
      int number = (int) (Math.random() * 100) + 1;
      Task task1 = new FactorialTask(number);
      System.out.println("Producer (%s)%n ++> %s".formatted(threadName, task1));
      taskExecutor.submitTask( () -> task1.execute());

      int terms = (int) (Math.random() * 2000) + 1;
      int precision = terms < 200 ? 6 : terms < 1000 ? 9 : 11;
      Task task2 = new PiApproximationTask(terms, precision);
      System.out.println("Producer (%s)%n ++> %s".formatted(threadName, task2));
      taskExecutor.submitTask( () -> task2.execute());
    }

    System.out.println("-------- producing tasks ---------");
    Thread.sleep(30_000);

    System.out.println("--------- shutdown system --------");
    taskExecutor.shutdown();
    System.out.println("---------- taskExecutor terminated successfully -------------");
  }

  public static void main(String[] args) throws InterruptedException {
//    System.out.println("=========== Task Queue ===========");
//    testTaskQueue();

    System.out.println("=========== Task Queue ===========");
    testTaskExecutor();
  }


}
