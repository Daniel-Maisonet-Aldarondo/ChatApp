package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {

    private static DatagramSocket socket;
    private static boolean running;
    private static int ClientId;
    private static ArrayList<ClientInfo> clients = new ArrayList<>();
    //initialize server
    public static void start(int port) {
        try {
            socket = new DatagramSocket(port);
            System.out.println("System started on port " + port);
            running = true;
            listen();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    //send message to every client
    private static void broadcast(String message) {
        for(ClientInfo info : clients) {
            send(message, info.getAddress(), info.getPort());
        }
    }
    //send message to specific client
    private static void send(String message, InetAddress address, int port) {
        try {
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data,data.length, address, port);
            socket.send(packet);
            System.out.println("Sent message to " + address.getHostAddress() + ": " + port);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    //listen for other clients
    private static void listen() {
        Thread listenThread = new Thread("Chat listener") {
            public void run() {
                try {
                    while(running) {
                        byte[] data = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);
                        //convert the byte array to string
                        String message = new String(data);
                        //to get rid of extra bytes
                        message = message.substring(0, message.indexOf("\\e"));

                        //MANAGE MESSAGE todo
                        if(!isCommand(message,packet)) {
                            broadcast(message);
                        }
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }; listenThread.start();
    }
    /*
     *   Server Command List
     * \con:[name] -> connects client to server
     * \dis:[id] -> disconnect from client
     */

    private static boolean isCommand(String message, DatagramPacket packet) {
        if(message.startsWith("\\con:")) {
            //Run connection
            String name = message.substring(message.indexOf(":") +1 );
            clients.add(new ClientInfo(name, ClientId++, packet.getAddress(), packet.getPort()));
            broadcast("User " + name + ", Connected!");
            return true;
        }
        return false;
    }

    private static void stop() {
        running = false;
    }
}
