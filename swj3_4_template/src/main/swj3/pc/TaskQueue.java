package swj3.pc;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {
    private final Queue<Task> queue = new LinkedList<>();
    private final int capacity;

    public TaskQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(Task task) throws InterruptedException {
        while (queue.size() == capacity) { // while braucht man damit nochmal geprüft wird ob Platz noch nicht weggeschnappt
            System.out.println("TaskQueue (%d/%d)%n  ==> %s: <full>".formatted(queue.size(), capacity, task));
            wait();
        }

        queue.offer(task);
        System.out.println("TaskQueue (%d/%d)%n  ==> %s".formatted(queue.size(), capacity, task));
        notifyAll();
    }

    public synchronized Task take() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("TaskQueue (%d/%d)%n  <== <empty>".formatted(queue.size(), capacity));
            wait(); // wait enstperrt, anderer kann rein
        }

        Task task = queue.poll();
        System.out.println("TaskQueue (%d/%d)%n  <== %s".formatted(queue.size(), capacity, task));
        notifyAll();

        return task;
    }
}
