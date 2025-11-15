package swj3.pingpong;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;

public class PingService implements Pingable {

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int registryPort = Registry.REGISTRY_PORT; // Standard = 1099
        String serverHostName = "localhost";
        if (args.length > 0) {
            String[] hostAndPort = args[0].split(":");
            if (hostAndPort.length > 0) serverHostName = hostAndPort[0];
            if (hostAndPort.length > 1) registryPort = Integer.parseInt(hostAndPort[1]);
        }

        System.setProperty("java.rmi.server.hostname", serverHostName);

        String internalUrl = String.format("rmi://localhost:%d/PingService", registryPort);
        String externalUrl = String.format("rmi://%s:%d/PingService", serverHostName, registryPort);

        Pingable service = new PingService();
        Remote serviceStub = UnicastRemoteObject.exportObject(service, registryPort);

        LocateRegistry.createRegistry(registryPort);
        Naming.rebind(internalUrl, serviceStub);

        System.out.println("Service available at " + externalUrl);
    }

    @Override
    public void ping(LocalDateTime pingTime) throws RemoteException {
        System.out.printf("Service: pingTime = %s%n", pingTime);
    }

    @Override
    public void ping(Pongable client, LocalDateTime pingTime) throws RemoteException { // brauch ich bei der Hausübung beim z.B. QuizService zum Zurückschicken
        System.out.printf("Service: pingTime = %s%n", pingTime);
        client.pong(pingTime);
    }
}

