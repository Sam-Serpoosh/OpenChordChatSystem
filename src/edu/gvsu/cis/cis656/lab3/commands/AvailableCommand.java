package edu.gvsu.cis.cis656.lab3.commands;

import java.io.IOException;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import edu.gvsu.cis.cis656.lab3.PresenceService;
import edu.gvsu.cis.cis656.lab3.RegistrationInfo;

public class AvailableCommand implements ChordCommand {
	private RegistrationInfo _user;
	private PresenceService _chordManager;
	
	public AvailableCommand(RegistrationInfo user, PresenceService chordManager) {
		_user = user;
		_chordManager = chordManager;
	}

	@Override
	public void execute() throws IOException, ServiceException {
		if (_user.isAvailable())
			System.out.println("You're already available!");
		else {
			_user.setStatus(true);
			boolean updated = _chordManager.updateRegistrationInformation(_user);
			if (updated)
				System.out.println("Status is Available now!");
		}
	} 
}
