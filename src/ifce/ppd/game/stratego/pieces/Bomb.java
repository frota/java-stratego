package ifce.ppd.game.stratego.pieces;

import ifce.ppd.game.stratego.Board;

public class Bomb extends Piece {

	public Bomb(int color) { // bomba - 11
		setColor(color);
		setRank(pBomb);
	}

	public int[][] moves(int x, int y, Piece[][] board) {
		return (new int[Board.sideSize][Board.sideSize]); // bomba não joga
	}

}
