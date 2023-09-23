package entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void onServer(Integer port) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("\u001B[01;32mServer On!\u001B[0m");

            Socket client = serverSocket.accept();
            OutputStream out = client.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);
            InputStream in = client.getInputStream();
            Scanner clientScanner = new Scanner(in);
            Scanner scn = new Scanner(System.in);

            if (client.isConnected()) {
                System.out.println("\u001B[01;32mClient connected...\u001B[0m");

                Thread receiveMessageThread = new Thread(() -> {
                    while (true) {
                        if (clientScanner.hasNextLine()) {
                            String receivedMsg = clientScanner.nextLine();
                            System.out.println("[CLIENT]: " + receivedMsg);
                        }
                    }
                });

                Thread sendMessageThread = new Thread(() -> {
                    while (true) {
                        String msg = scn.nextLine();

                        if (msg.equals("logout")) {
                            writer.println("Server Closed!");
                            try {
                                serverSocket.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("\u001B[01;31mServer Closed!\u001B[0m");
                            break;
                        }
                        writer.println(msg);
                    }
                });
                sendMessageThread.start();
                receiveMessageThread.start();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        onServer(6666);
    }
}