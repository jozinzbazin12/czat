package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.AuthenticationException;

import common.Communicator;
import common.User;

public class CommunicatorServerImpl extends UnicastRemoteObject implements Communicator {

	private static final long serialVersionUID = -6469696327068073544L;

	private Map<String, User> connectedUsers = new HashMap<>();

	public CommunicatorServerImpl() throws RemoteException {
	}

	@Override
	public User login(String name) throws RemoteException, AuthenticationException {
		if (connectedUsers.get(name) != null) {
			throw new AuthenticationException("Uzytkownik juz istnieje!");
		}

		User u = new User(name);
		connectedUsers.put(name, u);
		return u;
	}

	@Override
	public void send(String message, User user) throws RemoteException, AuthenticationException {
		authenticate(user);
		for (Entry<String, User> u : connectedUsers.entrySet()) {
			u.getValue().send(message, user);
		}

	}

	@Override
	public void logout(User name) throws AuthenticationException {
		if (connectedUsers.get(name.getName()) != null) {
			connectedUsers.remove(name.getName());
		}
	}

	@Override
	public void send(String message, String to, User user) throws RemoteException, AuthenticationException {
		authenticate(user);
		if (connectedUsers.get(to) == null) {
			throw new RemoteException("Nie ma takiego uzytkownika");
		}
		connectedUsers.get(to).sendPriv(message, user);
	}

	private void authenticate(User u) throws AuthenticationException {
		if (connectedUsers.get(u.getName()) == null) {
			throw new AuthenticationException("Nie jestes zalogowany");
		}
	}
}
