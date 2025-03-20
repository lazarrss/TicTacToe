package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";

    public Client() throws IOException {
        try (Socket socket = new Socket(HOST, Server.PORT);
             BufferedReader inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter outSocket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server.");
            while (true) {
                System.out.println("uso ovde");
                String response;
                while ((response = inSocket.readLine()) != null && !response.contains("Your turn")) {
                    System.out.println(response);
                }

                if (response == null || response.contains("wins") || response.contains("draw")) break;

                System.out.print("Enter move (row col): ");
                String move = scanner.nextLine();
                outSocket.println(move);
            }
        }
        System.out.println("Game over.");
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }
}
