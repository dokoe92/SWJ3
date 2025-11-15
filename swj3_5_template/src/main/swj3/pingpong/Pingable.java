package swj3.pingpong;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

public interface Pingable extends Remote { // Remote ist reiner Marker (Markerinterface, keine Methoden!)

  void ping(LocalDateTime pingTime) throws RemoteException;
  void ping(Pongable client, LocalDateTime pingTime) throws RemoteException;
}
