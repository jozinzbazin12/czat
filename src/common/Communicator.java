package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.naming.AuthenticationException;

public interface Communicator extends Remote {

	User login(String name) throws RemoteException, AuthenticationException;

	void logout(User user) throws RemoteException, AuthenticationException;

	void send(String message, User user) throws RemoteException, AuthenticationException;

	void send(String message, String to, User user) throws RemoteException, AuthenticationException;

}
