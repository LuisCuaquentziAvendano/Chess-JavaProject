package chess.pieces;
import java.util.ArrayList;

import chess.board.*;

public class Queen extends Piece {
	
	public Queen(int x, EnumColors color) {
		super(x, color == EnumColors.WHITE ? Board.LOW_LIMIT : Board.HIGH_LIMIT, color);
	}
	
	public Queen(int x, int y, EnumColors color) {
		super(x, y, color);
	}
	
	@Override
	public EnumPieces getType() {
		return EnumPieces.QUEEN;
	}
	
	@Override
	public Queen clone() {
		return new Queen(getX(), getY(), getColor());
	}
	
	// The queen gets its own valid movements with the methods of rook and bishop
	@Override
	public ArrayList<int[]> getValidMoves(Board board, boolean nextMove) {
		ArrayList<int[]> validMoves = new ArrayList<>();
		for (int [] move : Rook.getRookMoves(getX(), getY(), getColor(), board, nextMove))
			validMoves.add(move);
		for (int [] move : Bishop.getBishopMoves(getX(), getY(), getColor(), board, nextMove))
			validMoves.add(move);
		return validMoves;
	}
}
