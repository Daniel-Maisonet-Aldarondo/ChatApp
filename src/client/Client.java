package client;

import server.ClientInfo;
import server.sql.Connect;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.*;

public class Client {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private boolean running;

    private String name;


    public Client(String name, String address, int port) {
        try{
            this.name = name;
            this.address = InetAddress.getByName(address);
            this.port = port;

            socket = new DatagramSocket();

            running = true;
            listen();
            send("\\con:" + name);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            if (!message.startsWith("\\")) {
                message = name + ": " + message;
                sendToDB(message);
            }
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data,data.length, address, port);
            socket.send(packet);
            System.out.println("Sent message to " + address.getHostAddress() + ": " + port);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToDB(String message) {
        // use the prepapared statement to avoid conflicts with escape character ex: '
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String log;

        try {
            con = Connect.connectToDB();
            String query = "insert into Chat_Logs values(?)";
            statement = con.prepareStatement(query);
            statement.setString(1,message);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
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
                            ClientWindow.printToConsole(message);
                        }
                    }
                    running = false;

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }; listenThread.start();
    }

    private static boolean isCommand(String message, DatagramPacket packet) {
        if(message.startsWith("\\dis:")) {
            //do nothing because server handles the message
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }
}

