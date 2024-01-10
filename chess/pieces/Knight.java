package chess.pieces;
import java.util.ArrayList;
import chess.board.*;
import chess.rules.Rules;

public class Knight extends Piece {
	
	public Knight(int x, EnumColors color) {
		super(x, color == EnumColors.WHITE ? Board.LOW_LIMIT : Board.HIGH_LIMIT, color);
	}
	
	public Knight(int x, int y, EnumColors color) {
		super(x, y, color);
	}
	
	@Override
	public EnumPieces getType() {
		return EnumPieces.KNIGHT;
	}
	
	@Override
	public Knight clone() {
		return new Knight(getX(), getY(), getColor());
	}
	
	@Override
	public ArrayList<int[]> getValidMoves(Board board, boolean nextMove) {
		ArrayList<int[]> validMoves = new ArrayList<>();
		Board boardCopy;
		int i, x = getX(), y = getY(), newX, newY;
		EnumColors color = getColor();
		Piece piece;
        int[] rowMoves = { -2, -2, -1, -1, 1, 1, 2, 2 };  // All possible moves
        int[] columnMoves = { -1, 1, -2, 2, -2, 2, -1, 1 };
        for (i = 0; i < rowMoves.length; i++) {
        	newX = x + columnMoves[i];
        	newY = y + rowMoves[i];
        	if(! Board.positionInsideBoard(newX, newY))
        		continue;  // Out of board
        	piece = board.getPiece(newX, newY);
        	if (piece != null)  // There is a piece on this position
        		if (piece.getColor() == color)
        			continue;  // They are the same color, can not eat
        	if (nextMove) {
        		boardCopy = board.cloneBoard();
        		boardCopy.movePiece(x, y, newX, newY);
        		if (Rules.kingInDanger(boardCopy, color))
        			continue;  // King would be attacked
        	}
        	validMoves.add(new int[] {newX, newY});
        }
		return validMoves;
	}
}
