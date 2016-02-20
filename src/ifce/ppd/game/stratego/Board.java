package ifce.ppd.game.stratego;

import ifce.ppd.game.stratego.pieces.*;

/**
 * Tabuleiro de Combate.
 * @author Frota
 *
 */
public class Board {

	public static final int sideSize = 10;
	private static final int[][] lakes = {
		{2, 4}, {3, 4}, {2, 5}, {3, 5}, // lago 1
		{6, 4}, {7, 4}, {6, 5}, {7, 5} // lago 2
	};

	private Piece[][] board = new Piece[sideSize][sideSize];

	public Board() {
		for (int y = 0; y < sideSize; y++) { // re-zerando tabuleiro
			for (int x = 0; x < sideSize; x++) {
				board[x][y] = null;
			}	
		}
		for (int i = 0; i < lakes.length; i++) { // inserindo lagos
			board[lakes[i][0]][lakes[i][1]] = new Water();
		}
	}

	public void placePiece(Piece piece, int x, int y) {
		board[x][y] = piece;
	}

	public void removePiece(int x, int y) {
		board[x][y] = null;
	}

	public Piece getPiece(int x, int y) {
		return board[x][y];
	}

	public Piece[][] getPieces() {
		return board;
	}

}
