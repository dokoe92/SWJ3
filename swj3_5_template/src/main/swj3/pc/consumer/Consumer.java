package swj3.pc.consumer;

import swj3.pc.common.Task;
import swj3.pc.common.TaskQueue;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class Consumer {
    private final TaskQueue taskQueue;

    public Consumer(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void run() {
        System.out.println("Consumer started");

        for (;;) {
            try {
                Task task = taskQueue.take(); // Blocks if empty
                System.out.println("Consumer:  --> %s".formatted(task));
                task.execute();
                System.out.println("Consumer:  <-- %s".formatted(task));
            } catch (InterruptedException | RemoteException e) {
                System.out.println("Consumer error: message = %s".formatted(e.getMessage()));
                break;

            }
        }
    }

    public static void main(String[] args) throws Exception {
        String hostAndPort = "localhost";
        if (args.length > 0) hostAndPort = args[0];
        String taskQueueUrl = String.format("rmi://%s/TaskQueueService", hostAndPort);
        System.out.printf("Connecting to: %s%n", taskQueueUrl);

        TaskQueue taskQueue = (TaskQueue) Naming.lookup(taskQueueUrl);

        Consumer consumer = new Consumer(taskQueue);
        consumer.run();
    }
}
