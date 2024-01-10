package chess.pieces;
import java.util.ArrayList;
import chess.board.*;

public class Piece {
	private byte x, y;
	EnumColors color;
	
	public Piece(int x, int y, EnumColors color) {
		setPosition(x, y);
		if (color == EnumColors.BLACK || color == EnumColors.WHITE)
			this.color = color;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public EnumColors getColor() {
		return color;
	}
	
	// Each piece returns its own type
	public EnumPieces getType() {
		return EnumPieces.NONE;
	}
	
	// This is used for kings, pawns and rooks
	public boolean getHasBeenMoved() {
		return true;
	}
	
	public void setHasBeenMoved() {
	}
	
	// This is used for pawns
	public boolean getDoubleBox() {
		return false;
	}
	
	public void setDoubleBox() {
	}
	
	// This is used for pawns
	public int getMovesSinceDB() {
		return 0;
	}
	
	public void addMoveSinceDB() {
	}
	
	// Each piece finds and returns its own valid moves
	// nextMove = true if the search of valid moves is due to a piece
	// clicked on UI, otherwise, it is due to the function kingInDanger
	// looking for a checkmate. nextMove avoids an infinite recursion
	public ArrayList<int[]> getValidMoves(Board board, boolean nextMove) {
		return new ArrayList<>();
	}
	
	// Each piece clones all its attributes
	public Piece clone() {
		return new Piece(x, y, color);
	}
	
	// This function sets the new position of the piece
	public void setPosition(int x, int y) {
		if (Board.positionInsideBoard(x, y)) {
			this.x = (byte) x;
			this.y = (byte) y;
		}
	}
}
