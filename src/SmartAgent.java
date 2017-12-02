import java.util.ArrayList;
import java.util.HashMap;

/***
 * Slightly smarter AI.
 */
public class SmartAgent implements Agent {
        
    static final int INFINITY = 1000000;
    
    private int mMaxPly = 5;
    
    private int searchTime;
    
    private boolean maximizer;
    
    private int depth;
    
    private long timeMillis;
    
    public SmartAgent() {
    }
    
    @Override
    public Coordinate findMove(char[][] board, char piece) {
        //System.out.println(1);
        Coordinate move = smartDecision(board, piece);
        //System.out.println(2);
        return move;
    }
    
    public Coordinate smartDecision(char[][] board, char piece){
        MoveScore moveScore = alphaBeta(board,0,-INFINITY,INFINITY,piece);
        //System.out.println(moveScore.getMove());
        return moveScore.getMove();
    }
    /**
     * Searching the move using alpha beta algorithm.
     * @param board the state of game
     * @param ply the depth of searching
     * @param alpha the boundary
     * @param beta the boundary
     * @return the pair of move and score
     */
    public MoveScore alphaBeta(char[][] board, int ply, int alpha, int beta, char piece){

        char oppPiece = (piece == Reversi.BLACK_PIECE) ? Reversi.WHITE_PIECE : Reversi.BLACK_PIECE;
        
        // Check if we have done recursing
        if (ply==mMaxPly){
            return new MoveScore(null, Evaluation.evaluateBoard(board, piece, oppPiece));
        }
            
        int currentScore;
        int bestScore = -INFINITY;
        Coordinate bestMove;
        int adaptiveBeta = beta;    // Keep track the test window value
        
        // Generates all possible moves
        ArrayList<Coordinate> moveList = Evaluation.genPriorityMoves(board, piece);
        if (moveList.isEmpty())
            return new MoveScore(null, bestScore);
        bestMove = moveList.get(0);
        
        // Go through each move
        for(int i = 0;i < moveList.size();i++){
            Coordinate move = moveList.get(i);
            char[][] newBoard = new char[8][8];
            for (int r = 0; r < 8; ++r)
                for (int c=0; c < 8; ++c)
                    newBoard[r][c] = board[r][c];
            //System.out.println(3);
            Reversi.effectMove(newBoard, piece, move.getX(), move.getY());
            //System.out.println(4);

            // Recurse
            MoveScore current = alphaBeta(newBoard, ply+1, -adaptiveBeta, - Math.max(alpha,bestScore), oppPiece);
            
            currentScore = - current.getScore();
            
            // Update bestScore
            if (currentScore>bestScore){
                // if in 'narrow-mode' then widen and do a regular AB negamax search
                if (adaptiveBeta == beta || ply>=(mMaxPly-2)){
                    bestScore = currentScore;
                    bestMove = move;
                }else{ // otherwise, we can do a Test
                    current = alphaBeta(newBoard, ply+1, -beta, -currentScore, oppPiece);
                    bestScore = - current.getScore();
                    bestMove = move;
                }
                
                // If we are outside the bounds, the prune: exit immediately
                if(bestScore>=beta){
                    return new MoveScore(bestMove,bestScore);
                }
                
                // Otherwise, update the window location
                adaptiveBeta = Math.max(alpha, bestScore) + 1;
            }
        }
        return new MoveScore(bestMove,bestScore);
    }
}
