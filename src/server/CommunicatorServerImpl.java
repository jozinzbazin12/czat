package server;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.AuthenticationException;

import common.Communicator;
import common.RemoteObserver;
import common.User;

public class CommunicatorServerImpl extends Observable implements Communicator {

	private static final long MAX_IDLE_TIME = 600000;
	private Map<String, User> connectedUsers = new ConcurrentHashMap<>();
	private Map<String, WrappedObserver> observersMap = new ConcurrentHashMap<>();
	private volatile static SimpleDateFormat dateformat = new SimpleDateFormat("[dd.MM.yyyy HH:mm:ss]");
	private Thread janitor;

	public CommunicatorServerImpl() throws RemoteException {
		janitor = new Thread() {
			@Override
			public void run() {
				while (true) {
					Iterator<Map.Entry<String, User>> iter = connectedUsers.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, User> entry = iter.next();
						if (System.currentTimeMillis() - entry.getValue().getLastTime() > MAX_IDLE_TIME) {
							StringBuilder str = new StringBuilder();
							str.append("Usuwam uzytkownika ").append(entry.getKey()).append(" z powodu bezczynnosci");
							sendServerMessage(str.toString());
							iter.remove();
						}
					}
					try {
						sleep(MAX_IDLE_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		janitor.start();
	}

	@Override
	public User login(RemoteObserver o, String name) throws RemoteException, AuthenticationException {
		if (connectedUsers.get(name) != null) {
			throw new AuthenticationException("Uzytkownik juz istnieje!");
		}

		User u = new User(name);
		connectedUsers.put(name, u);
		WrappedObserver mo = new WrappedObserver(o, null);

		addObserver(mo); // Observer on new User achieved
		observersMap.put(name, mo);

		sendServerMessage(name + " dolaczyl do czatu");
		return u;
	}

	@Override
	public void send(String message, User user) throws RemoteException, AuthenticationException {
		authenticate(user);
		String msg = buildMessage(user, message);
		notifyMessage(msg);
	}

	@Override
	public void send(String message, String to, User user) throws RemoteException, AuthenticationException {
		authenticate(user);
		User toUser = connectedUsers.get(to);
		if (toUser == null) {
			throw new RemoteException("Nie ma takiego uzytkownika");
		}
		sendPriv(message, user, toUser);
	}

	@Override
	public void logout(RemoteObserver observer, User user) throws RemoteException, AuthenticationException {
		if (connectedUsers.get(user.getName()) != null) {
			connectedUsers.remove(user.getName());
			notifyMessage(user.getName() + " opuscil‚ czat");
		}
		String observerName = observer.getName();
		WrappedObserver wo = observersMap.get(observerName);
		deleteObserver(wo);

	}

	@Override
	public void getUsersList(RemoteObserver observer) throws RemoteException, AuthenticationException {
		String message = buildServerMessage("Uzytkownicy: " + connectedUsers.values().toString());
		observer.update(this, message);
	}

	private void authenticate(User u) throws AuthenticationException {
		User user = connectedUsers.get(u.getName());
		if (user == null) {
			throw new AuthenticationException("Nie jestes zalogowany");
		}
		user.update();
	}

	private void notifyMessage(String message) {
		setChanged();
		notifyObservers(message);
	}

	private String buildMessage(User from, String msg) {
		String dateString = dateformat.format(new Date());
		StringBuilder str = new StringBuilder();
		str.append(dateString).append(" ").append(from.getName()).append(") - ").append(msg);
		return str.toString();
	}

	public void sendPriv(String msg, User from, User toUser) {
		String str = "[PRIV] " + buildMessage(from, msg);
		notifyObservers(str);
	}

	private String buildServerMessage(String message) {
		String dateString = dateformat.format(new Date());
		StringBuilder str = new StringBuilder();
		str.append("[SERVER] ").append(dateString).append(" - ").append(message);
		return str.toString();
	}

	private void sendServerMessage(String msg) {
		String message = buildServerMessage(msg);
		notifyMessage(message);
	}
}
