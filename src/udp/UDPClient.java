package udp;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;

public class UDPClient extends Thread{

    private DatagramSocket socket;
    private String serverName;
    private InetAddress address;
    private byte[] buffer;
    private int serverPort;
    private int thread;

    public UDPClient(String serverName, int port, int thread) throws UnknownHostException, SocketException {
        socket = new DatagramSocket();
        this.serverName = serverName;
        serverPort = port;
        address = InetAddress.getByName(serverName);
        buffer = new byte[256];
        this.thread = thread;
    }

    @Override
    public void run() {
        String path = "src/data/input.txt";
//        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null){
                line = thread + " " + line;
                buffer =  line.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                socket.send(packet);
                buffer = new byte[256];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String rec = new String(packet.getData(), 0, packet.getLength()).trim();
                System.out.println(rec);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        for (int i = 1; i < 4; i++) {
            UDPClient client = new UDPClient("localhost", 6666, i);
            client.start();
        }
    }
}
