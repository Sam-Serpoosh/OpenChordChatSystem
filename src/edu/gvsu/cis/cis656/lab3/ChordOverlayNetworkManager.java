package edu.gvsu.cis.cis656.lab3;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class ChordOverlayNetworkManager implements PresenceService {
	private Chord _chord;

	public ChordOverlayNetworkManager(boolean isMaster, String host, int port) {
		super();
		PropertiesLoader.loadPropertyFile();
		if (isMaster)
			createChordNetwork(port);
		else
			joinChordNetwork(host, port);
	}

	@Override
	public boolean register(RegistrationInfo info) throws ServiceException {
		StringKey key = new StringKey(info.getUserName());
		if (nodeAlreadyExists(key))
			return false;

		_chord.insert(key, info);
		return true;
	}

	@Override
	public boolean updateRegistrationInformation(RegistrationInfo newInfo)
			throws ServiceException {
		StringKey key = new StringKey(newInfo.getUserName());
		Set<Serializable> nodes = _chord.retrieve(key);
		if (nodes.isEmpty())
			return false;

		RegistrationInfo registrationInfo = (RegistrationInfo) nodes.toArray()[0];
		_chord.remove(key, registrationInfo);
		_chord.insert(key, newInfo);
		return true;
	}

	@Override
	public void unregister(RegistrationInfo info) throws ServiceException {
		StringKey key = new StringKey(info.getUserName());
		_chord.remove(key, info);
	}

	@Override
	public RegistrationInfo lookup(String username) throws ServiceException {
		StringKey key = new StringKey(username);
		Set<Serializable> nodes = _chord.retrieve(key);
		if (nodes.isEmpty())
			return null;

		return (RegistrationInfo) nodes.toArray()[0];
	}

	private void createChordNetwork(int port) {
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);

		String localUrlString;
		URL localUrl = null;
		try {
			localUrlString = createUrl(protocol, InetAddress.getLocalHost().
					getHostAddress(), port);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		try {
			localUrl = new URL(localUrlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Incorrect Url: " + localUrlString, e);
		}

		_chord = new ChordImpl();
		try {
			_chord.create(localUrl);
		} catch (ServiceException exception) {
			throw new RuntimeException("Could not create DHT ! ", exception);
		}
	}

	private void joinChordNetwork(String bootstrapHost, int bootstrapPort) {
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);

		int localPort;
		try {
			localPort = getPort();
		} catch (IOException exception) {
			throw new RuntimeException("Error while trying to find free port!",
					exception);
		}

		String localUrlStr;
		URL localUrl = null;
		try {
			localUrlStr = createUrl(protocol, InetAddress.getLocalHost()
					.getHostAddress(), localPort);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		try {
			localUrl = new URL(localUrlStr);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Incorrect Url: " + localUrlStr, e);
		}

		String bootstrapUrlStr = createUrl(protocol, bootstrapHost,
				bootstrapPort);
		URL bootstrapUrl = null;
		try {
			bootstrapUrl = new URL(bootstrapUrlStr);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Incorrect Url: " + bootstrapUrlStr, e);
		}

		_chord = new ChordImpl();
		try {
			_chord.join(localUrl, bootstrapUrl);
		} catch (ServiceException e) {
			throw new RuntimeException("Could not join DHT!", e);
		}
	}

	private boolean nodeAlreadyExists(StringKey key) throws ServiceException {
		return !_chord.retrieve(key).isEmpty();
	}

	private int getPort() throws IOException {
		ServerSocket server = new ServerSocket(0);
		int localPort = server.getLocalPort();
		server.close();
		return localPort;
	}

	private String createUrl(String protocol, String host, int port) {
		return protocol + "://" + host + ':' + port + '/';
	}
}
