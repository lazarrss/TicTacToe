package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT = 2025;

    public Server() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Waiting for two players to connect..");

            Socket player1 = serverSocket.accept();
            System.out.println("Player 1 connected!");
            Socket player2 = serverSocket.accept();
            System.out.println("Player 2 connected!");

            new Thread(new ServerThread(player1, player2)).start();

            System.out.println("Game has started.");
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
