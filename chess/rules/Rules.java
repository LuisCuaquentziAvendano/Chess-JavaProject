package chess.rules;
import java.util.ArrayList;
import chess.board.*;
import chess.pieces.*;

public class Rules {
	// This function checks if the king of a specified color is threatened
	// by other piece on the board, this could be a check,
	// a checkmate or a tied game
	public static boolean kingInDanger(Board board, EnumColors color) {
		int x, y;
		Piece piece, king = board.getKing(color);
		int xKing = king.getX(), yKing = king.getY();
		ArrayList<int[]> validMoves;
		for (y = Board.LOW_LIMIT; y <= Board.HIGH_LIMIT; y++) {
			for (x = Board.LOW_LIMIT; x <= Board.HIGH_LIMIT; x++) {
				piece = board.getPiece(x, y);  // Check all moves of all pieces
				if (piece == null)
					continue;
				if (piece.getColor() == color)
					continue;  // Both pieces are the same color
				validMoves = piece.getValidMoves(board, false);
				for (int[] move : validMoves) {
					if (move[0] == xKing && move[1] == yKing)	
						return true;  // This piece threatens the king
				}
			}
		}
		return false;
	}
	
	// This function checks if the king is trapped, that means checkmate
	// or tied game, it depends on whether the king is in danger or not
	public static boolean trappedKing(Board board, EnumColors color) {
		int x, y;
		Piece piece;
		for (y = Board.LOW_LIMIT; y <= Board.HIGH_LIMIT; y++) {
			for (x = Board.LOW_LIMIT; x <= Board.HIGH_LIMIT; x++) {
				piece = board.getPiece(x, y);  // Check the number of moves of all pieces
				if (piece == null)
					continue;
				if (piece.getColor() != color)
					continue;
				if (piece.getValidMoves(board, true).size() > 0)
					return false;
			}
		}
		return true;
	}
	
	// This function checks if there is a pawn on the limits of the board
	public static Piece coronation(Board board) {
		int x, y;
		Piece piece = null;
		for (y = Board.LOW_LIMIT; y <= Board.HIGH_LIMIT; y++) {
			for (x = Board.LOW_LIMIT; x <= Board.HIGH_LIMIT; x++) {
				piece = board.getPiece(x, y);  // Check all pieces
				if (piece == null)
					continue;
				// The piece is a pawn an it is on a border
				if ((piece.getY() == Board.LOW_LIMIT
						|| piece.getY() == Board.HIGH_LIMIT)
						&& piece.getType() == EnumPieces.PAWN)
					return piece;
			}
		}
		return null;
	}
}
