package swj3.pingpong;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Client implements Pongable {

    private static final int NUM_PINGS = 10;

    public static void ping1(Pingable pingable) throws RemoteException, InterruptedException {
        for (int i = 1; i <= NUM_PINGS; i++) {
            LocalDateTime now = LocalDateTime.now();
            pingable.ping(now);
            System.out.printf("Client: pingTime = %s%n", now);
            Thread.sleep(500);
        }
    }

    public void ping2(Pingable pingable) throws RemoteException, InterruptedException {
        for (int i = 1; i <= NUM_PINGS; i++) {
            pingable.ping(this, LocalDateTime.now());
            Thread.sleep(500);
        }
    }

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException, InterruptedException {
        String hostAndPort = "localhost";
        if (args.length > 0) hostAndPort = args[0];

        String serviceUrl = String.format("rmi://%s/PingService", hostAndPort);
        System.out.printf("looking up %s ... %n", serviceUrl);

        Pingable serviceProxy = (Pingable) Naming.lookup(serviceUrl);

        System.out.println("typeof(proxy)=" + serviceProxy.getClass().getName());

//        ping1(serviceProxy);
        Client client = new Client();
        UnicastRemoteObject.exportObject(client, 0);
        client.ping2(serviceProxy);

        UnicastRemoteObject.unexportObject(client, true);
    }

    @Override
    public void pong(LocalDateTime pingTime) throws RemoteException {
        long timeInNanos = ChronoUnit.NANOS.between(pingTime, LocalDateTime.now());
        System.out.printf("Client: time for roundTrip = %.6f%n", timeInNanos / 1e9);
    }

}
