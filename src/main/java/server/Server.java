package server;

import java.net.DatagramSocket;

public class Server {
	
	private static DatagramSocket socket;

	//initiallize server
	public static void start(int port) {
		try {
			socket = new DatagramSocket(port);
			System.out.println("System started on port " + port);

		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	//send message to every client
	private static void boadcast() {
	}
	//send message to specific client
	private static void send() {

	}
	//listen for other clients
	private static void listen() {
	}


	public static void stop() {


	}
}


