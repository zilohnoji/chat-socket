package entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {

    public static void configure(String host, Integer port) throws IOException {
        Socket client = connectOnServer("127.0.0.1", 6666);
        if (client.isConnected()) {
            System.out.printf("Connected on server: \u001B[01;32m%s\u001B[0m\nPort: \u001B[01;32m %d\u001B[0m\n",
                    client.getInetAddress().getHostName(), client.getLocalPort());

            HashMap<String, Object> data = configureTransportConnection(client);

            Thread sendThread = new Thread(() -> {
                while (true) {
                    String msg = ((Scanner) data.get("scn")).nextLine();
                    sendTo(((PrintWriter) data.get("writer")), ((Scanner) data.get("scn")), msg);
                }
            });

            Thread receivedMessageThread = new Thread(() -> {
                while (true) {
                    if (((Scanner) data.get("serverScanner")).hasNextLine()) {
                        String receivedMessage = receivedTo((Scanner) data.get("serverScanner"));
                        if (receivedMessage.equals("logout")) {
                            System.out.println("\u001B[01;31mServer Closed!!");
                            break;
                        }
                        System.out.println("[SERVER]: " + receivedMessage);
                    }
                }
            });

            receivedMessageThread.start();
            sendThread.start();
        }
    }

    private static Socket connectOnServer(String host, Integer port) throws IOException {
        return new Socket(host, port);
    }

    private static void sendTo(PrintWriter writer, Scanner scn, String msg) {
        writer.println(msg);
    }

    private static HashMap<String, Object> configureTransportConnection(Socket client) throws IOException {
        return new HashMap<String, Object>() {{
            put("serverScanner", new Scanner(client.getInputStream()));
            put("out", client.getOutputStream());
            put("writer", new PrintWriter(client.getOutputStream(), true));
            put("scn", new Scanner(System.in));
        }};
    }

    private static String receivedTo(Scanner serverScanner) {
        return serverScanner.nextLine();
    }

    public static void main(String[] args) throws IOException {
        configure("127.0.0.1", 6666);
    }
}
