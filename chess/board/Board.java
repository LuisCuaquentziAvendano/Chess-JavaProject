package chess.board;
import chess.pieces.*;

public class Board {
	public static final byte LOW_LIMIT = 0, HIGH_LIMIT = 7;
	private Piece board[][] = new Piece[8][8];
	// This is the virtual board for the game, here is where all logic happens,
	// like moves, eaten pieces, check valid moves and all rules of chess
	
	// Remove all images on board
	public void clear() {
		int x, y;
		for (y = 0; y < 8; y++)
			for (x = 0; x < 8; x++)
				board[y][x] = null;
	}
	
	// Set all pieces on board to start the game
	public void setDefaultBoard() {
		int i;
		EnumColors colors[] = {EnumColors.WHITE, EnumColors.BLACK};
		clear();
		for (EnumColors color : colors) {
			for (i = 0; i < 8; i++)
				addPiece(new Pawn(LOW_LIMIT + i, color));
			addPiece(new Rook(LOW_LIMIT, color));
			addPiece(new Rook(HIGH_LIMIT, color));
			addPiece(new Knight(LOW_LIMIT + 1, color));
			addPiece(new Knight(HIGH_LIMIT - 1, color));
			addPiece(new Bishop(LOW_LIMIT + 2, color));
			addPiece(new Bishop(HIGH_LIMIT - 2, color));
		}
		addPiece(new Queen(LOW_LIMIT + 3, EnumColors.WHITE));
		addPiece(new King(HIGH_LIMIT - 3, EnumColors.WHITE));
		addPiece(new Queen(LOW_LIMIT + 3, EnumColors.BLACK));
		addPiece(new King(HIGH_LIMIT - 3, EnumColors.BLACK));
	}
	
	// Add a piece to the board, the piece already contains the position
	public void addPiece(Piece piece) {
		if (positionInsideBoard(piece.getX(), piece.getY()))
			board[piece.getY()][piece.getX()] = piece;
	}
	
	// Get a piece of the board given its position
	public Piece getPiece(int x, int y) {
		Piece piece = null;
		if (positionInsideBoard(x, y))
			piece = board[y][x];
		return piece;
	}
	
	// Remove a piece of the board
	public void removePiece(Piece piece) {
		if (positionInsideBoard(piece.getX(), piece.getY()))
			board[piece.getY()][piece.getX()] = null;
	}
	
	// This function searches the king of the specified color and returns it
	public Piece getKing(EnumColors color) {
		int x, y;
		Piece piece = null;
		for (y = 0; y < 8; y++) {
			for (x = 0; x < 8; x++) {
				piece = board[y][x];
				if (piece == null)
					continue;
				if (piece.getType() == EnumPieces.KING && piece.getColor() == color)
					return piece;
			}
		}
		return piece;
	}
	
	// This function is for clone the board, it is very useful to get valid moves of a piece
	public Board cloneBoard() {
		int x, y;
		Board copy = new Board();
		for (y = 0; y < 8; y++)
			for (x = 0; x < 8; x++)
				copy.board[y][x] = board[y][x] == null ? null : board[y][x].clone();
		return copy;
	}
	
	// Check if a position (x, y) is inside the virtual board limits
	public static boolean positionInsideBoard(int x, int y) {
		return x >= LOW_LIMIT && x <= HIGH_LIMIT &&
				y >= LOW_LIMIT && y <= HIGH_LIMIT;
	}
	
	// This function moves a piece to a new position
	// Other pieces could be changed in cases
	// like pawn eaten by pass, castling and normal eaten pieces
	public void movePiece(int oldX, int oldY, int newX, int newY) {
		int x, y;
		Piece toChange, piece;
		toChange = getPiece(oldX, oldY);  // Piece to move
		if (! positionInsideBoard(oldX, oldY))  // Do all necessary validations
			return;
		if (! positionInsideBoard(newX, newY))
			return;
		if (oldX == newX && oldY == newY)
			return;
		if (toChange == null)
			return;
		removePiece(toChange);  // Remove the piece of the current position
		toChange.setPosition(newX, newY);  // Set its new position
		addPiece(toChange);  // Put this piece on the new position on board
		// Update a pawns' attribute
		for (y = LOW_LIMIT; y <= HIGH_LIMIT; y++) {
			for (x = LOW_LIMIT; x <= HIGH_LIMIT; x++) {
				piece = getPiece(x, y);
				if (piece == null)
					continue;
				// Eat by pass can only be done immediately after the initial move
				if (piece.getType() == EnumPieces.PAWN)
					piece.addMoveSinceDB();
			}
		}
		if (toChange.getType() == EnumPieces.KING)
			checkCastling(oldX, oldY, newX, newY);  // Check special move
		else if (toChange.getType() == EnumPieces.PAWN)
			checkEatenByPass(oldX, oldY, newX, newY);  // Check special move
		else if (toChange.getType() == EnumPieces.ROOK)
			toChange.setHasBeenMoved();  // This is useful for castling
	}
	
	// This function implements a way to move the rook to its correct
	// position if the move made (to the king) was a castling
	private void checkCastling(int oldX, int oldY, int newX, int newY) {
		int oldXRook, dxRook;
		Piece rook;
		getPiece(newX, newY).setHasBeenMoved();  // King has been moved
		if (Math.abs(oldX - newX) == 1 || Math.abs(oldY - newY) == 1)
			return;  // This is a traditional move
		// Find the rook to complete the castling
		oldXRook = newX < HIGH_LIMIT/2 ? LOW_LIMIT : HIGH_LIMIT;
		dxRook = newX < HIGH_LIMIT/2 ? 1 : -1;
		rook = getPiece(oldXRook, oldY);  // Rook implicated on castling
		if (rook == null)
			return;  // Rook should not be null, but this is for safe code
		removePiece(rook);
		rook.setPosition(newX + dxRook, newY);
		addPiece(rook);
	}
	
	// This function implements a way to delete a pawn on the board
	// if it was eaten by pass, also updates other important
	// attributes of pawns
	private void checkEatenByPass(int oldX, int oldY, int newX, int newY) {
		Piece pawn, next;
		pawn = getPiece(newX, newY);  // This the pawn moved
		pawn.setHasBeenMoved();
		if (Math.abs(oldY - newY) == 2) {
			pawn.setDoubleBox();
			return;  // This pawn was moved 2 boxes its first move
		}
		if (newX - oldX == 0)
			return;  // This pawn made a traditional front move
		// The move was on diagonal
		next = getPiece(newX, oldY);  // Get the piece next to the pawn moved
		if (next == null)
			return;  // It is an empty square
		if (next.getType() == EnumPieces.PAWN
				&& next.getMovesSinceDB() == 1
				&& next.getDoubleBox())
			removePiece(next);  // A pawn was eaten by pass, remove it
	}
}
