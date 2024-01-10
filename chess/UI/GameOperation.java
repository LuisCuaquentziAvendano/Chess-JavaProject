package chess.UI;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import chess.board.*;
import chess.pieces.*;
import chess.rules.Rules;

public class GameOperation extends UI {
	private Board board = new Board();
	private ArrayList<int[]> validMoves = new ArrayList<>();
	private int clicked[] = new int[2];
	private int lastMove[] = new int[4];
	private EnumColors turn = EnumColors.WHITE;
	private boolean firstMove = true, invertBoard = false;
	private String colorClicked = "#cccccc", colorValidMove = "#f4f680";
	private ArrayList<int[]> moves = new ArrayList<>();
	private int movesLen = 0;
	
	public GameOperation() {
		restartClicked();
	}
	
	// Initialize all variables
	private void initializeGame() {
		board.setDefaultBoard();
		validMoves.clear();
		turn = EnumColors.WHITE;
		firstMove = true;
		invertBoard = false;
		moves.clear();
		movesLen = 0;
	}
	
	// Function for a square on board clicked
	protected void squareClicked(int x, int y) {
		x = invert(x);  // Board could be inverted
		y = invert(y);
		boolean possibleMove = true;
		Piece piece = board.getPiece(x, y);  // Piece clicked
		if (piece != null) {  // A piece was clicked
			if (piece.getColor() == turn) {  // Get the moves of that piece
				possibleMove = false;
				validMoves = piece.getValidMoves(board, true);  // Do all stuff with the piece logic
			}
		}
		// It was clicked an empty square or a piece of the opposite color
		if (possibleMove) {
			if (correctMove(x, y)) {  // Move is in valid moves
				updateMoveHistory(x, y);  // Save this move
				makeMove(x, y);  // Move the piece on board
			}
			validMoves.clear();  // Wait for other piece clicked
		}
		clicked = new int[] {x, y};  // This is the last square clicked
		setBoardColors(! possibleMove);
	}
	
	private boolean correctMove(int x, int y) {
		for (int[] move : validMoves)
			if (move[0] == x && move[1] == y)
				return true;  // The move is in valid moves
		return false;  // This is not a valid move for the last piece clicked
	}
	
	private void makeMove(int x, int y) {
		Piece pawn;
		firstMove = false;
		turn = turn == EnumColors.WHITE ? EnumColors.BLACK : EnumColors.WHITE;  // Change turn
		lastMove = new int[] {clicked[0], clicked[1], x, y};  // Save last move
		board.movePiece(clicked[0], clicked[1], x, y);  // Do all stuff with the board logic
		if (Rules.trappedKing(board, turn)) {  // The opponent doesn't have valid movements, end of game
			if (Rules.kingInDanger(board, turn))
				checkmate(turn);
			else
				tiedGame();
		}
		pawn = Rules.coronation(board);
		if (pawn != null)  // There is a pawn on a border, it means coronation
			coronation(pawn);
	}
	
	private void tiedGame() {
		JOptionPane.showMessageDialog(null,
				"Tablas por rey ahogado",
				"EMPATE",
				JOptionPane.DEFAULT_OPTION);
	}
	
	private void checkmate(EnumColors color) {  // Consider the turn change
		JOptionPane.showMessageDialog(null,
				"¡Ganan " + (color == EnumColors.WHITE ? "negras!" : "blancas!"),
				"JAQUE MATE",
				JOptionPane.DEFAULT_OPTION);
	}
	
	private void coronation(Piece pawn) {  // Show options for coronation
		Piece piece = null;
		String options[] = {"Dama", "Caballo", "Torre", "Alfil"};
		int choice = JOptionPane.showOptionDialog(
					    null,
					    "Elige una pieza para la promoción:",
					    "CORONACION",
					    JOptionPane.DEFAULT_OPTION,
					    JOptionPane.INFORMATION_MESSAGE,
					    UIManager.getIcon("OptionPane.informationIcon"),
					    options,
					    options[0]
					);
		switch (choice) {  // Create the piece chosen
			case 1:
				piece = new Knight(pawn.getX(), pawn.getY(), pawn.getColor());
				break;
			case 2:
				piece = new Rook(pawn.getX(), pawn.getY(), pawn.getColor(), true);
				break;
			case 3:
				piece = new Bishop(pawn.getX(), pawn.getY(), pawn.getColor());
				break;
			default:
				piece = new Queen(pawn.getX(), pawn.getY(), pawn.getColor());
		}
		board.addPiece(piece);  // Add the piece to the board
	}
	
	private void updateMoveHistory(int x, int y) {
		while (moves.size() > movesLen)  // Remove unnecessary saved moves
			moves.remove(moves.size() - 1);
		moves.add(new int[] {clicked[0], clicked[1], x, y});
		movesLen++;  // Save this movement in history game
	}
	
	private void setBoardColors(boolean pieceClicked) {
		int i;
		defaultSquaresColor();  // The original color of squares
		if (! firstMove) {  // In first move, there is not a previous square clicked
			for (i = 0; i < lastMove.length; i += 2)  // Show last move made
				squares[invert(lastMove[i + 1])][invert(lastMove[i])].setBackground(Color.decode(colorClicked));
		}
		for (int[] move : validMoves)  // Show valid moves for piece clicked
			squares[invert(move[1])][invert(move[0])].setBackground(Color.decode(colorValidMove));
		if (pieceClicked)  // Show piece clicked
			squares[invert(clicked[1])][invert(clicked[0])].setBackground(Color.decode(colorClicked));
		setImagesUI();  // Update images on UI
		repaint();  // Show changes on UI
	}
	
	// This function is to turn the position (x, y)
	// to the logic used in all game (white pieces in front)
	private int invert(int position) {
		if (invertBoard)  // Black pieces in front
			return Board.HIGH_LIMIT - position;
		return position;  // White pieces in front
	}
	
	// Undo a movement
	protected void backClicked() {
		int i = 0, aux = movesLen - 1;
		ArrayList<int[]> aux2 = new ArrayList<>();
		boolean aux3 = invertBoard;
		aux2.addAll(moves);
		if (movesLen <= 0)  // There are no more moves to undo
			return;
		initializeGame();  // Restart all variables
		movesLen = aux;  // Get variables saved before restart
		moves.addAll(aux2);
		invertBoard = aux3;
		while (i < movesLen) {  // Do all moves until the previous
			clicked = new int[] {moves.get(i)[0], moves.get(i)[1]};
			makeMove(moves.get(i)[2], moves.get(i)[3]);
			i++;
		}
		setBoardColors(false);
	}
	
	// Redo a movement
	protected void nextClicked() {
		if (movesLen >= moves.size())  // This is the last move
			return;
		clicked = new int[] {moves.get(movesLen)[0], moves.get(movesLen)[1]};
		makeMove(moves.get(movesLen)[2], moves.get(movesLen)[3]);
		movesLen++;  // Make the next move saved
		validMoves.clear();
		setBoardColors(false);
	}
	
	// Invert the position of pieces on UI
	protected void invertClicked() {
		invertBoard = ! invertBoard;
		validMoves.clear();
		setBoardColors(false);
	}
	
	protected void restartClicked() {
		initializeGame();  // Initialize all variables
		setBoardColors(false);  // Set default colors
	}
	
	private void setImagesUI() {  // Show pieces on UI
		int x, y;
		Piece piece;
		for (y = 0; y < 8; y++) {
			for (x = 0; x < 8; x++) {
				piece = board.getPiece(x, y);
				if (piece == null)  // Empty square
					squares[invert(y)][invert(x)].setIcon(null);
				else  // There is a piece
					squares[invert(y)][invert(x)].setIcon(
							getImage(piece.getType(), piece.getColor()));
			}
		}
	}
}
