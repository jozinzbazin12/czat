package server;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import common.Communicator;

public class Server {

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		String url = "rmi://localhost:1099/";
		try {
			Communicator server = new CommunicatorServerImpl();
			Naming.rebind(url + "Communicator", server);
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