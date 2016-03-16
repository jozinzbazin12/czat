/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.AuthenticationException;

import chat.common.Communicator;
import chat.common.RemoteObserver;
import chat.common.User;

/**
 *
 * @author Mateusz
 */
public class SSLCommunicatorServerImp implements Communicator, Serializable {

	private static final long serialVersionUID = -400082474560871668L;
	private CommunicatorServerImpl communicatorImp;

	public SSLCommunicatorServerImp(int port) throws RemoteException {
		communicatorImp = new CommunicatorServerImpl();
	}

	@Override
	public User login(RemoteObserver observer, String name) throws RemoteException, AuthenticationException {
		return communicatorImp.login(observer, name);
	}

	@Override
	public void logout(RemoteObserver observer, User user) throws RemoteException, AuthenticationException {
		communicatorImp.logout(observer, user);
	}

	@Override
	public void send(String message, User user) throws RemoteException, AuthenticationException {
		communicatorImp.send(message, user);
	}

	@Override
	public void send(String message, String to, User user) throws RemoteException, AuthenticationException {
		communicatorImp.send(message, to, user);
	}

	@Override
	public void getUsersList(User user) throws RemoteException, AuthenticationException {
		communicatorImp.getUsersList(user);
	}

}
