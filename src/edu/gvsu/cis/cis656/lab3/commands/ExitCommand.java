package edu.gvsu.cis.cis656.lab3.commands;

import java.io.IOException;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab3.PresenceService;
import edu.gvsu.cis.cis656.lab3.RegistrationInfo;

public class ExitCommand implements ChordCommand {
	private RegistrationInfo _user;
	private PresenceService _chordManager;
	
	public ExitCommand(RegistrationInfo user, PresenceService chordManager) {
		_user = user;
		_chordManager = chordManager;
	}

	@Override
	public void execute() throws IOException, ServiceException {
		_chordManager.unregister(_user);
		System.exit(0);
	}
}
