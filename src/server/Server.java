package server;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import common.Communicator;
import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        //String url = "rmi://localhost:1099/";
        try {

            CommunicatorServerImpl server = new CommunicatorServerImpl();
            Communicator rmiService = (Communicator) UnicastRemoteObject.exportObject(server, 0);

            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.rebind("Communicator", rmiService);   // changed for work purpose
            
            System.out.println("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// -Djava.security.policy=file:${workspace_loc}/RMI/src/client/security.policy
// -Djava.rmi.server.codebase=file:${workspace_loc}/RMI/bin/

// rmic Slowa
// start rmiregistry /rmiregistry &
