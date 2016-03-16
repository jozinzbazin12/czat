package chat.common;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -2698993581924179211L;
	private transient long lastTime;
	private String name;

	public User() {
		lastTime = System.currentTimeMillis();
	}

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void update() {
		lastTime = System.currentTimeMillis();
	}

}
