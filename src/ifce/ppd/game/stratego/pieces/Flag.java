package ifce.ppd.game.stratego.pieces;

import ifce.ppd.game.stratego.Board;

public class Flag extends Piece {

	public Flag(int color) { // F
		setColor(color);
		setRank(pFlag);
	}

	public int[][] moves(int x, int y, Piece[][] board) {
		return (new int[Board.sideSize][Board.sideSize]); // bandeira não joga
	}

}
