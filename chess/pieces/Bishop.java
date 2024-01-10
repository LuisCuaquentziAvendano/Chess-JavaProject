package chess.pieces;
import java.util.ArrayList;
import chess.board.*;
import chess.rules.Rules;

public class Bishop extends Piece {
	
	public Bishop(int x, EnumColors color) {
		super(x, color == EnumColors.WHITE ? Board.LOW_LIMIT : Board.HIGH_LIMIT, color);
	}
	
	public Bishop(int x, int y, EnumColors color) {
		super(x, y, color);
	}
	
	@Override
	public EnumPieces getType() {
		return EnumPieces.BISHOP;
	}
	
	@Override
	public Bishop clone() {
		return new Bishop(getX(), getY(), getColor());
	}
	
	@Override
	public ArrayList<int[]> getValidMoves(Board board, boolean nextMove) {
		return getBishopMoves(getX(), getY(), getColor(), board, nextMove);
	}
	
	// This is a static method because it is used by the queen
	public static ArrayList<int[]> getBishopMoves(int x, int y, EnumColors color, Board board, boolean nextMove) {
		ArrayList<int[]> validMoves = new ArrayList<>();
		for (int [] move : diagonalMoves(x, y, color, board, 1, 1, nextMove))
			validMoves.add(move);
		for (int [] move : diagonalMoves(x, y, color, board, -1, 1, nextMove))
			validMoves.add(move);
		for (int [] move : diagonalMoves(x, y, color, board, -1, -1, nextMove))
			validMoves.add(move);
		for (int [] move : diagonalMoves(x, y, color, board, 1, -1, nextMove))
			validMoves.add(move);
		return validMoves;
	}
	
	private static ArrayList<int[]> diagonalMoves(int x, int y, EnumColors color, Board board, int dx, int dy, boolean nextMove) {
		ArrayList<int[]> validMoves = new ArrayList<>();
		Board boardCopy;
		boolean pieceEaten = false;
		int i, newX, newY;
		Piece piece;
		for (i = 1; i <= Board.HIGH_LIMIT; i++) {  // Project the movement
			newX = x + i * dx;
			newY = y + i * dy;
			if (! Board.positionInsideBoard(newX, newY))
				break;  // Out of board
			piece = board.getPiece(newX, newY);
			if (piece != null) {  // There is a piece on the way of bishop
				if (piece.getColor() == color)
					break;  // They are the same color, can not eat
				else
					pieceEaten = true;  // Eat this piece and stop moving
			}
			if (nextMove) {
				boardCopy = board.cloneBoard();
				boardCopy.movePiece(x, y, newX, newY);
				if (Rules.kingInDanger(boardCopy, color))
					continue;  // King would be attacked
			}
			validMoves.add(new int[] {newX, newY});
			if (pieceEaten)
				break;  // There is a piece on the way
		}
		return validMoves;
	}
}
