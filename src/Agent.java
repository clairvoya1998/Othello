 
public interface Agent {

	/**
	 * Method to make a move. game state is stored in the class variables
	 *
	 * Method for you to implement. You want to make your modifications here.
	 *
	 * @return Returns a move.
	 */
	Coordinate findMove(char[][] board, char piece);
}