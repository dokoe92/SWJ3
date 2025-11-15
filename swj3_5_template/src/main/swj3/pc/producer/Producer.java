package swj3.pc.producer;

import swj3.pc.common.FactorialTask;
import swj3.pc.common.PiApproximationTask;
import swj3.pc.common.Task;
import swj3.pc.common.TaskQueue;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class Producer {
    private static void produceFactorialTasks(TaskQueue taskQueue) {
        try {
            for (int i = 0; i < 100; i++) {
                int number = (int) (Math.random() * 100) + 1;
                Task task = new FactorialTask(number);
                System.out.println("Producer: %s".formatted(task));
                taskQueue.put(task);

                Thread.sleep(40);
            }
            System.out.println("Producer terminated successfully");
        } catch (InterruptedException | RemoteException e) {
            System.out.println("Producer terminated with exception: message = %s".formatted(e.getMessage()));
        }
    }

    private static void producePiApproximationTasks(TaskQueue taskQueue) {
        try {
            for (int i = 0; i < 100; i++) {
                int terms = (int) (Math.random() * 2000) + 1;
                int precision = terms < 100 ? 6 : terms < 1000 ? 9 : 11;
                Task task = new PiApproximationTask(i, precision);
                System.out.println("Producer: %s".formatted(task));
                taskQueue.put(task);

                Thread.sleep(1_500);
            }
            System.out.println("Producer terminated successfully");
        } catch (InterruptedException | RemoteException e) {
            System.out.println("Producer terminated with exception: message = %s".formatted(e.getMessage()));
        }
    }

    public static void main(String[] args) throws Exception {
        String hostAndPort = "localhost";
        if (args.length > 0) hostAndPort = args[0];
        String taskQueueUrl = String.format("rmi://%s/TaskQueueService", hostAndPort);
        System.out.printf("Connecting to: %s%n", taskQueueUrl);

        TaskQueue taskQueue = (TaskQueue) Naming.lookup(taskQueueUrl);

        Thread producerThread = Thread
                .ofPlatform()
                .daemon()
                .name("produer-thread-1")
                .start(() -> produceFactorialTasks(taskQueue));

        Thread producerThread2 = Thread
                .ofPlatform()
                .daemon()
                .name("produer-thread-2")
                .start(() -> producePiApproximationTasks(taskQueue));

        producerThread.join();
        producerThread2.join();
    }
}
