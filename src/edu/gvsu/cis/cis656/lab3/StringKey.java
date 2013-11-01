package edu.gvsu.cis.cis656.lab3;

import de.uniba.wiai.lspi.chord.service.Key;

public class StringKey implements Key {
	private String _theString;

	public StringKey(String theString) {
		_theString = theString;
	}

	public byte[] getBytes() {
		return _theString.getBytes();
	}

	public int hashCode() {
		return _theString.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof StringKey)
			return ((StringKey)obj)._theString.equals(_theString);
		return false;
	}
}
