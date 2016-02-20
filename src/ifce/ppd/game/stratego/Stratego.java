package ifce.ppd.game.stratego;

import ifce.ppd.game.stratego.pieces.Piece;

/**
 * Partida de Combate.
 * @author Frota
 *
 */
public class Stratego {

	public static final int teamRed = Piece.cRed; // 1
	public static final int teamBlue = Piece.cBlue; // 2
	public static final int teamNeutral = Piece.cNeutral; // 0

	public static final int statusPreGame = 1;
	public static final int statusPlaying = 2;
	public static final int statusPosGame = 3;

	/* Implementar máximo de 5 movimentos entre casas alternadas */
//	public static final int maxMoves = 5;
//	private int[] lastRedMove = {-1, -1, -1, -1};
//	private int redMoveReps = 0;
//	private int[] lastBlueMove = {-1, -1, -1, -1};
//	private int blueMoveReps = 0;

	/* Número de peças de cada tipo, do espião à bandeira, total de 40 */
	public static final int[] numPieces = {1, 8, 5, 4, 4, 4, 3, 2, 1, 1, 6, 1};

	private Board board;
	private int status;
	private boolean redReady = false, blueReady = false;
	private boolean redTurn = true;

	/* Peças fora do jogo */
	private int[] redCollected = {1, 8, 5, 4, 4, 4, 3, 2, 1, 1, 6, 1}; // = numPieces
	private int[] blueCollected = {1, 8, 5, 4, 4, 4, 3, 2, 1, 1, 6, 1}; // = numPieces

	public Stratego() {
		board = new Board();
		status = statusPreGame;
	}

	/**
	 * Movimentos possíveis da peça em x, y.
	 * @param x
	 * @param y
	 * @return Vetor de inteiros com 0's, 1's (transporte) e 2's (ataques).
	 */
	public int[][] moves(int x, int y) {
		int[][] moves = new int[Board.sideSize][Board.sideSize];
		Piece p = board.getPiece(x, y);
		moves = p.moves(x, y, board.getPieces());
		// histórico deve apagar um aqui (baseado em >=5 e cor da peça em x, y)
		return moves;
	}

	/**
	 * 
	 * @return É o turno do vermelho?
	 */
	public boolean isRedTurn() {
		return redTurn;
	}

	public Board getBoard() {
		return board;
	}

	public int getStatus() {
		return status;
	}

	public Piece getPiece(int x, int y) {
		return board.getPiece(x, y);
	}

	public int[] getRedCollected() {
		return redCollected;
	}

	public int[] getBlueCollected() {
		return blueCollected;
	}

	public int getTotalRedCollected() {
		int c = 0;
		for (int i = 0; i < redCollected.length; i++) {
			c = c + redCollected[i];
		}
		return c;
	}

	public int getTotalBlueCollected() {
		int c = 0;
		for (int i = 0; i < blueCollected.length; i++) {
			c = c + blueCollected[i];
		}
		return c;
	}

	public int getNumPieces() {
		int c = 0;
		for (int i = 0; i < numPieces.length; i++) {
			c = c + numPieces[i];
		}
		return c;
	}

	public boolean setReadyRed() {
		if (status == statusPreGame && getTotalRedCollected() == 0 && !redReady) {
			redReady = true;
			if (blueReady) {
				status = statusPlaying; // se todos prontos
				redTurn = true;
				return true;
			}
		}
		return false;
	}

	public boolean getReadyRed() {
		return redReady;
	}

	public boolean setReadyBlue() {
		if (status == statusPreGame && getTotalBlueCollected() == 0 && !blueReady) {
			blueReady = true;
			if (redReady) {
				status = statusPlaying; // se todos prontos
				redTurn = true;
				return true;
			}
		}
		return false;
	}

	public boolean getReadyBlue() {
		return blueReady;
	}

	public int isValidMove(int x, int y, int mx, int my) {
		Piece p = board.getPiece(x, y);
		int mov[][] = p.moves(x, y, board.getPieces());
		return mov[mx][my];
	}

	/**
	 * Realiza uma jogada, dadas duas posições do tabuleiro (origem e destino),
	 * se for a vez do jogador em questão.
	 * 
	 * @param color Cor que identifica o jogador.
	 * @param x Posição inicial x da peça a ser movida.
	 * @param y Posição inicial y da peça a ser movida.
	 * @param mx Posição de destino x da peça a ser movida.
	 * @param my Posição de destino y da peça a ser movida.
	 * @return Se a bandeira foi capturada na jogada.
	 */
	public boolean makeAMove(int color, int x, int y, int mx, int my) { // de x,y -> mx,my
		boolean gotFlag = false;
		if (redTurn && color == teamRed && board.getPiece(x, y).getColor() == teamRed) {
			/* Jogada do vermelho (mesclar os dois) */
			int aux = isValidMove(x, y, mx, my);
			if (aux == 1) { // andar/correr
				board.placePiece(board.getPiece(x, y), mx, my);
				board.removePiece(x, y);
				redTurn = false;
			} else if (aux == 2) { // ataque red atacando azul
				int st = board.getPiece(x, y).strong(board.getPiece(mx, my));
				if (st == 1) { // matei
					blueCollected[board.getPiece(mx, my).getRank() - Piece.pSpy]++;
					board.placePiece(board.getPiece(x, y), mx, my);
					board.removePiece(x, y);
					redTurn = false;
				} else if (st == 0) { // morremos
					redCollected[board.getPiece(x, y).getRank() - Piece.pSpy]++;
					blueCollected[board.getPiece(mx, my).getRank() - Piece.pSpy]++;
					board.removePiece(mx, my);
					board.removePiece(x, y);
					redTurn = false;
				} else if (st == -1) { // morri
					redCollected[board.getPiece(x, y).getRank() - Piece.pSpy]++;
					board.removePiece(x, y);
					redTurn = false;
				} else if (st == 2) { // peguei bandeira
					blueCollected[board.getPiece(mx, my).getRank() - Piece.pSpy]++;
					gotFlag = true;
					status = statusPosGame; // redTurn indica vencedor
				} else {
					System.out.println("RED: Comparação inválida de peças! st = " + st);
					redTurn = true;
				}
			} else if (aux == 0) { // nada
				System.out.println("RED: Movimentação impossível! aux = 0");
			}
			// fim - de analise de jogada vermelha

		} else if (!redTurn && color == teamBlue && board.getPiece(x, y).getColor() == teamBlue) {
			/* Jogada do azul */
			int aux = isValidMove(x, y, mx, my);
			if (aux == 1) {
				board.placePiece(board.getPiece(x, y), mx, my);
				board.removePiece(x, y);
				redTurn = true;
			} else if (aux == 2) { // ataque azul
				int st = board.getPiece(x, y).strong(board.getPiece(mx, my));
				if (st == 1) { // matei
					redCollected[board.getPiece(mx, my).getRank() - Piece.pSpy]++;
					board.placePiece(board.getPiece(x, y), mx, my);
					board.removePiece(x, y);
					redTurn = true;
				} else if (st == 0) { // morremos
					blueCollected[board.getPiece(x, y).getRank() - Piece.pSpy]++;
					redCollected[board.getPiece(mx, my).getRank() - Piece.pSpy]++;
					board.removePiece(mx, my);
					board.removePiece(x, y);
					redTurn = true;
				} else if (st == -1) { // morri
					blueCollected[board.getPiece(x, y).getRank() - Piece.pSpy]++;
					board.removePiece(x, y);
					redTurn = true;
				} else if (st == 2) { // peguei bandeira
					redCollected[board.getPiece(mx, my).getRank() - Piece.pSpy]++;
					gotFlag = true;
					status = statusPosGame; // redTurn indica vencedor
				} else { // 3 4
					System.out.println("BLUE: Comparação inválida de peças! st = " + st);
					redTurn = false;
				}
			} else if (aux == 0) {
				System.out.println("BLUE: Movimentação impossível! aux = 0");
			}
			// fim - de analise de jogada azul
		}
		return gotFlag;
	}

	public boolean placePiece(Piece piece, int x, int y) { // colocando peça no pre-jogo
		if (status == statusPreGame) {
			if (board.getPiece(x, y) == null) { // lugar vazio
				int id = piece.getRank();
				int co = piece.getColor();

				if (co == teamRed && isRedField(x, y)) {
					if (redCollected[id - Piece.pSpy] > 0) {
						board.placePiece(piece, x, y);
						redCollected[id - Piece.pSpy]--; // red colocada no campo red vazio
						return true;
					}
				} else if (co == teamBlue && isBlueField(x, y)) {
					if (blueCollected[id - Piece.pSpy] > 0) {
						board.placePiece(piece, x, y);
						blueCollected[id - Piece.pSpy]--; // blue colocada no campo blue vazio
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean removePiece(int x, int y) { // tirando peca no pre-jogo
		if (status == statusPreGame) {
			if (board.getPiece(x, y).isAnyPiece()) { // tem peça (red ou blue) no lugar
				int id = board.getPiece(x, y).getRank();
				int co = board.getPiece(x, y).getColor();

				if (co == teamRed) { // removendo peça vermelha
					if (redCollected[id - Piece.pSpy] < numPieces[id - Piece.pSpy]) {
						board.removePiece(x, y);
						redCollected[id - Piece.pSpy]++;
						return true;
					}
				} else if (co == teamBlue) { // removendo peça azul
					if (blueCollected[id - Piece.pSpy] < numPieces[id - Piece.pSpy]) {
						board.removePiece(x, y);
						blueCollected[id - Piece.pSpy]++;
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isRedField(int x, int y) {
		return (x >= 0 && x < Board.sideSize && y >= 6 && y < Board.sideSize);
	}

	public boolean isBlueField(int x, int y) {
		return (x >= 0 && x < Board.sideSize && y >= 0 && y < 4);
	}

}
