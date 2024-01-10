package chess.pieces;
import java.util.ArrayList;
import chess.board.*;
import chess.rules.Rules;

public class King extends Piece {
	private boolean hasBeenMoved = false;
	
	public King(int x, EnumColors color) {
		super(x, color == EnumColors.WHITE ? Board.LOW_LIMIT : Board.HIGH_LIMIT, color);
	}
	
	public King(int x, int y, EnumColors color, boolean hasBeenMoved) {
		super(x, y, color);
		this.hasBeenMoved = hasBeenMoved;
	}
	
	@Override
	public EnumPieces getType() {
		return EnumPieces.KING;
	}
	
	// A castling only can be made if the king has not been moved
	@Override
	public void setHasBeenMoved() {
		hasBeenMoved = true;
	}
	
	@Override
	public King clone() {
		return new King(getX(), getY(), getColor(), hasBeenMoved);
	}
	
	// This function return all possible moves of this piece as pairs (x, y)
	@Override
	public ArrayList<int[]> getValidMoves(Board board, boolean nextMove) {
		int x, y;
		EnumColors color = getColor();
		Piece piece;
		ArrayList<int[]> validMoves = new ArrayList<>();
		for (int [] move : traditionalMove(board, nextMove))
			validMoves.add(move);		
		if (hasBeenMoved)
			return validMoves;  // King should not be moved for castling
		if (nextMove)
			if (Rules.kingInDanger(board, color))
				return validMoves;  // King should not be threatened for castling
		for (y = Board.LOW_LIMIT; y <= Board.HIGH_LIMIT; y++) {
			for (x = Board.LOW_LIMIT; x <= Board.HIGH_LIMIT; x++) {
				piece = board.getPiece(x, y);  // Search the rooks of the same color
				if (piece == null)
					continue;
				if (piece.getType() != EnumPieces.ROOK || piece.getColor() != color)
					continue;
				for (int [] move : castlingMove(board, piece, nextMove))
					validMoves.add(move);
			}
		}
		return validMoves;
	}
	
	private ArrayList<int[]> traditionalMove(Board board, boolean nextMove) {
		int dx, dy, x = getX(), y = getY();
		EnumColors color = getColor();
		Piece nextTo;
		ArrayList<int[]> validMoves = new ArrayList<>();
		Board boardCopy;
		for (dx = -1; dx < 2; dx++) {
			for (dy = -1; dy < 2; dy++) {
				if (! Board.positionInsideBoard(x + dx, y + dy))
					continue;  // Out of board
				if (dx == 0 && dy == 0)
					continue;  // This is the current position
				nextTo = board.getPiece(x + dx, y + dy);
				if (nextTo != null)  // There is a piece on this position
					if (nextTo.getColor() == color)
						continue;  // They are the same color, can not eat
				if (nextMove) {
					boardCopy = board.cloneBoard();
					boardCopy.movePiece(x, y, x + dx, y + dy);
					if (Rules.kingInDanger(boardCopy, color))
						continue;  // King would be attacked
				}
				validMoves.add(new int[] {x + dx, y + dy});
			}
		}
		return validMoves;
	}
	
	private ArrayList<int[]> castlingMove(Board board, Piece rook, boolean nextMove) {
		int xKing = getX(), yKing = getY(), i;
		ArrayList<int[]> validMove = new ArrayList<>();
		if (rook.getHasBeenMoved())  // Rook should not be moved for castling
			return validMove;
		if (! freeWayCastling(board, rook, nextMove))
			return validMove;  // There is a square occupied or attacked between king and rook
		// Check if the king should be moved to left or right
		i = xKing > rook.getX() ? -1 : 1;
		validMove.add(new int[] {xKing + 2*i, yKing});
		return validMove;
	}
	
	// This function checks if there are not pieces between the king
	// and the rook. It also checks if the king is safe during its
	// whole move. This 2 conditions are necessary to do a castling
	private boolean freeWayCastling(Board board, Piece rook, boolean nextMove) {
		int xKing = getX(), yKing = getY(), i, j;
		EnumColors color = getColor();
		Board boardCopy;		
		// This is for check the left or right side
		j = xKing > rook.getX() ? -1 : 1;
		for (i = 1; i < 3; i++) {  // Check the squares on the king path
			if (board.getPiece(xKing + i*j, yKing) != null)
				return false;  // There is a piece between king and rook
			if (nextMove) {
				boardCopy = board.cloneBoard();
				boardCopy.movePiece(xKing, yKing, xKing + i*j, yKing);
				if (Rules.kingInDanger(boardCopy, color))
					return false;  // There is an attacked square
			}
		}
		return true;
	}
}
