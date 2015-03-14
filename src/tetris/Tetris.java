package tetris;

import java.util.Random;
import java.util.Scanner;

public class Tetris {
	private int[][] board;
	boolean drawboard = true;
	Random rand = new Random();
	int piece, px, py, pr;
	int opx, opy;
	int linesCleared;
	boolean gameOver = false;
	TetrisAI bot;

	public Tetris(int w, int h, int delay) {
		board = new int[h][w];
		reset();
	}

	public Tetris(int w, int h) {
		this(w, h, 500);
	}

	public Tetris() {
		this(12, 30);
	}
	
	public void start() {
		while(!gameOver) {
			//Scanner in = new Scanner(System.in);
			System.out.println("Current piece: " + piece);
			System.out.println("Current orientation: " + pr);
			//System.out.print("Enter orientation and x: ");
			//pr = in.nextInt();
			//px = in.nextInt();
			//TetrisAI bot = new TetrisAI(this);
			int[] move = bot.move();
			px = move[0];
			pr = move[1];
			drop();
			print();
			System.out.println("Lines cleared: " + linesCleared);
			nextPiece();
		}
	}
	
	public int[][] playMove() {
		int[] move = bot.move();
		px = move[0];
		pr = move[1];
		drop();
		print();
		System.out.println("Lines cleared: " + linesCleared);
		nextPiece();
		return getBoard();
	}

	public int[][] getBoard() {
		return board;
	}

	public int getPiece() {
		return piece;
	}

	public int getRotation() {
		return pr;
	}

	public int getPieceX() {
		return px;
	}

	public int getPieceY() {
		return py;
	}
	
	public int getCellValue(int x, int y) {
		return board[y][x];
	}
	
	public synchronized void print() {
		System.out.println("Board");
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				System.out.print(board[y][x]);
			}
			System.out.println();
		}
	}

	public void nextPiece() {
		piece = rand.nextInt(pieces.length); // pieces.length is number of
												// tetriminoes (7)
		pr = rand.nextInt(pieces[piece].length); // number of orientations
		px = board[0].length / 2 - 2; // aprox middle of the board
		py = 0;
		if (!spaceFor(px, py, pieces[piece][pr])) {
			gameOver = true;
		}
		// pieces[piece][pr] is one tetriminoe
	}

	public void reset() {
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				board[y][x] = 0;
			}
		}
		opx = opy = 0;
		nextPiece();
		drawboard = true;
		linesCleared = 0;
		bot = new TetrisAI(this);

	}
	
	public boolean spaceFor(int x, int y, int[] piece) {
		for(int i = 0; i < piece.length; i++) {
			if(piece[i] == 0) continue;
			int _x = x + i % 4;
			int _y = y + i / 4;
			if(_y < 0 || _y >= board.length) return false;
			if(_x < 0 || _x >= board[0].length) return false;
			if(getCellValue(_x, _y) != 0) return false;
		}
		return true;
	}
	
	// should piece be parked here?
	public boolean parkPiece(int px, int py, int[] piece) {
		for(int xp = 0; xp < 4; xp++) {
			int m = -1;	// give m a sentinel value
			for(int yp = 4 - 1; yp >= 0; yp--) {
				if(piece[yp * 4 + xp] != 0) {
					m = yp;
					break;
				}
			}
			if (m >= 0) {
				if (py + m + 1 == board.length
						|| board[py + m + 1][px + xp] != 0) {
					return true;
				}
			}
		}
		return false;
	}

	public void fixPiece() {
		for (int i = 0; i < 16; i++) {
			if (pieces[piece][pr][i] != 0) {
				board[py + i / 4][px + i % 4] = pieces[piece][pr][i];
			}
		}
		clearLines(py, py + 4);
	}
	
	public boolean drop() {
		System.out.println("Dropping, piece: " + piece + " px: " +px + " pr: " + pr);
		while(!parkPiece(px, py, pieces[piece][pr]))
			py++;
		fixPiece();
		nextPiece();
		return true;
	}
	
	public void clearLines(int from, int to) {
		to = Math.min(to, board.length);
		for(int i = from; i < to; i++) 
			if(lineFull(i)) {
				removeLine(i);
				linesCleared++;
			}
	}
	
	private void removeLine(int l) {
		for(int i = 0; i < board[l].length; i++) {
			board[l][i] = 0;
		}
		
		int[] cow = board[l];
		for(int i = l - 1; i >= 0; i--) {
			board[i+1] = board[i];
		}
		board[0] = cow;
	}
	
	private boolean lineFull(int l) {
		for(int value : board[l])
			if(value == 0) return false;
		return true;
	}
	
	public static void main(String[] args) {
		Tetris t = new Tetris();
		t.start();
//		for(int i = 0; i < 4; i++) {
//			t.drop();
//			t.print();
//		}
	}
    
    final public static int[][][] pieces = {
        {{0,0,0,0,
          0,1,1,0,
          0,1,1,0,
          0,0,0,0}},

        {{0,0,0,0,
          2,2,2,2,
          0,0,0,0,
          0,0,0,0},
         {0,0,2,0,
          0,0,2,0,
          0,0,2,0,
          0,0,2,0}},

        {{0,0,0,0,
          0,0,3,3,
          0,3,3,0,
          0,0,0,0},
        { 0,0,3,0,
          0,0,3,3,
          0,0,0,3,
          0,0,0,0}},

        {{0,0,0,0,
          0,4,4,0,
          0,0,4,4,
          0,0,0,0},
        { 0,0,0,4,
          0,0,4,4,
          0,0,4,0,
          0,0,0,0}},

        {{0,0,0,0,
          0,5,5,5,
          0,5,0,0,
          0,0,0,0},
        { 0,0,5,0,
          0,0,5,0,
          0,0,5,5,
          0,0,0,0},
        { 0,0,0,5,
          0,5,5,5,
          0,0,0,0,
          0,0,0,0},
        { 0,5,5,0,
          0,0,5,0,
          0,0,5,0,
          0,0,0,0}},

        {{0,0,0,0,
          0,6,6,6,
          0,0,0,6,
          0,0,0,0},
        { 0,0,6,6,
          0,0,6,0,
          0,0,6,0,
          0,0,0,0},
        { 0,6,0,0,
          0,6,6,6,
          0,0,0,0,
          0,0,0,0},
        { 0,0,6,0,
          0,0,6,0,
          0,6,6,0,
          0,0,0,0}},

        {{0,0,0,0,
          0,7,7,7,
          0,0,7,0,
          0,0,0,0},
        { 0,0,7,0,
          0,0,7,7,
          0,0,7,0,
          0,0,0,0},
        { 0,0,7,0,
          0,7,7,7,
          0,0,0,0,
          0,0,0,0},
        { 0,0,7,0,
          0,7,7,0,
          0,0,7,0,
          0,0,0,0}}};
	
}
