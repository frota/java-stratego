package ifce.ppd.game.stratego.pieces;

import ifce.ppd.game.stratego.Board;

public class Water extends Piece { // ZERO

	public Water() {
		setColor(cNeutral);
		setRank(pWater);
	}

	public int[][] moves(int x, int y, Piece[][] board) {
		return (new int[Board.sideSize][Board.sideSize]); // lago não joga
	}

}
