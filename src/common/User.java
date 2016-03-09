package common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements Serializable {

	private static final long serialVersionUID = -2698993581924179211L;
	private String name;

	private static SimpleDateFormat dateformat = new SimpleDateFormat("[dd:MM:yyyy HH:mm:ss]");

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void send(String msg, User from) {
		System.out.println(buildMessage(from, msg));
	}

	private String buildMessage(User from, String msg) {
		String dateString = dateformat.format(new Date());
		StringBuilder str = new StringBuilder();
		str.append(dateString).append(" ").append(from.name).append(") - ").append(msg);
		return str.toString();
	}

	public void sendPriv(String msg, User from) {
		String str = ">>" + buildMessage(from, msg);
		System.out.println(str.toString());
	}

}
