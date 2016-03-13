package server;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import common.Communicator;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public class Server {

    private static int port = 1098;

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        try {
            setSettings();
            SSLCommunicatorServerImp server = new SSLCommunicatorServerImp(port);
            LocateRegistry.createRegistry(port, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory(null, null, true));
            Registry registry = LocateRegistry.getRegistry("localhost", port, new SslRMIClientSocketFactory());
            registry.rebind("Communicator", server);
            System.out.println("OK, port: " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setSettings() {

        String pass = "password";

        System.setProperty("javax.net.ssl.debug", "all");

        System.setProperty("javax.net.ssl.keyStore", "C:\\ssl\\keystore-server.jks");
        System.setProperty("javax.net.ssl.trustStore", "C:\\ssl\\truststore-server.jks");

        System.setProperty("javax.net.ssl.keyStorePassword", pass);
        System.setProperty("javax.net.ssl.trustStorePassword", pass);
    }
}
// -Djava.security.policy=file:${workspace_loc}/RMI/src/client/security.policy
// -Djava.rmi.server.codebase=file:${workspace_loc}/RMI/bin/

// rmic Slowa
// start rmiregistry /rmiregistry &
