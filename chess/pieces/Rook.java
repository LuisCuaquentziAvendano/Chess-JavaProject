package chess.pieces;
import java.util.ArrayList;
import chess.board.*;
import chess.rules.Rules;

public class Rook extends Piece {
	private boolean hasBeenMoved = false;
	
	public Rook(int x, EnumColors color) {
		super(x, color == EnumColors.WHITE ? Board.LOW_LIMIT : Board.HIGH_LIMIT, color);
	}
	
	public Rook(int x, int y, EnumColors color, boolean hasBeenMoved) {
		super(x, y, color);
		this.hasBeenMoved = hasBeenMoved;
	}
	
	@Override
	public EnumPieces getType() {
		return EnumPieces.ROOK;
	}
	
	// A castling only can be made if the rook has not been moved
	@Override
	public boolean getHasBeenMoved() {
		return hasBeenMoved;
	}
	
	@Override
	public void setHasBeenMoved() {
		hasBeenMoved = true;
	}
	
	@Override
	public Rook clone() {
		return new Rook(getX(), getY(), getColor(), hasBeenMoved);
	}
	
	@Override
	public ArrayList<int[]> getValidMoves(Board board, boolean nextMove) {
		return getRookMoves(getX(), getY(), getColor(), board, nextMove);
	}
	
	// This is a static method because it is used by the queen
	public static ArrayList<int[]> getRookMoves(int x, int y, EnumColors color, Board board, boolean nextMove) {
		ArrayList<int[]> validMoves = new ArrayList<int[]>();
		for (int [] move : straightMoves(x, y, color, board, true, nextMove))
			validMoves.add(move);
		for (int [] move : straightMoves(x, y, color, board, false, nextMove))
			validMoves.add(move);
		return validMoves;
	}
	
	private static ArrayList<int[]> straightMoves(int x, int y, EnumColors color, Board board, boolean xAxis, boolean nextMove) {
		ArrayList<int[]> validMoves = new ArrayList<int[]>();
		Board boardCopy;
		boolean pieceEaten;
		int j, i, newX, newY;
		Piece piece;
		for (i = -1; i < 2; i += 2) {  // Go to positive and negative axis
			pieceEaten = false;
			for (j = 1; j <= Board.HIGH_LIMIT; j++) {  // Project the movement
				newX = x + i * j * (xAxis ? 1 : 0);
				newY = y + i * j * (xAxis ? 0 : 1);
				if (! Board.positionInsideBoard(newX, newY))
					break;  // Out of board
				piece = board.getPiece(newX, newY);
				if (piece != null) {  // There is a piece on the way of rook
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
		}
		return validMoves;
	}
}
