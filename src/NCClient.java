/**
 * An implementation of a simple N&C game.
 * NB Change BOARD_SIZE to 4 and see what happens when you change just
 * ONE constant!
 */

import java.io.*;
import java.net.*;

public class NCClient {
    // board size
    private static final int BOARD_SIZE = 8;
    // square contents representation
    private static final char EMPTY_PIECE = '-';
    private static final char BLACK_PIECE = 'b';
    private static final char WHITE_PIECE = 'w';
    // game control
    private static final int MOVES_TO_STALEMATE = BOARD_SIZE * BOARD_SIZE;

    /**
     * Prints out a handsome String representation of the N&C board.
     */
    public static void displayBoard(char[][] board) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                System.out.print(board[row][col] + " ");
                if (col == BOARD_SIZE - 1) System.out.print((char) ('A' + row));
            }
            System.out.println();
        }
        System.out.println("1 2 3 4 5 6 7 8");
    }

    /**
     * The play loop of the game.
     * This could be simplified by exiting as soon as we reach a game over
     * state.
     */
    public static void play(String hostname) throws IOException {
        // turn control
        boolean blackTurn = true;
        boolean whiteTurn = false;

        // game control
        int movesMade = 0;
        boolean gameOver = false;


        // user interface loop
        String command = "";

        try {
            try {
                try {
                    try {
                        // Connects to a server
                        Socket server = new Socket(hostname, 10006);

                        server.setSoTimeout(10000); //Socket Timeout

                        System.out.println("Server accepted connection");

                        //reading from server

                        BufferedReader serverInput = new BufferedReader
                        								(new InputStreamReader
                        									(server.getInputStream()));

                        // Sending to server

                        PrintWriter serverOutput = new PrintWriter
                        								(server.getOutputStream(), true);

                        System.out.print("Send game request to server: ");
                        serverOutput.println(EasyIn.getString());
                        serverOutput.flush();

                        String reply = serverInput.readLine();


                        if (reply.equals("yes")) {

                            // client moves first
                            do {
                                // output board and status
                                displayBoard(Reversi.getInstance().getBoard());
                                blackTurn = Reversi.getInstance().getBlackTurn();

                                if (blackTurn) {
                                    System.out.println("Waiting for local input");
                                    System.out.println("Format: 'move x y', 'quit' if you want to exit the game");
                                    command = EasyIn.getString();
                                    serverOutput.println(command);
                                    serverOutput.flush();
                                } else {
                                    System.out.println("Waiting for server input");
                                    command = serverInput.readLine();
                                }

                                if (command.contains("move")) {
                                    if (gameOver)
                                        System.out.println("game over - cannot move!");
                                    else {

                                        String[] commands = command.split(" ");
                                        boolean incorrectInput = false;

                                        int row = -1;
                                        int col = -1;

                                        if (commands.length == 3) {
                                            row = Integer.parseInt(commands[1]) - 1;
                                            col = Integer.parseInt(commands[2]) - 1;
                                        }
                                        else incorrectInput = true;


                                        char piece = (Reversi.getInstance().getBlackTurn())? BLACK_PIECE : WHITE_PIECE;

                                        if (Reversi.isValidMove(Reversi.getInstance().getBoard(), piece, row, col) && !incorrectInput) {
                                            Reversi.getInstance().movePiece(Reversi.getInstance().getBoard(), row, col, false);
                                            movesMade++;
                                            displayBoard(Reversi.getInstance().getBoard());
                                            // Check for victory
                                            if (Reversi.getInstance().getGameState() == 1){
                                                System.out.println("Game Over");
                                                gameOver = true;
                                            }
                                            // Check stalemate
                                            else if (movesMade == MOVES_TO_STALEMATE) {
                                                System.out.println("Stalemate!");
                                                gameOver = true;
                                            }
                                        } else
                                            System.out.println("Illegal move!");
                                    }
                                }
                            }
                            while (!command.equals("quit") && !gameOver);

                        } else {

                            System.out.println("Game request rejected");
                        }
                        
                        serverInput.close(); //Close inbound stream
                        serverOutput.close(); //Close outbound stream
                        server.close(); //Close socket

                    } catch (UnknownHostException uhe) {
                        System.out.println("Could not find host. Enter the correct hostname");
                    }

                } catch (ConnectException ce) {
                    System.out.println("Could not connect to server");
                }

            } catch (SocketException se) {
                System.out.println("Cannot read from server");
            }

        } catch (SocketTimeoutException ste) {
            System.out.println("Connection time out");
        }
    
        
    }

    /**
     * Entry point. Initialising the board array here - we might wish to
     * have a separate initialisation method.
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Hostname:");
        String hostname = EasyIn.getString();
        Reversi.getInstance().newGame();
        play(hostname);
    }
}