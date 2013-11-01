package edu.gvsu.cis.cis656.lab3.commands;

import java.io.IOException;

import de.uniba.wiai.lspi.chord.service.ServiceException;

public interface ChordCommand {
	public void execute() throws IOException, ServiceException;
}
