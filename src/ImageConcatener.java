import java.awt.image.BufferedImage;

public class ImageConcatener {

	private int widthComponent;
	private int heightComponent;
	private int numComponentByRow;
	private int numComponentByColumn;

	private BufferedImage finalImage;

	public ImageConcatener(int widthComponent, int heightComponent, int numComponentByRow, int numComponentByColumn) {
		this.widthComponent = widthComponent;
		this.heightComponent = heightComponent;
		this.numComponentByRow = numComponentByRow;
		this.numComponentByColumn = numComponentByColumn;
		finalImage = new BufferedImage(numComponentByRow * widthComponent, heightComponent * numComponentByColumn, BufferedImage.TYPE_INT_RGB);
	}

	public void addImageAtIndex(BufferedImage img, int x, int y) {
		if (img.getHeight() != heightComponent || img.getWidth() != widthComponent) {
			throw new IllegalArgumentException("Added image has not the good dimensions");
		}
		if (x < 0 || x >= numComponentByRow || y < 0 || y >= numComponentByColumn) {
			throw new IllegalArgumentException("No such index");
		}

		boolean imageDrawn = finalImage.createGraphics().drawImage(img, x * widthComponent, y * heightComponent, null);
		if (!imageDrawn) {
			System.out.println("Problems drawing first image");
		}
	}

	public BufferedImage build() {
		return finalImage;
	}
}
