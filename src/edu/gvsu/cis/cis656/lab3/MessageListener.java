package edu.gvsu.cis.cis656.lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageListener implements Runnable {
	private ServerSocket _serverSocket;

	public MessageListener() {
		try {
			_serverSocket = new ServerSocket(0);
		} catch (IOException exception) {
			throw new RuntimeException("Could NOT bind the socket!", exception);
		}
	}

	public InetAddress getInetAddress() {
		return _serverSocket.getInetAddress();
	}

	public int getLocalPort() {
		return _serverSocket.getLocalPort();
	}

	public void close() {
		try {
			_serverSocket.close();
		} catch (IOException exception) {
			throw new RuntimeException("Could NOT close the socket!", exception);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket clientSocket = _serverSocket.accept();
				listenForMessage(clientSocket);
			} catch (IOException exception) {
				System.err.println("Could NOT Accept Socket!");
				System.exit(1);
			}
		}
	}
	
	private void listenForMessage(Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));

		System.out.println();
		System.out.println(bufferedReader.readLine());
		System.out.println();

		bufferedReader.close();
		socket.close();
	}
}
