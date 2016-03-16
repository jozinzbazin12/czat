package chat.client;

public enum Action {

	LOGIN("login", "Loguje sie do serwera\nSkladnia: login ADRES NICK"),
	LOGOUT("logout", "Wylogowuje sie"),
	SEND("send", "Wysyla wiadomosc do wszystkich\nSkladnia: send WIADOMOSC"),
	SEND_PRIV("send-priv", "Wysyla prywatna wiadomosc do uzytkownika\nSkladnia: send-priv NICK WIADOMOSC"),
	GET_USERS("get-users", "Pobiera liste zalogoanych uzytkownikow"),
	EXIT("exit", "Konczy prace programu"),
	HELP("help", "help: Wyswietla dostepne komendy i instrukcje uzytkowania\nhelp AKCJA: wyswietla opis akcji");

	private String command;

	private String help;

	private Action(String command, String help) {
		this.command = command;
		this.help = help;
	}

	public String getCommand() {
		return command;
	}

	public String getHelp() {
		return help;
	}

	public static Action getValue(String str) {
		for (Action a : values()) {
			if (a.command.equals(str)) {
				return a;
			}
		}
		return null;
	}

}
