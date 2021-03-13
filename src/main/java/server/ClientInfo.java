package server;

import java.net.InetAddress;

public class ClientInfo {

	private InetAddress address;
	private int port;
	private String name;
	private int id;

	public ClientInfo(String name, int id, InetAddress address, int port) {
		this.name = name;
		this.id = id;
		this.address = address;
		this.port = port;
	}

	public String getName()  {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public InetAddress getAddress() {
		return this.address;
	}

	public int getPort() {
		return this.port;
	}

}

