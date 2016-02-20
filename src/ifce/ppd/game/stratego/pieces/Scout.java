package ifce.ppd.game.stratego.pieces;

import ifce.ppd.game.stratego.Board;

public class Scout extends Piece { // 02

	public Scout(int color) {
		setColor(color);
		setRank(pScout);
	}

	public int[][] moves(int x, int y, Piece[][] board) {
		int[][] moves = new int[Board.sideSize][Board.sideSize]; // tá zerado
		int posX, posY;
		int i = 1;
		boolean north = true,
				east = true,
				south = true,
				west = true; // indicam se não encontrei obstáculos na direção referida

		Piece p = board[x][y];
		if (p == null) {
			return moves;
		}

		while (north || east || south || west) {
			posX = x; // norte
			posY = y - i;
			if (posX >= 0 && posX < Board.sideSize
					&& posY >= 0 && posY < Board.sideSize
					&& north) {
				if (board[posX][posY] == null) { // se casa vazia
					moves[posX][posY] = 1;
				} else if (isEnemy(board[posX][posY])) { // se peça inimiga
					if (i <= 2) { // a 2 ou 1 de distância
						moves[posX][posY] = 2;
					}
					north = false;
				} else if (board[posX][posY].getRank() == Piece.pWater
						|| isAlly(board[posX][posY])) { // se inimigo ou lago
					north = false;
				} else {
					north = false;
				}
			} else {
				north = false;
			}

			posX = x + i; // leste
			posY = y;
			if (posX >= 0 && posX < Board.sideSize
					&& posY >= 0 && posY < Board.sideSize
					&& east) {
				if (board[posX][posY] == null) {
					moves[posX][posY] = 1;
				} else if (isEnemy(board[posX][posY])) {
					if (i <= 2) {
						moves[posX][posY] = 2;
					}
					east = false;
				} else if (board[posX][posY].getRank() == Piece.pWater
						|| isAlly(board[posX][posY])) {
					east = false;
				} else {
					east = false;
				}
			} else {
				east = false;
			}

			posX = x; // sul
			posY = y + i;
			if (posX >= 0 && posX < Board.sideSize
					&& posY >= 0 && posY < Board.sideSize
					&& south) {
				if (board[posX][posY] == null) {
					moves[posX][posY] = 1;
				} else if (isEnemy(board[posX][posY])) {
					if (i <= 2) {
						moves[posX][posY] = 2;
					}
					south = false;
				} else if (board[posX][posY].getRank() == Piece.pWater
						|| isAlly(board[posX][posY])) {
					south = false;
				} else {
					south = false;
				}
			} else {
				south = false;
			}

			posX = x - i; // oeste
			posY = y;
			if (posX >= 0 && posX < Board.sideSize
					&& posY >= 0 && posY < Board.sideSize
					&& west) {
				if (board[posX][posY] == null) {
					moves[posX][posY] = 1;
				} else if (isEnemy(board[posX][posY])) {
					if (i <= 2) {
						moves[posX][posY] = 2;
					}
					west = false;
				} else if (board[posX][posY].getRank() == Piece.pWater
						|| isAlly(board[posX][posY])) {
					west = false;
				} else {
					west = false;
				}
			} else {
				west = false;
			}

			i++;
		}

		return moves;

	}

}
