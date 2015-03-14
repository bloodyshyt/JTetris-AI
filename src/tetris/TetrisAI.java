package tetris;

public class TetrisAI {

	Tetris tetris;
	int[][] board;
	int px, py, pr, piece;

	public TetrisAI(Tetris t) {
		tetris = t;
		board = t.getBoard();
	}

	private boolean get(int x, int y) {
		if (x < px || x >= px + 4 || y < py || y >= py + 4)
			return board[y][x] > 0;

		if (Tetris.pieces[piece][pr][(y - py) * 4 + (x - px)] != 0)
			return true;
		return board[y][x] > 0;
	}

	public int[] move() {
		double maxscore = Integer.MIN_VALUE;
		int maxpx = 0, maxpr = 0;

		px = pr = -13;
		piece = tetris.getPiece();

		for (px = -4; px < board[0].length + 4; px++) {
			for (pr = 0; pr < Tetris.pieces[piece].length; pr++) {
				if (!tetris.spaceFor(px, 0, Tetris.pieces[piece][pr]))
					continue;

				py = 0;
				while (!tetris.parkPiece(px, py, Tetris.pieces[piece][pr]))
					py++;
				double score = evaluate();
				if (score > maxscore) {
					maxscore = score;
					maxpx = px;
					maxpr = pr;
				}
			}
		}
		System.out.println("Max score of " + maxscore + " x: " + maxpx + " maxpr: " + maxpr); 
		return new int[] { maxpx, maxpr };
	}

	private double evaluate() {
		int sum = 0;

		sum += ironMill();
		sum -= countCoveredHoles() * 10000;
		return sum;
	}

	int ironMill() {
		int iron = 0;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				if (get(x, y))
					iron += y * y * y;
			}
		}
		return iron;
	}

	int countCoveredHoles() {
		int holes = 0;
		for (int x = 0; x < board[0].length; x++) {
			boolean swap = false;
			for (int y = 0; y < board.length; y++) {
				if (get(x, y))
					swap = true;
				else {
					if (swap)
						holes++;
					swap = false;
				}
			}
		}
		// System.out.println("Holes: " + holes);
		return holes;
	}

}
