/**
 * Basic Delierable.
 * 
 * @xw37
 * @version 1.0
 */
import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

public class Reversi extends Observable{

    /** The unique instance of this class */
    private static Reversi Instance;

    /** Game State */
    public static final int PLAYING = 0;
    public static final int END = 1;

    /** number of rows */
    public static final int BOARD_SIZE = 8;

    /** piece represents black */
    public static final char BLACK_PIECE = 'b';

    /** piece represents white */
    public static final char WHITE_PIECE = 'w';

    /** susggest piece for black */
    public static final char SUGGEST_BLACK_PIECE = 'l';

    /** susggest piece for white */
    public static final char SUGGEST_WHITE_PIECE = 'h';

    /** empty piece */
    public static final char EMPTY_PIECE = '-';

    /** move offset for row */
    private static final int[] MOVE_ROW = {-1, -1, -1,  0,  0,  1,  1,  1};

    /** move offset for column */
    private static final int[] MOVE_COL = {-1,  0,  1, -1,  1, -1,  0,  1};


    /** whether it is black's turn to move */
    private boolean blackTurn;

    /** whether it is white's turn to move */
    private boolean whiteTurn;

    /** the board */
    private char[][] board;

    /** score of black piece */
    private int BlackScore;

    /** score of white piece */
    private int WhiteScore;

    /** state of the game */
    private int State;

    /** AI agent */

    private SimpleAgent simpleAgent = new SimpleAgent();
    private SmartAgent smartAgent;

    /** new piece position */
    private int NewPieceRow;
    private int NewPieceCol;

    /** whether a piece is changed*/
    private boolean[][] EffectedPiece;

    private Vector<String> MoveList;

    /** Private constructor */
    private Reversi() {
        init();
    }

    public static Reversi getInstance() {
        if (Instance == null)
            Instance = new Reversi();

        return Instance;
    }

    /** Initialize the board */
    private void init() {
        // init board
        board = new char[BOARD_SIZE][BOARD_SIZE];

        // init move list
        MoveList = new Vector<String>();
        
        // init effected pieces
        EffectedPiece = new boolean[BOARD_SIZE][BOARD_SIZE];

        // set up AI agent
        smartAgent = new SmartAgent();

       // blackTurn = true;
       // whiteTurn = false;
    }

    public char[][] getBoard() {
        return board;
    }

    /** Sets game state */
    public void setGameState(int state) {
        State = state;
    }
    
    /** Gets game state */
    public int getGameState() {
        return State;
    }

    /** Set whether white moves*/
    public void setWhiteTurn(boolean value) {
        whiteTurn = value;
    }

    /** Get whether white moves*/
    public boolean getWhiteTurn() {
        return whiteTurn;
    }

        /** Set whether black moves*/
    public void setBlackTurn(boolean value) {
        blackTurn = value;
    }

    /** Get whether white moves*/
    public boolean getBlackTurn() {
        return blackTurn;
    }

    /** Get white's score */
    public int getWhiteScore() {
        return WhiteScore;
    }

    /** Get black's score */
    public int getBlackScore() {
        return BlackScore;
    }


    public Vector<String> getMoveList() {
        return MoveList;
    }

    /** New game */
    public void newGame() {
        // reset the board
        initBoard();
        // reset effected pieces
        resetEffectedPieces();
        // black piece starts first
//        blackTurn = true;
        setBlackTurn(true);
//        whiteTurn = false;
        setWhiteTurn(false);
        System.out.println("New Game Start, Black Piece Moves First.");
        // set state
        setGameState(PLAYING);
        //State = PLAYING;
        stateChange();

        // get next move
        //myNextMove(AI);
    }

    /** Reset the board */
    private void initBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = EMPTY_PIECE;
            }
        }
        board[3][3] = WHITE_PIECE;
        board[4][4] = WHITE_PIECE;
        board[3][4] = BLACK_PIECE;
        board[4][3] = BLACK_PIECE;

        BlackScore = 2;
        WhiteScore = 2;

        NewPieceRow = -1;
        NewPieceCol = -1;

        MoveList.removeAllElements();
    }

    public void resetEffectedPieces() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                EffectedPiece[row][col] = false;
            }
        }
    }

    public void setEffectedPiece(int row, int col) {
        EffectedPiece[row][col] = true;
    }

    public boolean isEffectedPiece(int row, int col) {
        return EffectedPiece[row][col];
    }

    private void suggestPiece(char piece) {
        ArrayList<Coordinate> suggestPiece = findValidMove(board, piece, true);
        String str = String.format("Valid move: %s:\t%s", String.valueOf(piece).toUpperCase(), Coordinate.encode(suggestPiece.get(0).getX(), suggestPiece.get(0).getY()));
        for (int i = 1; i < suggestPiece.size(); i++) {
            String tempCoord = Coordinate.encode(suggestPiece.get(i).getX(), suggestPiece.get(i).getY());
            str = str + " or " + tempCoord;
        }
        System.out.println(str);
    }

    private void nextMove() {
        /**case : 2 players*/
            /** check which turn*/
        char piece = (blackTurn) ? BLACK_PIECE : WHITE_PIECE;
        if ((findValidMove(board, piece, true)).isEmpty()) {
            char opPiece = (piece == BLACK_PIECE) ? WHITE_PIECE : BLACK_PIECE;
            if ((findValidMove(board, opPiece, false)).isEmpty())
            {
                setGameState(END);
                //State = END;
                System.out.println("Game over.");
                System.out.println("Black score: " + getBlackScore());
                System.out.println("White score: " + getWhiteScore());
                char result = checkWinner();
                switch (result) {
                    case'b':
                        System.out.println("Black Win.");
                        break;
                    case'w':
                        System.out.println("White Win.");
                        break;
                    case'e':
                        System.out.println("Equal.");
                        break;
                    default:
                        System.out.println("Error.");
                }
                stateChange();
                System.exit(0);
                return;
            }
            changeTurn();
            nextMove();
        }
        else suggestPiece(piece);
    }

    private void myNextMove(boolean AI) {
       if (!AI) {
           nextMove();
        }
        else {
            getNextMove();
        }
    }

    /** Get next move */
    private void getNextMove() {
        if (!whiteTurn) {
            char piece = (blackTurn) ? BLACK_PIECE : WHITE_PIECE;
            if ((findValidMove(board, piece, true)).isEmpty()) {
                char opPiece = (piece == BLACK_PIECE) ? WHITE_PIECE : BLACK_PIECE;
                if ((findValidMove(board, opPiece, false)).isEmpty())
                {
                    State = END;
                    System.out.println("Game over.");
                    System.out.println("Black score: " + getBlackScore());
                    System.out.println("White score: " + getWhiteScore());
                    char result = checkWinner();
                    switch (result) {
                        case'b':
                            System.out.println("Black Win.");
                            break;
                        case'w':
                            System.out.println("White Win.");
                            break;

                        case'e':
                            System.out.println("Equal.");
                            break;
                        default:
                            System.out.println("Error.");
                    }
                    stateChange();
                    System.exit(0);
                    return;
                }
                changeTurn();
                getNextMove();
            }
        }
        else {
            /**Auto set: computer take white piece*/
            char piece = (blackTurn) ? BLACK_PIECE : WHITE_PIECE;
            char opPiece = (piece == BLACK_PIECE) ? WHITE_PIECE : BLACK_PIECE;

            // clear all suggested pieces
            for (int i=0 ;i < BOARD_SIZE; ++i)
                for (int j=0; j < BOARD_SIZE; ++j)
                    if (board[i][j] == SUGGEST_BLACK_PIECE || board[i][j] == SUGGEST_WHITE_PIECE)
                        board[i][j] = EMPTY_PIECE;

            // copy board to temp
            char[][] tempBoard = new char[8][8];
            for (int i=0; i< BOARD_SIZE; ++i)
                for (int j=0; j < BOARD_SIZE; ++j)
                    tempBoard[i][j] = board[i][j];

            // find optimal move
            Coordinate move = smartAgent.findMove(tempBoard, piece);
            if (move != null)
            {
                //System.out.println(move.getRow() + " " + move.getCol());
                effectMove(board, piece, move.getX(), move.getY());
                addToMoveList(piece, move.getX(), move.getY());
                NewPieceRow = move.getX();
                NewPieceCol = move.getY();
                stateChange();
                suggestPiece(opPiece);
            }
            // next move
            changeTurn();
            getNextMove();
        }
    }

    /** add a move to move list */
    private void addToMoveList(char piece, int row, int col) {
        String str = String.format("%s:\t%s", String.valueOf(piece).toUpperCase(), Coordinate.encode(row, col));
        System.out.println(str);
        MoveList.add(str);
    }

    /** change turn of playing */
    private void changeTurn() {
        blackTurn = !blackTurn;
        whiteTurn = !whiteTurn;
    }

    /** Calculate score */
    private void calScore() {
        BlackScore = 0;
        WhiteScore = 0;
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j)
            {
                if (board[i][j] == BLACK_PIECE)
                    BlackScore++;
                else if (board[i][j] == WHITE_PIECE)
                    WhiteScore++;
            }
    }

    /**
     * Finds valid moves for specific piece
     * @param board the board
     * @param piece the piece need to find move
     * @param isSuggest true to indicate suggested pieces on the board
     * @return an array list of moves
     */
     public static ArrayList<Coordinate> findValidMove(char[][] board, char piece, boolean isSuggest) {
        char suggestPiece = (piece == BLACK_PIECE) ? SUGGEST_BLACK_PIECE : SUGGEST_WHITE_PIECE;

        ArrayList<Coordinate> moveList = new ArrayList<>();
        for (int i = 0; i < 8; ++i)
            for (int j = 0; j < 8; ++j) {
                // clean the suggest piece before
                if (board[i][j] == SUGGEST_BLACK_PIECE || board[i][j] == SUGGEST_WHITE_PIECE)
                    board[i][j] = EMPTY_PIECE;

                if (isValidMove(board, piece, i, j))
                {
                    moveList.add(new Coordinate(i, j));

                    // if we want suggestion, mark on board
                    if (isSuggest)
                        board[i][j] = suggestPiece;
                }
            }

        return moveList;
    }

    /**
     * Check whether a move is valid
     * @param board the board
     * @param piece the piece need to check
     * @param row row of the move
     * @param col column of the move
     * @return true if the move is valid, false otherwise
     */
    public static boolean isValidMove(char[][] board, char piece, int row, int col) {
        // check whether this square is empty
        if (board[row][col] != EMPTY_PIECE)
            return false;

        char oppPiece = (piece == BLACK_PIECE) ? WHITE_PIECE : BLACK_PIECE;

        boolean isValid = false;
        // check 8 directions
        for (int i = 0; i < BOARD_SIZE; i++) {
            int curRow = row + MOVE_ROW[i];
            int curCol = col + MOVE_COL[i];
            boolean hasOppPieceBetween = false;
            while (curRow >=0 && curRow < BOARD_SIZE && curCol >= 0 && curCol < BOARD_SIZE) {

                if (board[curRow][curCol] == oppPiece)
                    hasOppPieceBetween = true;
                else if ((board[curRow][curCol] == piece) && hasOppPieceBetween)
                {
                    isValid = true;
                    break;
                }
                else
                    break;

                curRow += MOVE_ROW[i];
                curCol += MOVE_COL[i];
            }
            if (isValid)
                break;
        }

        return isValid;
    }

    public char checkWinner() {
        char result = ' ';
        if (getBlackScore() > getWhiteScore()) {
            result = 'b';
        }
        else if (getBlackScore() < getWhiteScore()) {
            result = 'w';
        }
        else if (getBlackScore() == getWhiteScore()) {
            result = 'e';
        }
        return result;
    }

    private static int counter = 0;
    /**
     * Effect the move
     * @param board the board
     * @param piece the piece of move
     * @param row row of the move
     * @param col column of the move
     * @return the new board after the move is affected
     */
    public static char[][] effectMove(char[][] board, char piece, int row, int col) {
        board[row][col] = piece;
        char oppPiece = (piece == BLACK_PIECE) ? WHITE_PIECE : BLACK_PIECE;

        Reversi.getInstance().resetEffectedPieces();

        // check 8 directions
        for (int i = 0; i < BOARD_SIZE; ++i) {
            int curRow = row + MOVE_ROW[i];
            int curCol = col + MOVE_COL[i];
            boolean hasOppPieceBetween = false;
            while (curRow >=0 && curRow < BOARD_SIZE && curCol >= 0 && curCol < BOARD_SIZE) {
//                System.out.println(counter++);
                // if empty square, break
                if (board[curRow][curCol] == EMPTY_PIECE)
                    break;

                if (board[curRow][curCol] == oppPiece)
                    hasOppPieceBetween = true;

                if ((board[curRow][curCol] == piece) && hasOppPieceBetween)
                {
                    int effectPieceRow = row + MOVE_ROW[i];
                    int effectPieceCol = col + MOVE_COL[i];
                    while (effectPieceRow != curRow || effectPieceCol != curCol)
                    {
                        if (board[effectPieceRow][effectPieceCol] == oppPiece) {
                            Reversi.getInstance().setEffectedPiece(effectPieceRow, effectPieceCol);
                            board[effectPieceRow][effectPieceCol] = piece;
                            effectPieceRow += MOVE_ROW[i];
                            effectPieceCol += MOVE_COL[i];
                        } else {
                            break;
                        }
                    }

                    break;
                }

                curRow += MOVE_ROW[i];
                curCol += MOVE_COL[i];

                //System.out.println(String.format("%s\t%s\t%s", curRow, curCol, BOARD_SIZE));
            }
        }

        return board;
    }

    /**
     * human move piece
     * @param row row of the move
     * @param col column of the move
     */
    public void movePiece(char[][] board, int row, int col, boolean AI) {

        //System.out.println("ell");
        char piece = (blackTurn) ? BLACK_PIECE : WHITE_PIECE;
        char suggestPiece = (blackTurn) ? SUGGEST_BLACK_PIECE : SUGGEST_WHITE_PIECE;
        if (board[row][col] == suggestPiece)
        {
            effectMove(board, piece, row, col);
            NewPieceRow = row;
            NewPieceCol = col;

            // add to move list
            addToMoveList(piece, row, col);
            // notify the observer
            stateChange();
            // change turn
            changeTurn();
            myNextMove(AI);
        }
        else {
            if (isValidMove(board, piece, row, col)) {
                board[row][col] = piece;
                //System.out.println("1");
                board = effectMove(board, piece, row, col);
                stateChange();
                addToMoveList(piece, row, col);
                changeTurn();
                myNextMove(AI);
            }
        }

    }

    private void stateChange() {
        calScore();
        setChanged();
        notifyObservers();
    }
    
   

}

