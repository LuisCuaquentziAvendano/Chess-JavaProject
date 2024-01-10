package chess.pieces;
import java.util.ArrayList;
import chess.board.*;
import chess.rules.Rules;

public class Pawn extends Piece {
	private boolean hasBeenMoved = false;
	private boolean doubleBox = false;
	private int movesSinceDB = 0;
	
	public Pawn(int x, EnumColors color) {
		super(x, color == EnumColors.WHITE ? Board.LOW_LIMIT + 1 : Board.HIGH_LIMIT - 1, color);
	}
	
	public Pawn(int x, int y, EnumColors color, boolean hasBeenMoved, boolean doubleBox, int movesSinceDB) {
		super(x, y, color);
		this.hasBeenMoved = hasBeenMoved;
		this.doubleBox = doubleBox;
	}
	
	@Override
	public EnumPieces getType() {
		return EnumPieces.PAWN;
	}
	
	// A pawn can do a double box move only its first move
	@Override
	public boolean getHasBeenMoved() {
		return hasBeenMoved;
	}
	
	@Override
	public void setHasBeenMoved() {
		hasBeenMoved = true;
	}
	
	// This is for know if the pawn moved 2 boxes its first move
	@Override
	public boolean getDoubleBox() {
		return doubleBox;
	}
	
	@Override
	public void setDoubleBox() {
		doubleBox = true;
	}
	
	// A pawn can eat by pass another pawn only on the next move to
	// the oponent's double box move
	@Override
	public int getMovesSinceDB() {
		return movesSinceDB;
	}
	
	@Override
	public void addMoveSinceDB() {
		if (doubleBox)
			movesSinceDB++;
	}
	
	@Override
	public Pawn clone() {
		return new Pawn(getX(), getY(), getColor(), hasBeenMoved, doubleBox, movesSinceDB);
	}
	
	@Override
	public ArrayList<int[]> getValidMoves(Board board, boolean nextMove) {
		ArrayList<int[]> validMoves = new ArrayList<>();
		int i;
		for (i = 1; i < 3; i++) {  // Move 1 or 2 boxes to front
			for (int [] move : frontMove(board, i, nextMove))
				validMoves.add(move);
		}
		for (i = -1; i < 2; i += 2) {  // Move on diagonal to left or right
			for (int [] move : diagonalMove(board, i, nextMove))
				validMoves.add(move);
		}
		return validMoves;
	}
	
	// Front moves of pawns can be done in 2 cases:
	// traditional move and initial move (2 boxes)
	private ArrayList<int[]> frontMove(Board board, int dy, boolean nextMove) {
		int x = getX(), y = getY();
		EnumColors color = getColor();
		Piece inFront;
		ArrayList<int[]> validMove = new ArrayList<int[]>();
		Board boardCopy;
		dy = color == EnumColors.WHITE ? dy : -dy;
		if (! Board.positionInsideBoard(x, y + dy))
			return validMove;  // Out of board
		if (Math.abs(dy) == 2 && getHasBeenMoved())
			return validMove;  // The double box move is for first movement
		inFront = board.getPiece(x, y + dy);
		if (inFront != null)
			return validMove;  // This square is occupied
		inFront = board.getPiece(x, y + dy/2);
		if (Math.abs(dy) == 2 && inFront != null)
			return validMove;  // The 2 squares in front should be free
		if (nextMove) {
			boardCopy = board.cloneBoard();
			boardCopy.movePiece(x, y, x, y + dy);
			if (Rules.kingInDanger(boardCopy, color))
				return validMove;  // King would be attacked
		}
		validMove.add(new int[] {x, y + dy});
		return validMove;
	}
	
	// Diagonal moves of pawns can be done in 2 cases:
	// eat traditionally and eat by pass
	private ArrayList<int[]> diagonalMove(Board board, int dx, boolean nextMove) {
		int x = getX(), y = getY();
		EnumColors color = getColor();
		int dy = color == EnumColors.WHITE ? 1 : -1;
		ArrayList<int[]> validMove = new ArrayList<>();
		Piece inDiagonal, nextTo;
		Board boardCopy;
		if (! Board.positionInsideBoard(x + dx, y + dy))
			return validMove;  // Out of board
		inDiagonal = board.getPiece(x + dx, y + dy);
		nextTo = board.getPiece(x + dx, y);
		if (inDiagonal == null && nextTo == null)
			return validMove;  // Both squares can not be free, this move is for eat
		if (inDiagonal != null) {  // There is a piece on this position
			if (inDiagonal.getColor() == color)
				return validMove;  // They are the same color, can not eat
		}
		// The other way this position is valid is only if there is
		// another pawn next to the current, they are different
		// colors and the other pawn can be eaten by pass
		else {
			if (nextTo.getType() != EnumPieces.PAWN
					|| nextTo.getColor() == color
					|| ! (nextTo.getMovesSinceDB() == 0
					&& nextTo.getDoubleBox()))
				return validMove;
		}
		if (nextMove) {
			boardCopy = board.cloneBoard();
			boardCopy.movePiece(x, y, x + dx, y + dy);
			if (Rules.kingInDanger(boardCopy, color)) {
				return validMove;  // This position is attacked
			}
		}
		validMove.add(new int[] {x + dx, y + dy});
		return validMove;
	}
}
