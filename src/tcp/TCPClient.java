package tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient extends Thread{

    private int port;
    private String serverName;
    private int thread;

    public TCPClient(int port, String serverName, int counter){
        this.port = port;
        this.serverName = serverName;
        thread = counter;
    }

    @Override
    public void run() {
        String path = "src/data/input2.txt";
        BufferedReader fileReader = null;
        BufferedReader sockerReader = null;
        PrintWriter socketWriter = null;
        try {
            Socket socket = new Socket(InetAddress.getByName(serverName), port);
            fileReader = new BufferedReader(new FileReader(path));
            String line;
            sockerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            while ((line = fileReader.readLine()) != null){
                String[] parts = line.split(",");
                for (String s : parts){
                    socketWriter.write(thread + " " + s + "\n");
                    socketWriter.flush();
                    String response = sockerReader.readLine();
                    if (response != null){
                        System.out.println("server responded " + response);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (socketWriter != null)
                    socketWriter.close();
                if (fileReader != null)
                    fileReader.close();
                if (sockerReader != null)
                    sockerReader.close();
//                if (socket != null)
//                    socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("SERVER_PORT"));
        String s = System.getenv("SERVER_NAME");
        for (int i = 1; i <6; i++){
            new TCPClient(7000, "server", i).start();
        }
    }
}
