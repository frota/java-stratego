package ifce.ppd.game.gfx;

/**
 * Permite usar um vetor de inteiros como uma tela de desenho, onde se pode
 * usar instâncias de Sprite.
 * 
 * Alteração: apenas a remoção de funcionalidades não utilizadas.
 * 
 * @author The Cherno
 * @author Frota
 *
 */
public class Screen {

	public static final int TRANSPARENCY = -65281; // 0xffff00ff

	private int[] pixels;
	private int width, height;

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = TRANSPARENCY;
		}
	}

	/**
	 * Renderiza um sprite na posição (xp, yp) desejada deste Screen.
	 * @param xp Início da renderização de sprite.
	 * @param yp Início da renderização de sprite.
	 * @param sprite Sprite a ser renderizado.
	 */
	public void renderSprite(int xp, int yp, Sprite sprite, boolean fixed) {
		for (int y = 0; y < sprite.getHeight(); y++) {
			int ya = y + yp;
			for (int x = 0; x < sprite.getWidth(); x++) {
				int xa = x + xp;
				if (xa < 0 || xa >= width || ya < 0 || ya >= height) {
					continue;
				}
				if (sprite.getPixel(x + y * sprite.getWidth()) != TRANSPARENCY) {
					pixels[xa + ya * width] =
							sprite.getPixel(x + y * sprite.getWidth());
				}
			}	
		}
	}

	public int getPixel(int i) {
		return pixels[i];
	}

}
