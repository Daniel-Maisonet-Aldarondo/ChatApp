package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import server.sql.Connect;

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


    private static boolean isCommand(String message, DatagramPacket packet) {
        if(message.startsWith("\\con:")) {
            //Run connection
            String name = message.substring(message.indexOf(":") +1 );
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            clients.add(new ClientInfo(name, ClientId++ , address, port));
            //send user chat logs if any
            sendChatLogs(address, port);
            broadcast("User " + name + ", Connected!");
            return true;
        }
        if(message.startsWith("\\dis:")) {
            InetAddress address = packet.getAddress();
            for(ClientInfo client : clients) {
                if(client.getAddress().equals(address)){
                    broadcast("User " + client.getName() + ", has Disconnected!");
                    clients.remove(client);
                }
            }
            return true;
        }
        return false;
    }

//    private static ClientInfo getClientInfo(String name, int id, InetAddress address, int port) {
//        for(ClientInfo c : clients) {
//            if( c.getName().equals(name)
//            && c.getId() = )
//        }
//    }

    private static void sendChatLogs(InetAddress address, int port) {
        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        String logs = "";

        try {
            con = Connect.connectToDB();
            statement = con.createStatement();
            rs = statement.executeQuery("select * from Chat_Logs");

            while(rs.next()) {
                logs += rs.getString("text") + "\n";
            }
            send(logs, address, port);

        } catch (SQLException e) {
            e.printStackTrace();
            broadcast("Could not retrieve chat logs...");
        }

    }

    private static void stop() {
        running = false;
    }
}
