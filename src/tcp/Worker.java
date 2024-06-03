package tcp;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Worker extends Thread{

    private Socket socket;
    private static Semaphore s1;
    private static Semaphore s2;

    public Worker(Socket socket){
        this.socket = socket;
        s1 = new Semaphore(1);
        s2 = new Semaphore(1);
    }

    public void writeToFile(String s) throws InterruptedException {
        s1.acquire(1);
        String path = "src/data/output.txt";
        try {
            PrintWriter fileWriter = new PrintWriter(new FileWriter(path, true));
            fileWriter.write(s);
            fileWriter.write("\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        s1.release(1);
    }

    public void incrementCounter() throws InterruptedException {
        String path = "src/data/counter.txt";
        s2.acquire(1);
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(new File(path)));
            String line = fileReader.readLine().trim();
            int count;
//            if (line.equals("start"))
//                count = 0;
//            else
                count = Integer.parseInt(line);
            count++;
            PrintWriter fileWriter = new PrintWriter(new FileWriter(path));
            line = String.valueOf(count);
            fileWriter.write(line);
            fileWriter.write("\n");
            fileWriter.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        s2.release(1);
    }

    @Override
    public void run() {
        PrintWriter sockerWriter = null;
        BufferedReader socketReader = null;
        try {
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sockerWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line;
            while ((line = socketReader.readLine()) != null){
                writeToFile(line);
                incrementCounter();
                System.out.println("Recieved: " + line);
                sockerWriter.write(line);
                sockerWriter.write("\n");
                sockerWriter.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socketReader.close();
                sockerWriter.close();
                socketReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
