package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import javax.naming.AuthenticationException;

import common.Communicator;
import common.RemoteObserver;
import common.User;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIClientSocketFactory;

public class Client extends UnicastRemoteObject implements RemoteObserver {

    private static final long serialVersionUID = 7240745763804587458L;
    private static final String SYNTAX_ERROR = "Niepoprawna skladnia komendy, uzyj \"help\", aby sprawdzic dostepne komendy";
    private static Scanner sc;
    private static Communicator server;
    private static User user;
    private static Client remoteClient;

    public Client() throws RemoteException {
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
                    case GET_USERS:
                        server.getUsersList(remoteClient);
                        break;
                    default:
                        break;
                }
            } catch (AuthenticationException | RemoteException e) {
                // e.printStackTrace();
                System.out.println(e.getMessage());
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

        try {
            server.send(line[1], user);
        } catch (NullPointerException e) {
            System.out.println("Nie jestes zalogowany");
        }
    }

    private static void sendPriv(String[] line) throws AuthenticationException, RemoteException {
        if (line.length != 3) {
            System.out.println(SYNTAX_ERROR);
            return;
        }
        server.send(line[2], line[1], user);
    }

    private static void logout() throws AuthenticationException, RemoteException {
        server.logout(remoteClient, user);
    }

    private static void login(String[] line) throws AuthenticationException, RemoteException, MalformedURLException {
        if (line.length != 3) {
            System.out.println(SYNTAX_ERROR);
            return;
        }
        StringBuilder name = new StringBuilder();
        name.append("//").append(line[1]).append("/Communicator");
        try {
            setSettings();  

            Registry registry = LocateRegistry.getRegistry("localhost", 1098, new SslRMIClientSocketFactory()); // need to explict port number from args and pass it trough
            server = (Communicator) registry.lookup("Communicator");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        remoteClient = new Client();
        user = server.login(remoteClient, line[2]);
        if (user != null) {
            System.out.println("Zalogowano do " + line[1]);
        }
    }

    @Override
    public void update(Object observable, Object updateMsg) throws RemoteException {
        System.out.println(updateMsg); // shows the observed message
    }

    @Override
    public String getName() {
        return user.getName();
    }

    private static void setSettings() {

        String pass = "password";
        System.setProperty("javax.net.ssl.debug", "all");
        System.setProperty("javax.net.ssl.keyStorePassword", pass);
        System.setProperty("javax.net.ssl.trustStorePassword", pass);

        System.setProperty("javax.net.ssl.keyStore", "C:\\ssl\\keystore-client.jks");
        System.setProperty("javax.net.ssl.trustStore", "C:\\ssl\\truststore-client.jks");

    }
}

// -Djava.security.policy=file:${workspace_loc}/RMI/src/client/security.policy
// -Djava.rmi.server.codebase=file:${workspace_loc}/RMI/bin/
