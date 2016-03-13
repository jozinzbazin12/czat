package server;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import javax.naming.AuthenticationException;

import common.Communicator;
import common.RemoteObserver;
import common.User;

public class CommunicatorServerImpl extends Observable implements Communicator {

	private static final long MAX_IDLE_TIME = 600000;
	private Map<String, WrappedObserver> observersMap = new ConcurrentHashMap<>();
	private volatile static SimpleDateFormat dateformat = new SimpleDateFormat("[dd.MM.yyyy HH:mm:ss]");
	private Thread janitor;

	public CommunicatorServerImpl() throws RemoteException {
		janitor = new Thread() {
			@Override
			public void run() {
				while (true) {
					Iterator<Map.Entry<String, WrappedObserver>> iter = observersMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, WrappedObserver> entry = iter.next();
						if (System.currentTimeMillis() - entry.getValue().getUser().getLastTime() > MAX_IDLE_TIME) {
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
		if (observersMap.get(name) != null) {
			throw new AuthenticationException("Uzytkownik juz istnieje!");
		}

		User u = new User(name);
		WrappedObserver mo = new WrappedObserver(o, u);
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
		WrappedObserver toUser = observersMap.get(to);
		if (toUser == null) {
			throw new RemoteException("Nie ma takiego uzytkownika");
		}
		WrappedObserver fromUser = observersMap.get(user.getName());
		sendPriv(message, fromUser, toUser);
	}

	@Override
	public void logout(RemoteObserver observer, User user) throws RemoteException, AuthenticationException {
		if (observersMap.get(user.getName()) != null) {
			observersMap.remove(user.getName());
			notifyMessage(user.getName() + " opuscil� czat");
		}
		String observerName = observer.getName();
		WrappedObserver wo = observersMap.get(observerName);
		deleteObserver(wo);

	}

	@Override
	public void getUsersList(RemoteObserver observer) throws RemoteException, AuthenticationException {
		String message = buildServerMessage("Uzytkownicy: " + observersMap.values().toString());
		observer.update(this, message);
	}

	private void authenticate(User u) throws AuthenticationException {
		WrappedObserver user = observersMap.get(u.getName());
		if (user == null) {
			throw new AuthenticationException("Nie jestes zalogowany");
		}
		user.getUser().update();
	}

	private void notifyMessage(String message) {
		setChanged();
		notifyObservers(message);
	}

	private String buildMessage(User from, String msg) {
		String dateString = dateformat.format(new Date());
		StringBuilder str = new StringBuilder();
		str.append(dateString).append(" (").append(from.getName()).append(") - ").append(msg);
		return str.toString();
	}

	public void sendPriv(String msg, WrappedObserver fromUser, WrappedObserver toUser) {
		String str = "[PRIV] " + buildMessage(fromUser.getUser(), msg);
		fromUser.update(this, str);
		toUser.update(this, str);
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
