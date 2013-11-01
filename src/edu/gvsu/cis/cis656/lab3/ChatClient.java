package edu.gvsu.cis.cis656.lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab3.commands.AvailableCommand;
import edu.gvsu.cis.cis656.lab3.commands.BusyCommand;
import edu.gvsu.cis.cis656.lab3.commands.ExitCommand;
import edu.gvsu.cis.cis656.lab3.commands.TalkCommand;

public class ChatClient {
	private final static int MASTER_PORT = 8080;
	private String _username;
	private String _host;
	private boolean _isMaster = false;

	public static void main(String[] args) throws ServiceException {
		new ChatClient().start(args);
	}

	private void start(String[] args) throws ServiceException {
		if (wrongNumberofArguments(args))
			printInstruction();

		setHostAndUsernameAndMasterStatus(args);
		PresenceService chordManager = new ChordOverlayNetworkManager(_isMaster, 
				_host, MASTER_PORT);
		RegistrationInfo currentUser = createUserAndStartListening(chordManager);
		
		try {
			printCommands();
			while (true) {
				System.out.println();
				System.out.print("$ ");
				String command = readCommand().toLowerCase();
				String[] commandParts = command.split(" ");
				String commandName = commandParts[0];

				if (commandName.equals("talk"))
					new TalkCommand(commandParts, chordManager, currentUser).execute();
				else if (commandName.equals("busy"))
					new BusyCommand(currentUser, chordManager).execute();
				else if (commandName.equals("available"))
					new AvailableCommand(currentUser, chordManager).execute();
				else if (commandName.equals("whoami"))
					System.out.println(currentUser.getUserName());
				else if (commandName.equals("exit"))
					new ExitCommand(currentUser, chordManager).execute();
				else
					System.out.println("Wrong Command, Retry!");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private boolean wrongNumberofArguments(String[] args) {
		return args.length < 2 || args.length > 3;
	}
	
	private RegistrationInfo createUserAndStartListening(PresenceService chordManager) 
			throws ServiceException {
		MessageListener messageListener = new MessageListener();
		RegistrationInfo user = new RegistrationInfo(_username, 
				messageListener.getInetAddress().getHostAddress(), 
				messageListener.getLocalPort(), true);
		
		if (!chordManager.register(user)) {
			System.err.println("The name '" + _username + "' is already taken!");
			System.exit(1);
		}
		
		new Thread(messageListener).start();
		
		return user;
	}

	private static void printInstruction() {
		System.err.println("Usage:\n" 
				+ "Start a master node\n"
				+ "java edu.gvsu.cis.cis656.lab3.ChatClient -master {user} {host}\n"
				+ "\n"
				+ "Start a regular node\n"
				+ "java edu.gvsu.cis.cis656.lab3.ChatClient {user} host:port\n");
		System.exit(1);
	}

	private void printCommands() {
		System.out.println("All Commands: ");
		System.out.println("* whoami");
		System.out.println("* talk [receiver] [message]");
		System.out.println("* busy");
		System.out.println("* available");
		System.out.println("* exit");
	}

	private static String readCommand() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			String input = bufferedReader.readLine();
			while (input.isEmpty())
				input = bufferedReader.readLine();
			return input;
		} catch (IOException e) {
			System.out.println("IO error trying to read command!");
			System.exit(1);
		}
		
		return "";
	}
	
	private void setHostAndUsernameAndMasterStatus(String[] args) {
		if (args[0].startsWith("-")) {
			_isMaster = true;
			_username = args[1].toLowerCase();
			_host = args[2];
		} else {
			_username = args[0].toLowerCase();
			_host = args[1];
		}
	}
}
