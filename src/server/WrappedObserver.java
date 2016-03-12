/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

import common.RemoteObserver;
import common.User;

/**
 *
 * @author Mateusz
 */
class WrappedObserver implements Observer, Serializable {

	private static final long serialVersionUID = 1L;

	private transient User user;

	private RemoteObserver ro = null;

	public WrappedObserver(RemoteObserver ro, User user) {
		this.ro = ro;
		this.user = user;
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			ro.update(o.toString(), arg);
		} catch (RemoteException e) {
			System.out.println("Remote exception removing observer:" + this);
			o.deleteObserver(this);
		}
	}

	public User getUser() {
		return user;
	}

}
