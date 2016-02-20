package ifce.ppd.game.stratego.pieces;

import ifce.ppd.game.stratego.Board;

public class Piece {

	private int color;
	private int rank;

//	public static enum Color {
//		neutral, red, blue
//	}

//	public static enum Rank {
//		water, spy, scout, miner, sergeant, lieutenant, captain, major, colonel,
//		general, marshal, bomb, flag
//	}

	public static final int cNeutral = 0;
	public static final int cRed = 1;
	public static final int cBlue = 2;

	public static final int pWater = 0; // lago
	public static final int pSpy = 1; // espi�o 1
	public static final int pScout = 2; // soldado 2
	public static final int pMiner = 3; // cabo-armeiro 3
	public static final int pSergeant = 4; // sargento 4
	public static final int pLieutenant = 5; // tenente 5
	public static final int pCaptain = 6; // capit�o 6
	public static final int pMajor = 7; // major 7
	public static final int pColonel = 8; // coronel 8
	public static final int pGeneral = 9; // general 9
	public static final int pMarshal = 10; // marechal 10
	public static final int pBomb = 11; // bomba B
	public static final int pFlag = 12; // bandeira F

	public int getColor() {
		return color;
	}

	public void setColor(int c) {
		this.color = c;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int r) {
		rank = r;
	}

	public boolean isAnyPiece() {
		return (rank >= pSpy && rank <= pFlag); // id >= 1 && id <= 12
	}

	public boolean isMovablePiece() {
		return (rank >= pSpy && rank <= pMarshal); // id >= 1 && id <= 10
	}

	public boolean isEnemy(Piece piece) {
		return ((this.color == cRed && piece.color == cBlue) // RxB
				|| (this.color == cBlue && piece.color == cRed)); // BxR
	}

	public boolean isAlly(Piece piece) {
		return ((this.color == cRed && piece.color == cRed) // R+R
				|| (this.color == cBlue && piece.color == cBlue)); // B+B
	}

	public int isValidMove(int x, int y, int mx, int my, Piece[][] board) {
		int mov[][] = new int[Board.sideSize][Board.sideSize];
		mov = moves(x, y, board);
		return mov[mx][my]; // 0, 1 ou 2
	}

	/**
	 * Retorna a for�a desta pe�a em rela��o � piece.
	 * -1 para mais fraco. 0 para mesma for�a. 1 para mais forte.
	 * 2 para piece sendo bandeira.
	 * 3 para compara��o inv�lida (pe�as do mesmo time, ...).
	 * 4 � caso imposs�vel.
	 * 
	 * @param piece Pe�a que esta inst�ncia ir� se comparar.
	 * @return A for�a desta pe�a.
	 */
	public int strong(Piece piece) {
		if (isMovablePiece()) {
			if (isEnemy(piece)) { // sou pe�a m�vel, ele � inimigo
				if (piece.getRank() == pFlag) {
					return 2; // peguei bandeira
				} else if (getRank() == pMiner && piece.getRank() == pBomb) {
					return 1; // meu cabo desarmou bomba
				} else if (getRank() == pSpy && piece.getRank() == pMarshal) {
					return 1; // meu espi�o matou marechal
				} else if (getRank() > piece.getRank()) {
					return 1; // sou mais forte (geral)
				} else if (getRank() == piece.getRank()) {
					return 0; // mesma for�a
				} else if (getRank() < piece.getRank()) {
					return -1; // sou mais fraco (geral)
				} else {
					return 4; // Caso inexistente?!?!
				}
			} else { // pe�as do mesmo time
				return 3;
			}
		} else { // se sou im�vel ou n�o sou pe�a
			return 3;
		}
	}

	/**
	 * (M�todo no lugar errado?) Calcula as jogadas poss�veis da pe�a em x, y do
	 * tabuleiro board. (Override por Scout, Water, Bomb e Flag)
	 * 
	 * @param x Posi��o x do tabuleiro.
	 * @param y Posi��o y do tabuleiro.
	 * @param board O tabuleiro.
	 * @return Vetor de inteiros indicandos as posi��es poss�ves para
	 * transporte (1) e ataque (2).
	 */
	public int[][] moves(int x, int y, Piece[][] board) {
		int[][] moves = new int[Board.sideSize][Board.sideSize];
		int posX, posY; // auxiliam no calculo de jogadas possiveis

		Piece p = board[x][y];
		if (p == null) {
			return moves; // x, y vazio
		}

		posX = x; // norte
		posY = y - 1;
		if (posX >= 0 && posX < Board.sideSize && posY >= 0 && posY < Board.sideSize) {
			if (board[posX][posY] == null) { // se casa vazia
				moves[posX][posY] = 1;
			} else if (isEnemy(board[posX][posY])) { // se pe�a inimiga
				moves[posX][posY] = 2;
			}
		}

		posX = x + 1; // leste
		posY = y;
		if (posX >= 0 && posX < Board.sideSize && posY >= 0 && posY < Board.sideSize) {
			if (board[posX][posY] == null) {
				moves[posX][posY] = 1;
			} else if (isEnemy(board[posX][posY])) {
				moves[posX][posY] = 2;
			}
		}

		posX = x; // sul
		posY = y + 1;
		if (posX >= 0 && posX < Board.sideSize && posY >= 0 && posY < Board.sideSize) {
			if (board[posX][posY] == null) {
				moves[posX][posY] = 1;
			} else if (isEnemy(board[posX][posY])) {
				moves[posX][posY] = 2;
			}
		}

		posX = x - 1; // oeste
		posY = y;
		if (posX >= 0 && posX < Board.sideSize && posY >= 0 && posY < Board.sideSize) {
			if (board[posX][posY] == null) {
				moves[posX][posY] = 1;
			} else if (isEnemy(board[posX][posY])) {
				moves[posX][posY] = 2;
			}
		}

		return moves;
	}

}
