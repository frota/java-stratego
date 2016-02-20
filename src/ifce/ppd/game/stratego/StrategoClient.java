package ifce.ppd.game.stratego;

import ifce.ppd.game.ClientWindow;
import ifce.ppd.game.gfx.Screen;
import ifce.ppd.game.gfx.Sprite;
import ifce.ppd.game.input.Mouse;
import ifce.ppd.game.stratego.pieces.Piece;

public class StrategoClient {

	private int anim_1 = 0; // animação de seleção
	private int anim_2 = 0; // animação de ataque

	private Piece piece1 = null; // animação de ataque
	private int piece1x, piece1y;
	private Piece piece2 = null;
	private int piece2x, piece2y; // animação de ataque

	private int mouseOldB = -1;
	private Sprite[] redSprites = {
			Sprite.red_01, Sprite.red_02, Sprite.red_03, Sprite.red_04, Sprite.red_05,
			Sprite.red_06, Sprite.red_07, Sprite.red_08, Sprite.red_09, Sprite.red_10, 
			Sprite.red_bo, Sprite.red_fl
	};
	private Sprite[] blueSprites = {
			Sprite.blue_01, Sprite.blue_02, Sprite.blue_03, Sprite.blue_04, Sprite.blue_05,
			Sprite.blue_06, Sprite.blue_07, Sprite.blue_08, Sprite.blue_09, Sprite.blue_10, 
			Sprite.blue_bo, Sprite.blue_fl
	};

	private Stratego stratego;
	private int myColor;
	private boolean playing = false;
	private Piece onMouse;
	private int sel_X = -1;
	private int sel_Y = -1;
	private int[][] myMoves = new int[Board.sideSize][Board.sideSize];

	public StrategoClient() {
		stratego = new Stratego();
		myColor = Piece.cNeutral;
		onMouse = null;
	}

	public Stratego getStratego() {
		return stratego;
	}

	public void setOnMouse(Piece piece) {
		onMouse = piece;
	}

	public int getColor() {
		return myColor;
	}

	public void setColor(int color) {
		myColor = color;
	}

	public boolean getPlaying() {
		return playing;
	}

	public void setPlaying(boolean bool) {
		playing = bool;
	}

	/**
	 * 
	 * @param client
	 */
	public void update(ClientWindow client) {
		int sqrX = 10 * Mouse.getX() / Sprite.map.X_SIZE;
		int sqrY = 10 * Mouse.getY() / Sprite.map.Y_SIZE;

		if (mouseOldB == -1 && Mouse.getButton() == 1) {
			// botão esquerdo
			if (stratego.getStatus() == Stratego.statusPreGame && onMouse != null) { // pre jogo
				if (onMouse.getColor() == Piece.cRed && stratego.isRedField(sqrX, sqrY)) {
					/* Colocando Vermelho no pre-jogo */
					boolean b = stratego.placePiece(onMouse, sqrX, sqrY);
					if (b) {
						client.getStreamPpd().sendCOMBX("COMB3"
								+ String.format("%02d", onMouse.getRank()) + "" + sqrX + "" + sqrY);
						onMouse = null;
					}
					client.setButtonsText(stratego.getRedCollected());
					client.setBtnReadyEnabled(stratego.getTotalRedCollected() <= 0);
					
				} else if (onMouse.getColor() == Piece.cBlue && stratego.isBlueField(sqrX, sqrY)) {
					/* Colocando Azul no pre-jogo */
					boolean b = stratego.placePiece(onMouse, sqrX, sqrY);
					if (b) {
						client.getStreamPpd().sendCOMBX("COMB3"
								+ String.format("%02d", onMouse.getRank()) + "" + sqrX + "" + sqrY);
						onMouse = null;
					}
					client.setButtonsText(stratego.getBlueCollected());
					client.setBtnReadyEnabled(stratego.getTotalBlueCollected() <= 0);
				}
				// fim - prejogo
			} else if (stratego.getStatus() == Stratego.statusPlaying) {
				/* JOGANDO */
				if ((myColor == Piece.cRed && stratego.isRedTurn())) { // RED na vez RED
					if (sel_X == -1 || sel_Y == -1) { // nada selecionado
						Piece piece = stratego.getPiece(sqrX, sqrY);
						if (piece != null && piece.getColor() == Piece.cRed) {
							sel_X = sqrX;
							sel_Y = sqrY; // selecionei um red
							myMoves = stratego.moves(sel_X, sel_Y);
						}
					} else { // tenho algo selecionado
						int m = stratego.isValidMove(sel_X, sel_Y, sqrX, sqrY);
						if (m == 1 || m == 2) { // JOGADA - VERMELHO --------------------
							if (m == 2) {
								callAnim2(sel_X, sel_Y, sqrX, sqrY);
							}
							stratego.makeAMove(myColor, sel_X, sel_Y, sqrX, sqrY);
							String comb = "COMB6" + myColor + "" + sel_X + "" + sel_Y
									+ "" + sqrX + "" + sqrY;
							client.getStreamPpd().sendCOMBX(comb);
							client.setLabelsText(stratego.getBlueCollected()); // atualizo inimigo
							sel_X = -1;
							sel_Y = -1;
							client.setGameStatus("Adversário jogando!");
						} else if (sel_X == sqrX && sel_Y == sqrY) {
							sel_X = -1;
							sel_Y = -1;
						}
					}
					// fim - jogada vermelha
				} else if (myColor == Piece.cBlue && !stratego.isRedTurn()) { // BLUE na vez BLUE
					if (sel_X == -1 || sel_Y == -1) { // nada selecionado
						Piece piece = stratego.getPiece(sqrX, sqrY);
						if (piece != null && piece.getColor() == Piece.cBlue) {
							sel_X = sqrX;
							sel_Y = sqrY;
							myMoves = stratego.moves(sel_X, sel_Y);
						}
					} else {
						int m = stratego.isValidMove(sel_X, sel_Y, sqrX, sqrY);
						if (m == 1 || m == 2) { // JOGADA -- AZUL -----------------------
							if (m == 2) {
								callAnim2(sel_X, sel_Y, sqrX, sqrY);
							}
							stratego.makeAMove(myColor, sel_X, sel_Y, sqrX, sqrY);
							String comb = "COMB6" + myColor + "" + sel_X + "" + sel_Y
									+ "" + sqrX + "" + sqrY;
							client.getStreamPpd().sendCOMBX(comb);
							client.setGameStatus("Adversário jogando!");
							client.setLabelsText(stratego.getRedCollected()); // atualizo inimigo
							sel_X = -1;
							sel_Y = -1;
						} else if (sel_X == sqrX && sel_Y == sqrY) {
							sel_X = -1;
							sel_Y = -1;
						}
					}
					// fim - jogada azul
				}
			} else if (stratego.getStatus() == Stratego.statusPosGame) {
				client.setGameStatus("Fim de jogo!");
				client.dispose();
				System.exit(0);
			}
			// fim - botão esquerdo
		} else if (mouseOldB == -1 && Mouse.getButton() == 3) { // botão direito
			if (stratego.getStatus() == Stratego.statusPreGame) { // remoção de peças no pre-jogo
				Piece piece = stratego.getPiece(sqrX, sqrY);
				if (piece != null) {
					if (piece.getColor() == Piece.cRed && myColor == Piece.cRed && !stratego.getReadyRed()) {
						boolean b = stratego.removePiece(sqrX, sqrY);
						if (b) {
							client.getStreamPpd().sendCOMBX("COMB4" + sqrX + "" + sqrY);
						}
						client.setButtonsText(stratego.getRedCollected());
						client.setBtnReadyEnabled(stratego.getTotalRedCollected() <= 0);
						
					} else if (piece.getColor() == Piece.cBlue && myColor == Piece.cBlue && !stratego.getReadyBlue()) {
						boolean b = stratego.removePiece(sqrX, sqrY);
						if (b) {
							client.getStreamPpd().sendCOMBX("COMB4" + sqrX + "" + sqrY);
						}
						client.setButtonsText(stratego.getBlueCollected());
						client.setBtnReadyEnabled(stratego.getTotalBlueCollected() <= 0);
					}
				}
			}
		}

		mouseOldB = Mouse.getButton();
	}

	public void callAnim2(int x1, int y1, int x2, int y2) {
		piece1x = x1;
		piece1y = y1;
		piece1 = stratego.getPiece(x1, y1);
		piece2x = x2;
		piece2y = y2;
		piece2 = stratego.getPiece(x2, y2);
	}

	/**
	 * Renderiza graficamente o jogo na tela screen.
	 * @param screen Tela onde a renderização será executada.
	 */
	public void render(Screen screen) {
		int sqrX = Mouse.getX() / 48; // pos x do mouse
		int sqrY = Mouse.getY() / 48; // pos y do mouse
		sqrX = sqrX * 48;
		sqrY = sqrY * 48;

		anim_1++; // anima peça selecionada

		screen.renderSprite(0, 0, Sprite.map, false); // renderiza campo

		if (!playing) {
			return;
		}

		for (int y = 0; y < Board.sideSize; y++) {
			for (int x = 0; x < Board.sideSize; x++) {
				/* Renderizando peças */
				Piece piece = stratego.getPiece(x, y);
				if (piece != null && piece.isAnyPiece()) {
					if (myColor == Piece.cRed) {
						if (piece.getColor() == Piece.cRed) {
							screen.renderSprite(x * 48, y * 48,
									redSprites[piece.getRank() - Piece.pSpy], false);
						} else if (piece.getColor() == Piece.cBlue) {
							screen.renderSprite(x * 48, y * 48, Sprite.blue_unknown, false);
						}
					} else if (myColor == Piece.cBlue) {
						if (piece.getColor() == Piece.cBlue) {
							screen.renderSprite(x * 48, y * 48,
									blueSprites[piece.getRank() - Piece.pSpy], false);
						} else if (piece.getColor() == Piece.cRed) {
							screen.renderSprite(x * 48, y * 48, Sprite.red_unknown, false);
						}
					} else {
						screen.renderSprite(x * 48, y * 48, Sprite.err_001, false);
					}
				}
			}
		}

		/* renderiza um combate */
		if (piece1 != null && piece2 != null) {
			if (piece1.getColor() == Piece.cRed) {
				screen.renderSprite(piece1x * 48, piece1y * 48,
						redSprites[piece1.getRank() - Piece.pSpy], false);
			} else if (piece1.getColor() == Piece.cBlue) {
				screen.renderSprite(piece1x * 48, piece1y * 48,
						blueSprites[piece1.getRank() - Piece.pSpy], false);
			}
			if (piece2.getColor() == Piece.cRed) {
				screen.renderSprite(piece2x * 48, piece2y * 48,
						redSprites[piece2.getRank() - Piece.pSpy], false);
			} else if (piece2.getColor() == Piece.cBlue) {
				screen.renderSprite(piece2x * 48, piece2y * 48,
						blueSprites[piece2.getRank() - Piece.pSpy], false);
			}
			anim_2++;
			if (anim_2 >= 60) {
				piece1 = null;
				piece2 = null;
				anim_2 = 0;
			}
		} else {
			anim_2 = 0;
		}

		if (onMouse != null) { /* renderiza peça no mouse */
			int mx = Mouse.getX() - 24;
			int my = Mouse.getY() - 24;
			if (myColor == Piece.cRed) {
				screen.renderSprite(mx, my,
						redSprites[onMouse.getRank() - Piece.pSpy], false);
			} else if (myColor == Piece.cBlue) {
				screen.renderSprite(mx, my,
						blueSprites[onMouse.getRank() - Piece.pSpy], false);
			}
		}

		if (sel_X >= 0 && sel_X < Board.sideSize && sel_Y >= 0 && sel_Y < Board.sideSize) {
			/* renderiza selected e moves */
			if (anim_1 < 7) {
				screen.renderSprite(sel_X * 48, sel_Y * 48, Sprite.selected_1, false);
			} else if (anim_1 >= 7 && anim_1 < 13) {
				screen.renderSprite(sel_X * 48, sel_Y * 48, Sprite.selected_2, false);
			} else if (anim_1 >= 13 && anim_1 < 20) {
				screen.renderSprite(sel_X * 48, sel_Y * 48, Sprite.selected_3, false);
			} else if (anim_1 >= 20 && anim_1 < 27) {
				screen.renderSprite(sel_X * 48, sel_Y * 48, Sprite.selected_2, false);
			} else {
				screen.renderSprite(sel_X * 48, sel_Y * 48, Sprite.selected_1, false);
				anim_1 = 0;
			}

			for (int y = 0; y < Board.sideSize; y++) { // moves
				for (int x = 0; x < Board.sideSize; x++) {
					if (myMoves[x][y] == 1) {
						screen.renderSprite(x * 48, y * 48, Sprite.move, false);
					} else if (myMoves[x][y] == 2) {
						screen.renderSprite(x * 48, y * 48, Sprite.attack, false);
					}
				}
			}
		}

		screen.renderSprite(sqrX, sqrY, Sprite.mouse, false); /* renderiza mouse */

		if (stratego.getStatus() == Stratego.statusPosGame) { /* vitória ou derrota */
			if ((stratego.isRedTurn() && myColor == Piece.cRed)
					|| (!stratego.isRedTurn() && myColor == Piece.cBlue)) {
				screen.renderSprite(144, 180, Sprite.victory, false);
			} else if ((stratego.isRedTurn() && myColor == Piece.cBlue)
					|| (!stratego.isRedTurn() && myColor == Piece.cRed)) {
				screen.renderSprite(144, 180, Sprite.defeat, false);
			}
		}
	}

}
