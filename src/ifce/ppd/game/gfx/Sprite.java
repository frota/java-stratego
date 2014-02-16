package ifce.ppd.game.gfx;

/**
 * Trabalha 'SpriteSheet's possibilitando dividí-los em pedaços.
 * 
 * Alteração: apenas a remoção de funcionalidades não utilizadas e adequação ao
 * jogo Combate.
 * 
 * @author The Cherno
 * @author Frota
 *
 */
public class Sprite {

	public final int X_SIZE;
	public final int Y_SIZE;
	private int x, y;
	private int[] pixels;
	private SpriteSheet sheet;

	/* Texto DERROTA e VITÓRIA */
	public static Sprite victory = new Sprite(192, 96, 0, 1, SpriteSheet.text);
	public static Sprite defeat = new Sprite(192, 96, 0, 0, SpriteSheet.text);
	

	/* Campo de batalha */
	public static Sprite map = new Sprite(
			SpriteSheet.map.getXSize(),
			SpriteSheet.map.getYSize(), 0, 0, SpriteSheet.map);

	/* Peças vermelhas */
	public static Sprite red_01 = new Sprite(48, 48, 0, 0, SpriteSheet.pieces);
	public static Sprite red_02 = new Sprite(48, 48, 1, 0, SpriteSheet.pieces);
	public static Sprite red_03 = new Sprite(48, 48, 2, 0, SpriteSheet.pieces);
	public static Sprite red_04 = new Sprite(48, 48, 3, 0, SpriteSheet.pieces);
	public static Sprite red_05 = new Sprite(48, 48, 4, 0, SpriteSheet.pieces);
	public static Sprite red_06 = new Sprite(48, 48, 5, 0, SpriteSheet.pieces);
	public static Sprite red_07 = new Sprite(48, 48, 0, 1, SpriteSheet.pieces);
	public static Sprite red_08 = new Sprite(48, 48, 1, 1, SpriteSheet.pieces);
	public static Sprite red_09 = new Sprite(48, 48, 2, 1, SpriteSheet.pieces);
	public static Sprite red_10 = new Sprite(48, 48, 3, 1, SpriteSheet.pieces);
	public static Sprite red_bo = new Sprite(48, 48, 4, 1, SpriteSheet.pieces);
	public static Sprite red_fl = new Sprite(48, 48, 5, 1, SpriteSheet.pieces);
	public static Sprite red_unknown = new Sprite(48, 48, 7, 2, SpriteSheet.pieces);

	/* Peças azuis */
	public static Sprite blue_01 = new Sprite(48, 48, 0, 2, SpriteSheet.pieces);
	public static Sprite blue_02 = new Sprite(48, 48, 1, 2, SpriteSheet.pieces);
	public static Sprite blue_03 = new Sprite(48, 48, 2, 2, SpriteSheet.pieces);
	public static Sprite blue_04 = new Sprite(48, 48, 3, 2, SpriteSheet.pieces);
	public static Sprite blue_05 = new Sprite(48, 48, 4, 2, SpriteSheet.pieces);
	public static Sprite blue_06 = new Sprite(48, 48, 5, 2, SpriteSheet.pieces);
	public static Sprite blue_07 = new Sprite(48, 48, 0, 3, SpriteSheet.pieces);
	public static Sprite blue_08 = new Sprite(48, 48, 1, 3, SpriteSheet.pieces);
	public static Sprite blue_09 = new Sprite(48, 48, 2, 3, SpriteSheet.pieces);
	public static Sprite blue_10 = new Sprite(48, 48, 3, 3, SpriteSheet.pieces);
	public static Sprite blue_bo = new Sprite(48, 48, 4, 3, SpriteSheet.pieces);
	public static Sprite blue_fl = new Sprite(48, 48, 5, 3, SpriteSheet.pieces);
	public static Sprite blue_unknown = new Sprite(48, 48, 7, 3, SpriteSheet.pieces);

	/* Peças que indicam erros */
	public static Sprite err_001 = new Sprite(48, 48, 6, 0, SpriteSheet.pieces);
	public static Sprite err_002 = new Sprite(48, 48, 6, 1, SpriteSheet.pieces);
	public static Sprite err_003 = new Sprite(48, 48, 6, 2, SpriteSheet.pieces);
	public static Sprite err_004 = new Sprite(48, 48, 6, 3, SpriteSheet.pieces);

	/* Mouse, movimentação e ataque */
	public static Sprite mouse = new Sprite(48, 48, 7, 0, SpriteSheet.pieces);
	public static Sprite move = new Sprite(48, 48, 8, 0, SpriteSheet.pieces);
	public static Sprite attack = new Sprite(48, 48, 9, 0, SpriteSheet.pieces);

	/* Seleção */
	public static Sprite selected_1 = new Sprite(48, 48, 7, 1, SpriteSheet.pieces);
	public static Sprite selected_2 = new Sprite(48, 48, 8, 1, SpriteSheet.pieces);
	public static Sprite selected_3 = new Sprite(48, 48, 9, 1, SpriteSheet.pieces);

	/* Lago e vazio - não devem ser usados */
	public static Sprite lake_ = new Sprite(48, 48, 8, 2, SpriteSheet.pieces);
	public static Sprite void_ = new Sprite(48, 48, 8, 3, SpriteSheet.pieces);

	/* Auxiliares */
	public static Sprite red_turning = new Sprite(48, 48, 9, 2, SpriteSheet.pieces);
	public static Sprite blue_turning = new Sprite(48, 48, 9, 3, SpriteSheet.pieces);

	public Sprite(int xsize, int ysize, int x, int y, SpriteSheet sheet) {
		X_SIZE = xsize;
		Y_SIZE = ysize;
		pixels = new int[X_SIZE * Y_SIZE];
		this.x = x * xsize; // tile de tamanho xsize
		this.y = y * ysize; // tile de tamanho ysize
		this.sheet = sheet;
		load();
	}

	private void load() {
		for (int y = 0; y < Y_SIZE; y++) {
			for (int x = 0; x < X_SIZE; x++) {
				pixels[x + y * X_SIZE]
						= sheet.getPixel((x + this.x) + (y + this.y) * sheet.getXSize());
			}
		}
	}

	public int getWidth() {
		return X_SIZE;
	}

	public int getHeight() {
		return Y_SIZE;
	}

	public int getPixel(int i) {
		return pixels[i];
	}

}
