package server;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import common.Communicator;

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
			CommunicatorServerImpl server = new CommunicatorServerImpl();
			Communicator rmiService = (Communicator) UnicastRemoteObject.exportObject(server, 0);
			LocateRegistry.createRegistry(port);
			Registry registry = LocateRegistry.getRegistry(port);
			registry.rebind("Communicator", rmiService);
			System.out.println("OK, port: " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -Djava.security.policy=file:${workspace_loc}/RMI/src/client/security.policy
// -Djava.rmi.server.codebase=file:${workspace_loc}/RMI/bin/

// rmic Slowa
// start rmiregistry /rmiregistry &
