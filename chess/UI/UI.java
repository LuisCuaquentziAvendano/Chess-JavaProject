package chess.UI;
import java.awt.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import chess.board.*;
import chess.pieces.*;

public abstract class UI {
	private JFrame window = new JFrame("AJEDREZ");
	private int squareSize = 70;
	private String lightColor = "#e9edcc", darkColor = "#779954";
	private ImageIcon images[] = new ImageIcon[12];
	public JButton squares[][] = new JButton[8][8];
	
	public UI() {
		createWindow();
	}
	
	protected abstract void squareClicked(int x, int y);
	protected abstract void backClicked();
	protected abstract void nextClicked();
	protected abstract void invertClicked();
	protected abstract void restartClicked();
		
	private void createWindow() {
		int i, j;
		// The main window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setSize(1000, 700);
		window.getContentPane().setBackground(new Color(30,30,30));
		window.setLayout(null);
		
		// The container of all buttons
		JPanel chessboard = new JPanel();
		chessboard.setBounds(400,50,squareSize*8,squareSize*8);
		chessboard.setBackground(Color.cyan);
		chessboard.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		window.add(chessboard);

		// Button to undo a movement
		JButton back = new JButton();
		back.setBounds(1000, 540, squareSize, squareSize);
		back.setBackground(Color.LIGHT_GRAY);
		ImageIcon backIcon = new ImageIcon(getClass().getResource(
				"/chess/images/back.png"));
		back.setIcon(new ImageIcon(
				backIcon.getImage().getScaledInstance(squareSize, squareSize, Image.SCALE_SMOOTH)));
		window.add(back);
		back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	backClicked();
            }
        });
		
		// Button to redo a movement
		JButton next = new JButton();
		next.setBounds(1020 + squareSize, 540, squareSize, squareSize);
		next.setBackground(Color.LIGHT_GRAY);
		ImageIcon nextIcon = new ImageIcon(getClass().getResource(
				"/chess/images/next.png"));
		next.setIcon(new ImageIcon(
				nextIcon.getImage().getScaledInstance(squareSize, squareSize, Image.SCALE_SMOOTH)));
		window.add(next);
		next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	nextClicked();
            }
        });
		
		// Button for invert the pieces on UI (white or black pieces in front)
		JButton invert = new JButton();
		invert.setBounds(1000, 50, squareSize, squareSize);
		invert.setBackground(Color.LIGHT_GRAY);
		ImageIcon invertIcon = new ImageIcon(getClass().getResource(
				"/chess/images/invert.png"));
		invert.setIcon(new ImageIcon(
				invertIcon.getImage().getScaledInstance(squareSize, squareSize, Image.SCALE_SMOOTH)));
		window.add(invert);
		invert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	invertClicked();
            }
        });
		
		// Button to restart the game
		JButton restart = new JButton();
		restart.setBounds(100, 540, squareSize*3, squareSize);
		Font font = new Font("Arial", Font.PLAIN, 18);
		restart.setFont(font);
		restart.setText("REINICIAR PARTIDA");
		restart.setBackground(Color.LIGHT_GRAY);
		window.add(restart);
		restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	restartClicked();
            }
        });
		
		// Resize all piece images to the button size
		resizeImages(squareSize);
		for(i = 0; i<8; i++) {
			for(j = 0; j<8; j++) {
				final int x = j, y = Board.HIGH_LIMIT - i;
				JButton square = new JButton();				
				square.setPreferredSize(new Dimension(squareSize, squareSize));
				square.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                squareClicked(x, y);
		            }
		        });  // Call this function each time a square is clicked
				chessboard.add(square);
				squares[y][x] = square;  // Add the button to a matrix
			}
		}
		window.setVisible(true);
	}
		
	// Resize all images and keep them in a list
	private void resizeImages(int size) {
		int i = 0;
		ImageIcon originalIcon;
	    Image scaledImage;
	    String path = "/chess/images/";
	    String pieces[] = {"bishop", "king", "knight", "pawn", "queen", "rook"};
	    String colors[] = {"W.png", "B.png"};
	    for (String piece : pieces) {
	    	for (String color : colors) {
	    		originalIcon = new ImageIcon(getClass().getResource(path + piece + color));
	    		scaledImage = originalIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
	    		images[i] = new ImageIcon(scaledImage);
	    		i++;
	    	}
	    }
	}
	
	public void repaint() {
		window.repaint();  // Refresh the UI
	}
	
	// Set the original color on all squares
	// This is used after show the new valid moves,
	// the last move and the current piece clicked
	public void defaultSquaresColor() {
		int y, x;
		for(y = 0; y<8; y++) {
			for(x = 0; x<8; x++) {
				if (y % 2 == x % 2)
					squares[y][x].setBackground(Color.decode(darkColor));
				else
					squares[y][x].setBackground(Color.decode(lightColor));
			}
		}
	}
		
	// Find the correct image for a piece given its type and color
	public ImageIcon getImage(EnumPieces type, EnumColors color) {
		int i = 0;
		EnumPieces types[] = {EnumPieces.BISHOP, EnumPieces.KING, EnumPieces.KNIGHT,
				EnumPieces.PAWN, EnumPieces.QUEEN, EnumPieces.ROOK};
	    EnumColors colors[] = {EnumColors.WHITE, EnumColors.BLACK};
	    for (EnumPieces currentType : types) {
	    	for (EnumColors currentColor : colors) {
	    		if (currentType == type && currentColor == color)
	    			return images[i];
	    		i++;
	    	}
	    }
	    return images[0];
	}
}
