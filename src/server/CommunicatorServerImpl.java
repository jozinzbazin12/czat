package server;

import common.Communicator;
import common.RemoteObserver;
import common.User;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import javax.naming.AuthenticationException;

public class CommunicatorServerImpl extends Observable implements Communicator {

    private static final long serialVersionUID = -6469696327068073544L;

    private Map<String, User> connectedUsers = new HashMap<>();
    private Map<String, WrappedObserver> observersMap = new HashMap<>();

    public CommunicatorServerImpl() throws RemoteException {
    }

    @Override
    public User login(RemoteObserver o, String name) throws RemoteException, AuthenticationException {
        if (connectedUsers.get(name) != null) {
            throw new AuthenticationException("Uzytkownik juz istnieje!");
        }

        User u = new User(name);
        connectedUsers.put(name, u);
        WrappedObserver mo = new WrappedObserver(o);

        addObserver(mo);                    //Observer on new User achieved
        observersMap.put(name, mo);
        notifyMessage(name + " dołączył do czatu");

        return u;
    }

    @Override
    public void send(String message, User user) throws RemoteException, AuthenticationException {
        authenticate(user);

        notifyMessage(user.getName() + ": " + message);
    }

    @Override
    public void logout(RemoteObserver observer, User user) throws RemoteException, AuthenticationException {

        if (connectedUsers.get(user.getName()) != null) {
            connectedUsers.remove(user.getName());
            notifyMessage(user.getName() + " opóścił czat");
        }
        String observerName = observer.getName();
        WrappedObserver wo = observersMap.get(observerName);
        deleteObserver(wo);

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

    private void notifyMessage(String message) {

        setChanged();
        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("HH:mm:ss yyyy:MM:dd").format(Calendar.getInstance().getTime()) + " ");
        sb.append(message);
        notifyObservers(sb.toString());
    }
}
