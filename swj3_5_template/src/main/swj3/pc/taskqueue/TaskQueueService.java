package swj3.pc.taskqueue;

import swj3.pc.common.Task;
import swj3.pc.common.TaskQueue;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

public class TaskQueueService implements TaskQueue {
    private final Queue<Task> queue = new LinkedList<>();
    private final int capacity;

    public TaskQueueService(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(Task task) throws InterruptedException {
        while (queue.size() == capacity) {
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
            wait();
        }

        Task task = queue.poll();
        System.out.println("TaskQueue (%d/%d)%n  <== %s".formatted(queue.size(), capacity, task));
        notifyAll();

        return task;
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int registryPort = Registry.REGISTRY_PORT; // Standard = 1099
        String serverHostName = "localhost";
        if (args.length > 0) {
            String[] hostAndPort = args[0].split(":");
            if (hostAndPort.length > 0) serverHostName = hostAndPort[0];
            if (hostAndPort.length > 1) registryPort = Integer.parseInt(hostAndPort[1]);
        }

        System.setProperty("java.rmi.server.hostname", serverHostName);

        String internalUrl = String.format("rmi://localhost:%d/TaskQueueService", registryPort);
        String externalUrl = String.format("rmi://%s:%d/TaskQueueService", serverHostName, registryPort);

        TaskQueueService service = new TaskQueueService(10);
        Remote taskQueueStub = UnicastRemoteObject.exportObject(service, registryPort);

        LocateRegistry.createRegistry(registryPort);
        Naming.rebind(internalUrl, taskQueueStub);

        System.out.printf("TaskQueueService is running at %s ...%n", externalUrl);
    }
}
