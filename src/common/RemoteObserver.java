/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Mateusz
 */
public interface RemoteObserver extends Remote {
	
	void update(Object observable, Object updateMsg) throws RemoteException;

	String getName() throws RemoteException;
}
