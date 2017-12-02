/**
 * An implementation of a simple N&C game.
 * NB Change BOARD_SIZE to 4 and see what happens when you change just
 * ONE constant!
 */

import java.io.*;
import java.net.*;

public class NCServer {
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
     *  state.
     */
    public static void play() throws IOException {
        // turn control
        boolean blackTurn = true;
        boolean whiteTurn = false;

        // game control
        int movesMade = 0;
        boolean gameOver = false;

        // user interface loop
        String command = "";

        try {

//            try {

                //Waits of incoming connection from clients
                ServerSocket server = new ServerSocket(10006);
                System.out.println("Waiting for client connection");

                //Once a client tries to establish a connection, it accepts the connection
                Socket client = server.accept();
                client.setSoTimeout(10000); // Socket timeout in milliseconds
                System.out.println("Client connection accepted");

                server.close(); // Don't accept any more connections

                //reading from client

                BufferedReader clientInput = new BufferedReader
                								(new InputStreamReader
                										(client.getInputStream()));

                // Sending to client

                PrintWriter clientOutput = new PrintWriter(client.getOutputStream(), true);

                String gameRequest = clientInput.readLine();

                String reply = "";

                if (gameRequest.equals("new game")) {
                    System.out.println("Game request from " + client.getInetAddress().getHostName());
                    System.out.print("yes/no: ");
                    reply = EasyIn.getString();
                    clientOutput.println(reply);
                    clientOutput.flush(); //flush everything just in case

                }

                if (reply.equals("yes")) {

                    do {
                        // output board and status
                        displayBoard(Reversi.getInstance().getBoard());
                        blackTurn = Reversi.getInstance().getBlackTurn();
                        whiteTurn = Reversi.getInstance().getWhiteTurn();

                        if (blackTurn) {

                            System.out.println("Waiting for client input");
                            command = clientInput.readLine();


                        } else {
                            System.out.println("Waiting for local input");
                            System.out.println("Format: move x y");
                            command = EasyIn.getString();
                            clientOutput.println(command);
                            clientOutput.flush();
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
                                        System.out.println("Game Over.");
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
                
                clientInput.close(); //close inbound stream
                clientOutput.close(); //Close outbound stream
                client.close(); //Close socket

            } catch (SocketTimeoutException ste) {
                System.out.println("Connection Time Out");
            }

            catch (SocketException se) {
            System.out.println("Cannot read from client");
        }
    }

    /**
     * Entry point. Initialising the board array here - we might wish to
     *  have a separate initialisation method.
     */
    public static void main(String[] args) throws IOException {
        Reversi.getInstance().newGame();
        play();
    }
}