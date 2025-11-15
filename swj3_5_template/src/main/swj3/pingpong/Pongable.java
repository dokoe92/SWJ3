package swj3.pingpong;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

public interface Pongable extends Remote {

  void pong(LocalDateTime pingTime) throws RemoteException;
}
