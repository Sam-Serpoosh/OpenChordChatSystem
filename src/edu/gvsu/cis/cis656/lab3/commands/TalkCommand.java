package edu.gvsu.cis.cis656.lab3.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab3.PresenceService;
import edu.gvsu.cis.cis656.lab3.RegistrationInfo;

public class TalkCommand implements ChordCommand {
	private String[] _commandParts;
	private PresenceService _chordManager;
	private RegistrationInfo _user;
	
	public TalkCommand(String[] commandParts, PresenceService chordManager, 
			RegistrationInfo user) {
		_commandParts = commandParts;
		_chordManager = chordManager;
		_user = user;
	}
	
	@Override
	public void execute() throws ServiceException, IOException {
		if (_commandParts.length >= 3)
			talk();
		else
			System.out.println("Usage: talk {username} {message}");
	}
	
	public void talk() throws ServiceException, IOException {
		if (_commandParts.length >= 3) {
			String receiver = _commandParts[1];
			String message = grabMessageOutOfCommand();

			RegistrationInfo receiverInfo = _chordManager.lookup(receiver);
			if (receiverInfo == null)
				System.out.println("The receiver does NOT exist!");
			else if (receiverInfo.getUserName().equals(_user.getUserName()))
				System.out.println("You can NOT send a message to yourself.");
			else if (!receiverInfo.isAvailable())
				System.out.println("The receiver is NOT available now.");
			else
				sendMessage(receiverInfo, message);
		}
	}
	
	private String grabMessageOutOfCommand() {
		String message = "";
		for (int i = 2; i < _commandParts.length; i++)
			message += _commandParts[i] + " ";
		
		return message;
	}
	
	private void sendMessage(RegistrationInfo receiverInfo, String message) throws IOException {
		Socket clientSocket = null;
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;

		try {
			clientSocket = new Socket(receiverInfo.getHost(), receiverInfo.getPort());
			printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			bufferedReader = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Unknown Host: " + receiverInfo.getHost() + "!");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("I/O Problem occured: " + receiverInfo.getHost() + ".");
			System.exit(1);
		}
		String outgoingMessage = _user.getUserName() + ": " + message;
		printWriter.println(outgoingMessage);
		System.out.println("Delivered message to " + receiverInfo.getUserName().toUpperCase());

		printWriter.close();
		bufferedReader.close();
		clientSocket.close();
	}
}
