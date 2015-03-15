package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author The NG Family displays tetris state
 */
public class GraphicsWindow extends JPanel {

	Tetris tetris;
	int delay = 1;
	int[][] board;

	public GraphicsWindow() {
		setDoubleBuffered(true);
		//tetris = new Tetris();
		tetris = new Tetris(new double[] {-2.93, 4.83, -2.99, 3.39, 3.93, 2.16, 4.33 });
	} 

	public void start() {
		while(!tetris.gameOver) {
			board = tetris.playMove();
			repaint();
			sleep(delay);
		}
	}
	
	protected synchronized void paintComponent(Graphics g) {
        int yd=getHeight()/board.length;
        int xd=getWidth()/board[0].length;

        for(int x = 0; x < board[0].length; x++) {
        	for(int y = 0; y < board.length; y++) {
        		g.setColor(getColor(board[y][x]));
        		g.fillRect(x * xd, y * yd, xd, yd);
        	}
        }
    }
	
	

	public Dimension getPreferredSize() {
		int[][] board = tetris.getBoard();
		return new Dimension(board[0].length * 20, board.length * 20);
	}

	protected Color getColor(int p) {
		switch (p) {
		default:
		case 0:
			return Color.black;
		case 1:
			return Color.yellow;
		case 2:
			return Color.blue;
		case 3:
			return Color.red;
		case 4:
			return Color.cyan;
		case 5:
			return Color.green;
		case 6:
			return Color.magenta;
		case 7:
			return Color.white;
		}
	}

	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Tetris");
		GraphicsWindow graphicsWindow = new GraphicsWindow();
		f.getContentPane().add(graphicsWindow);
		f.pack();
		f.setVisible(true);
		graphicsWindow.start();
	}

}
