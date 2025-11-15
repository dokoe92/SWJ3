package swj3.pc.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskQueue extends Remote {
    void put(Task task) throws InterruptedException, RemoteException;
    Task take() throws InterruptedException, RemoteException;
}
