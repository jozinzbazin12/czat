/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import common.Communicator;
import common.RemoteObserver;
import common.User;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationID;
import java.rmi.server.UnicastRemoteObject;
import javax.naming.AuthenticationException;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

/**
 *
 * @author Mateusz
 */
public class SSLCommunicatorServerImp extends UnicastRemoteObject  implements Communicator {

    CommunicatorServerImpl communicatorImp;
    public SSLCommunicatorServerImp(int port) throws RemoteException{
        super(port, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory(null, null, true));
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
    public void getUsersList(RemoteObserver observer) throws RemoteException, AuthenticationException {
        communicatorImp.getUsersList(observer);
    }
    
}
