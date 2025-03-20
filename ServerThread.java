package org.example;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable{
    private final Socket player1;
    private final Socket player2;
    private final BufferedReader in1, in2;
    private final PrintWriter out1, out2;
    private char[][] board = {
            {' ', '|', ' ', '|', ' '},
            {'-', '+', '-', '+', '-'},
            {' ', '|', ' ', '|', ' '},
            {'-', '+', '-', '+', '-'},
            {' ', '|', ' ', '|', ' '}
    };

    public ServerThread(Socket p1, Socket p2) throws IOException {
        this.player1 = p1;
        this.player2 = p2;
        this.in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        this.in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
        this.out1 = new PrintWriter(p1.getOutputStream(), true);
        this.out2 = new PrintWriter(p2.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            out1.println("Welcome Player 1 (X)");
            out2.println("Welcome Player 2 (O)");

            int turn = 1; // 1 za Player 1, 2 za Player 2
            while (true) {
                sendBoard(out1);
                sendBoard(out2);

                PrintWriter currentOut = (turn == 1) ? out1 : out2;
                BufferedReader currentIn = (turn == 1) ? in1 : in2;
                char symbol = (turn == 1) ? 'X' : 'O';

                currentOut.println("Your turn! Enter row (0-2) and column (0-2) separated by space:");
                String move = currentIn.readLine();
                if (move == null || move.equalsIgnoreCase("exit")) {
                    out1.println("Game Over! Player disconnected.");
                    out2.println("Game Over! Player disconnected.");
                    break;
                }

                String[] parts = move.split(" ");
                if (parts.length != 2) continue;

                int row = Integer.parseInt(parts[0]) * 2;
                int col = Integer.parseInt(parts[1]) * 2;

                if (board[row][col] == ' ') {
                    board[row][col] = symbol;
                } else {
                    currentOut.println("Invalid move, try again.");
                    continue;
                }

                if (checkWinner(symbol)) {
                    sendBoard(out1);
                    sendBoard(out2);
                    out1.println("Player " + turn + " (" + symbol + ") wins!");
                    out2.println("Player " + turn + " (" + symbol + ") wins!");
                    break;
                }

                if (isBoardFull()) {
//                    sendBoard(out1);
//                    sendBoard(out2);
                    out1.println("It's a draw!");
                    out2.println("It's a draw!");
                    break;
                }

                turn = (turn == 1) ? 2 : 1;
            }
        } catch (IOException e) {
            System.out.println("Game ended due to an error: " + e.getMessage());
        } finally {
            try {
                player1.close();
                player2.close();
            } catch (IOException e) {
                System.out.println("Error closing sockets.");
            }
        }
    }

    private void sendBoard(PrintWriter out) {
        for (char[] row : board) {
            out.println(new String(row));
        }
    }

    private boolean checkWinner(char symbol) {
        for (int i = 0; i < 5; i += 2) {
            if (board[i][0] == symbol && board[i][2] == symbol && board[i][4] == symbol) return true;
            if (board[0][i] == symbol && board[2][i] == symbol && board[4][i] == symbol) return true;
        }
        if (board[0][0] == symbol && board[2][2] == symbol && board[4][4] == symbol) return true;
        if (board[0][4] == symbol && board[2][2] == symbol && board[4][0] == symbol) return true;
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 5; i += 2) {
            for (int j = 0; j < 5; j += 2) {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }
}
