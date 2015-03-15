package tetris;

public class TetrisAI {

	Tetris tetris;
	int[][] board;
	int px, py, pr, piece;
	double[] weights;
	static Feature[] f;

	public TetrisAI(Tetris t, double[] weights) {
		this();
		tetris = t;
		board = t.getBoard();
		this.weights = weights;
	}

	public TetrisAI(Tetris t) {
		this();
		tetris = t;
		board = t.getBoard();
	}

	public TetrisAI() {
		f = new Feature[] { fsumOfHeights, fconveredHoles, fblockades,
				fcompleteLines, fedgesTouchingBlock, fedgesTouchingWall,
				fedgesTouchingFloor };
	}

	public static int getNumFeatures() {
		return 7;
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
				// double score = evaluate();
				double score = evaluate();
				if (score > maxscore) {
					maxscore = score;
					maxpx = px;
					maxpr = pr;
				}
			}
		}
		// System.out.println("Max score of " + maxscore + " x: " + maxpx +
		// " maxpr: " + maxpr);
		return new int[] { maxpx, maxpr };
	}

	private double evaluate() {
		double score = 0;
		// double[] weights = new double[] {1 , -10000};
		// Feature[] f = new Feature[] {fironMill, fconveredHoles};
		// Feature[] f = new Feature[] { faggregateHeight, fcompleteLines,
		// fconveredHoles, fbumpiness };
		Feature[] f = new Feature[] { fsumOfHeights, fcompleteLines,
				fconveredHoles, fblockades };
		// double[] weights = new double[] {-0.666, 0.992, -0.46544, -0.24};
		// weights = new double[] {1, -1000, 0, 0, 0, 0};
		for (int i = 0; i < f.length; i++)
			score += f[i].evaluate() * weights[i];
		return score;
	}

	private int[] getLandingHeight() {
		int[] landingHeight = new int[board[0].length];
		for (int x = 0; x < board[0].length; x++) {
			int y = board.length - 1;
			while (get(x, y) && y > 0)
				y--;
			landingHeight[x] = board.length - 1 - y;
		}
		return landingHeight;
	}

	private boolean lineComplete(int l) {
		for (int i = 0; i < board[0].length; i++) {
			if (get(i, l) == false)
				return false;
		}
		return true;
	}

	Feature fedgesTouchingBlock = new Feature() {

		@Override
		public double evaluate() {
			int n = 0;
			for (int x = 0; x < board[0].length; x++) {
				for (int y = 0; y < board.length; y++) {
					// check adjacent
					int up = y - 1;
					int down = y + 1;
					int left = x - 1;
					int right = x + 1;

					if (up >= 0 && get(x, up))
						n++;
					if (down < board.length && get(x, down))
						n++;
					if (left >= 0 && get(left, y))
						n++;
					if (right < board[0].length && get(right, y))
						n++;
				}
			}
			return n;
		}
	};

	Feature fedgesTouchingFloor = new Feature() {

		@Override
		public double evaluate() {
			int n = 0;
			for (int i = 0; i < board[0].length; i++) {
				if (get(i, board.length - 1))
					n++;
			}
			return n;
		}
	};
	Feature fedgesTouchingWall = new Feature() {

		@Override
		public double evaluate() {
			int n = 0;
			for (int y = board.length - 1; y >= 0; y++) {
				if (get(0, y))
					n++;
				if (get(board[0].length - 1, y))
					n++;
			}
			return n;
		}
	};

	Feature fsumOfHeights = new Feature() {

		@Override
		public double evaluate() {
			int sumHeights = 0;
			for (int y = 0; y < board.length; y++) {
				int height = board.length - y;
				for (int x = 0; x < board[0].length; x++) {
					if (get(x, y))
						sumHeights += height;
				}
			}
			return sumHeights;
		}
	};

	Feature fblockades = new Feature() {

		@Override
		public double evaluate() {
			int nblockades = 0;
			for (int x = 0; x < board[0].length; x++) {
				boolean blockade = false;
				// go down the column to check for holes
				for (int y = board.length - 1; y <= 0; y--) {
					if (!get(x, y))
						blockade = false;
					else if (blockade)
						nblockades++;
				}
			}
			return nblockades;
		}
	};

	Feature fironMill = new Feature() {

		@Override
		public double evaluate() {
			int iron = 0;
			for (int y = 0; y < board.length; y++) {
				for (int x = 0; x < board[0].length; x++) {
					if (get(x, y))
						iron += y * y * y;
				}
			}
			return iron;
		}
	};

	Feature fconveredHoles = new Feature() {

		@Override
		public double evaluate() {
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
	};

	Feature faggregateHeight = new Feature() {

		@Override
		public double evaluate() {
			int[] heights = getLandingHeight();
			int sum = 0;
			for (int i : heights)
				sum += i;
			// System.out.println("Agg Height: " + sum);
			return sum;
		}
	};
	Feature fcompleteLines = new Feature() {

		@Override
		public double evaluate() {
			int n = 0;
			for (int y = 0; y < board.length; y++) {
				if (lineComplete(y))
					n++;
			}
			// System.out.println("Complete Lines: " + n);
			return n;
		}
	};
	Feature fbumpiness = new Feature() {

		@Override
		public double evaluate() {
			int bumpiness = 0;
			int[] heights = getLandingHeight();
			for (int i = 1; i < heights.length; i++) {
				bumpiness += Math.abs(heights[i] - heights[i - 1]);
			}
			bumpiness += Math.abs(heights[heights.length - 1] - heights[0]);
			// System.out.println("Bumpiness: " + bumpiness);
			return bumpiness;
		}
	};
}
