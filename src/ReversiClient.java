//import java.awt.*;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.*;
//
///**
// * Created by xw37 on 20/04/17.
// */
//public class ReversiClient {
//    public static void play(int[][] board, String hostname) throws IOException {
//        // turn control
//        boolean BLACK_PIECEToPlay = true;
//        boolean WHITE_PIECEToPlay = false;
//
//        // game control
//        int movesMade = 0;
//        boolean gameOver = false;
//
//        // user interface loop
//        String command = "";
//
//        try {
//            try {
//                try {
//                    try {
//                        // Connects to a server
//                        Socket server = new Socket(hostname, 10006);
//
//                        server.setSoTimeout(10000); //Socket Timeout
//
//                        System.out.println("Server accepted connection");
//
//                        //reading from server
//
//                        BufferedReader serverInput = new BufferedReader
//                                (new InputStreamReader
//                                        (server.getInputStream()));
//
//                        // Sending to server
//
//                        PrintWriter serverOutput = new PrintWriter
//                                (server.getOutputStream(), true);
//
//                        System.out.print("Send game request to server: ");
//                        serverOutput.println(EasyIn.getString());
//                        serverOutput.flush();
//
//                        String reply = serverInput.readLine();
//
//
//                        if (reply.equals("yes")) {
//
//                            do {
//                                // output board and status
//                                displayBoard(board);
//
//                                if (BLACK_PIECEToPlay) {
//                                    System.out.println("Waiting for local input");
//                                    command = EasyIn.getString();
//                                    serverOutput.println(command);
//                                    serverOutput.flush();
//                                } else {
//                                    System.out.println("Waiting for server input");
//                                    command = serverInput.readLine();
//
//                                }
//
//
//                                if (command.contains("move")) {
//                                    if (gameOver)
//                                        System.out.println("game over - cannot move!");
//                                    else {
//
//                                        String[] commands = command.split(" ");
//                                        int row = Integer.parseInt(commands[1]);
//                                        int col = Integer.parseInt(commands[2]);
//
//                                        if (Reversi.getInstance().movePiece(board, row, col, AI)) {
//                                            movesMade++;
//                                            // Check for victory
//                                            if (winThroughSquare(board, row, col)) {
//
//
//                                                if (BLACK_PIECEToPlay)
//                                                    System.out.println("BLACK_PIECEs wins!");
//                                                else
//                                                    System.out.println("WHITE_PIECEes wins!");
//
//                                                displayBoard(board);
//                                                gameOver = true;
//                                            }
//                                            // Check stalemate
//                                            else if (movesMade == MOVES_TO_STALEMATE) {
//                                                System.out.println("Stalemate!");
//                                                gameOver = true;
//                                            }
//                                            BLACK_PIECEToPlay = !BLACK_PIECEToPlay;
//                                        } else
//                                            System.out.println("Illegal move!");
//                                    }
//                                }
//                            }
//                            while (!command.equals("quit") && !gameOver);
//
//                        } else {
//
//                            System.out.println("Game request rejected");
//                        }
//
//                        serverInput.close(); //Close inbound stream
//                        serverOutput.close(); //Close outbound stream
//                        server.close(); //Close socket
//
//                    } catch (UnknownHostException uhe) {
//
//                        System.out.println("Could not find host. Enter the correct hostname");
//                    }
//
//                } catch (ConnectException ce) {
//
//                    System.out.println("Could not connect to server");
//                }
//
//            } catch (SocketException se) {
//
//                System.out.println("Cannot read from server");
//            }
//
//        } catch (SocketTimeoutException ste) {
//
//            System.out.println("Connection time out");
//
//        }
//
//
//    }
//}
