package ifce.ppd.game.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Carrega imagens (recursos) em formato de vetor de inteiros.
 * 
 * Alteração: apenas a remoção de funcionalidades não utilizadas.
 * 
 * @author The Cherno
 * @author Frota
 *
 */
public class SpriteSheet {

	private String path;
	private int[] pixels;
	private final int X_SIZE;
	private final int Y_SIZE;

	public static SpriteSheet map = new SpriteSheet("/textures/map.png", 480, 480);
	public static SpriteSheet pieces = new SpriteSheet("/pieces/pieces.png", 480, 192);
	public static SpriteSheet text = new SpriteSheet("/textures/text.png", 192, 192);

	public SpriteSheet(String path, int xsize, int ysize) {
		this.path = path;
		X_SIZE = xsize;
		Y_SIZE = ysize;
		pixels = new int[X_SIZE * Y_SIZE];
		load();
	}

	private void load() {
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
			int w = image.getWidth();
			int h = image.getHeight();
			image.getRGB(0, 0, w, h, pixels, 0, w); // ..., offset, scansize
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getPixel(int i) {
		return pixels[i];
	}

	public int getXSize() {
		return X_SIZE;
	}

	public int getYSize() {
		return Y_SIZE;
	}

}
