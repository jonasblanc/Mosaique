import java.awt.image.BufferedImage;

public final class ImageBuilder {

	public ImageBuilder() {
		// TODO Auto-generated constructor stub
	}

	public static void buildRGB(int width, int height, int rgb, String pathToDirectory, String name) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				img.setRGB(x, y, rgb);
			}
		}
		IOManager.saveImage(img, pathToDirectory, name);
	}

}
