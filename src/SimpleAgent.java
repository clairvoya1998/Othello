import java.util.ArrayList;

/**
 * Created by xw37 on 20/04/17.
 *
 * Dummy AI.
 */
public class SimpleAgent implements Agent {
    @Override
    public Coordinate findMove(char[][] board, char piece) {
        return simpleDecision(board, piece);
    }

    public Coordinate simpleDecision(char[][] board, char piece) {
        ArrayList<Coordinate> moveList = Evaluation.genPriorityMoves(board, piece);
        if (moveList.isEmpty()) {
            return null;
        }
        Coordinate bestMove = moveList.get(0);
        return bestMove;
    }
}
