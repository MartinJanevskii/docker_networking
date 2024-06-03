package udp;

import java.io.IOException;
import java.net.*;

public class UDPServer extends Thread{

    private byte[] buffer;
    DatagramSocket socket;

    public UDPServer(int port, String name){
        try {
            socket = new DatagramSocket(port, InetAddress.getByName(name));
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        buffer = new byte[256];
    }

    @Override
    public void run() {
        while(true) {
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(datagramPacket);
                String recieved = new String(datagramPacket.getData(), 0, buffer.length).trim();
                System.out.println("Recieved: " + recieved);
                if(recieved.equals("logout")) break;
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, datagramPacket.getAddress(), datagramPacket.getPort());
                socket.send(packet);
                buffer = new byte[256];
            } catch (IOException e) {
                throw new RuntimeException(e);
//            }finally {
//                socket.close();
//                System.out.println("Recieved logout, now closing the socket");
            }
        }
    }

    public static void main(String[] args) {
        UDPServer server = new UDPServer(6666, "localhost");
        server.start();
    }
}
