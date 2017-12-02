import java.util.ArrayList;
import java.util.PriorityQueue;

public class Evaluation {
	
	private static final int BOARD_SIZE = Reversi.BOARD_SIZE;
	
	
	private static int[][] BOARD_VALUE = {
		{100, -1, 5, 2, 2, 5, -1, 100},
		{-1, -10,1, 1, 1, 1,-10, -1},
		{5 , 1,  1, 1, 1, 1,  1,  5},
		{2 , 1,  1, 0, 0, 1,  1,  2},
		{2 , 1,  1, 0, 0, 1,  1,  2},
		{5 , 1,  1, 1, 1, 1,  1,  5},
		{-1,-10, 1, 1, 1, 1,-10, -1},
		{100, -1, 5, 2, 2, 5, -1, 100}};
	
	public static int evaluateBoard(char[][] board, char piece, char oppPiece) {
		int score = 0;
		for (int r = 0; r < BOARD_SIZE; ++r) {
			for (int c = 0; c < BOARD_SIZE; ++c) {
				if (board[r][c] == piece)
					score = score + BOARD_VALUE[r][c];
				else if (board[r][c] == oppPiece)
					score = score - BOARD_VALUE[r][c];
			}
		}
		return score;
	}
	
	public static ArrayList<Coordinate> genPriorityMoves(char[][] board, char piece) {
		ArrayList<Coordinate> moveList = Reversi.findValidMove(board, piece, false);
		PriorityQueue<MoveScore> moveQueue = new PriorityQueue<MoveScore>();
		for (int i=0; i < moveList.size(); ++i) {
			Coordinate move = moveList.get(i);
			MoveScore moveScore = new MoveScore(move, BOARD_VALUE[move.getX()][move.getY()]);
			moveQueue.add(moveScore);
		}
		
		moveList = new ArrayList<Coordinate>();
		while (!moveQueue.isEmpty()) {
			moveList.add(moveQueue.poll().getMove());
		}
		
		return moveList;
	}
	
	
}
