package edu.gvsu.cis.cis656.lab3;

import java.io.Serializable;

public class RegistrationInfo implements Serializable {
	private static final long serialVersionUID = -1692519871343236571L;
	private String _userName;
	private String _host;
	private boolean _status;
	private int _port;

	public RegistrationInfo(String username, String host, int port, boolean status) {
		_userName = username;
		_host = host;
		_port = port;
		_status = status;
	}

	public String getUserName() {
		return _userName;
	}

	public String getHost() {
		return _host;
	}
	
	public int getPort() {
		return _port;
	}

	public boolean getStatus() {
		return _status;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public void setHost(String host) {
		_host = host;
	}

	public void setStatus(boolean status) {
		_status = status;
	}

	public void setPort(int port) {
		_port = port;
	}
	
	public boolean isAvailable() {
		return _status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _port;
		result = prime * result + ((_userName == null) ? 0 : _userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RegistrationInfo)
			return ((RegistrationInfo)obj).getUserName().equals(getUserName());
		return false;
	}

}