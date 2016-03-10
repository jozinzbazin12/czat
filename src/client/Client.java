package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Scanner;

import javax.naming.AuthenticationException;

import common.Communicator;
import common.RemoteObserver;
import common.User;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements RemoteObserver  {

	private static final String SYNTAX_ERROR = "Niepoprawna skladnia komendy, uzyj \"help\", aby sprawdzic dostepne komendy";
	private static Scanner sc;
	private static Communicator server;
	private static User user;
        
        public Client() throws RemoteException       
        {
            super();
        }

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		parseAction();
		if (sc != null) {
			sc.close();
		}
	}

	private static void parseAction() throws MalformedURLException, NotBoundException {
		while (true) {
			sc = new Scanner(System.in);
			String input = sc.nextLine();
			String[] line = input.split(" ");
			if (line.length == 0) {
				System.out.println("Brak komendy");
				continue;
			}
			try {
				Action action = Action.getValue(line[0]);
				if (action == null) {
					System.out.println("Niepoprawna komenda, uzyj \"help\", aby sprawdzic dostepne komendy");
					continue;
				}
				switch (action) {
				case HELP:
					help(line);
					break;
				case EXIT:
					return;
				case LOGIN:
					login(line);
					break;
				case LOGOUT:
					logout();
					break;
				case SEND:
					send(line);
					break;
				case SEND_PRIV:
					sendPriv(line);
					break;
				}
			} catch (AuthenticationException | RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private static void help(String[] line) {
		if (line.length >= 2) {
			Action a = Action.getValue(line[1]);
			System.out.println(a.getHelp());
		} else {
			System.out.println(Action.HELP.getHelp());
			StringBuilder str = new StringBuilder();
			str.append("Dostepne komendy:\n");
			for (Action a : Action.values()) {
				str.append(a.getCommand()).append(", ");
			}
			System.out.println(str.toString());
		}

	}

	private static void send(String[] line) throws AuthenticationException, RemoteException {
		if (line.length != 2) {
			System.out.println(SYNTAX_ERROR);
			return;
		}
		server.send(line[1], user);
	}

	private static void sendPriv(String[] line) throws AuthenticationException, RemoteException {
		if (line.length != 3) {
			System.out.println(SYNTAX_ERROR);
			return;
		}
		server.send(line[2], line[1], user);
	}

	private static void logout() throws AuthenticationException, RemoteException {
		server.logout(user);

	}

	private static void login(String[] line)
			throws AuthenticationException, RemoteException, MalformedURLException, NotBoundException {
		if (line.length != 3) {
			System.out.println(SYNTAX_ERROR);
			return;
		}
		StringBuilder name = new StringBuilder();
		name.append("//").append(line[1]).append(":1099/Communicator");
		server = (Communicator) Naming.lookup("Communicator");   //changed for work purpose
                Client remoteClient = new Client();
		user = server.login(remoteClient,line[2]);
		if (user != null) {
			System.out.println("Zalogowano do " + line[1]);
		} 
                
	}

    @Override
    public void update(Object observable, Object updateMsg) throws RemoteException {
        System.out.println(updateMsg);    // shows the observed message 
    }
}

// -Djava.security.policy=file:${workspace_loc}/RMI/src/client/security.policy
// -Djava.rmi.server.codebase=file:${workspace_loc}/RMI/bin/