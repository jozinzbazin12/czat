package chat.server;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.activation.Activatable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public class Server {

	private static int port = 1099;

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
			SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory();
			SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory(null, null, true);

			Registry registry = LocateRegistry.createRegistry(port, csf, ssf);
			Remote exportObject = Activatable.exportObject(server, null, port, csf, ssf);
			registry.rebind("Communicator", exportObject);
			System.out.println("OK, port: " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setSettings() {
		String pass = "password";
		System.setProperty("javax.net.ssl.debug", "all");
		System.setProperty("javax.net.ssl.keyStore", "src/chat/server/ssl_keys/keystore-server.jks");
		System.setProperty("javax.net.ssl.trustStore", "src/chat/server/rssl_keys/truststore-server.jks");

		System.setProperty("javax.net.ssl.keyStorePassword", pass);
		System.setProperty("javax.net.ssl.trustStorePassword", pass);
	}
}
// -Djava.security.policy=src/chat/common/security.policy
