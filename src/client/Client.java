package client;

import server.ClientInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private boolean running;


    public Client(String name, String address, int port) {
        try{
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
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data,data.length, address, port);
            socket.send(packet);
            System.out.println("Sent message to " + address.getHostAddress() + ": " + port);
        }catch (Exception e) {
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

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }; listenThread.start();
    }

    private static boolean isCommand(String message, DatagramPacket packet) {
        if(message.startsWith("\\con:")) {
            //Run connection
            return true;
        }
        return false;
    }
//    public static void main(String[] args) {
//        Client client = new Client("Test","localhost", 5050);
//    }
}

