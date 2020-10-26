import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ImageProcessing {

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static BufferedImage[] resize(BufferedImage[] images, int newW, int newH) {
		for (int i = 0; i < images.length; ++i) {
			images[i] = resize(images[i], newW, newH);
		}
		return images;
	}

	public static int mean(BufferedImage img) {
		int totalRed = 0;
		int totalBlue = 0;
		int totalGreen = 0;

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int clr = img.getRGB(x, y);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;

				totalRed += red;
				totalBlue += blue;
				totalGreen += green;
			}
		}
		int num_pixel = img.getHeight() * img.getWidth();
		totalRed /= num_pixel;
		totalBlue /= num_pixel;
		totalGreen /= num_pixel;

		totalRed = totalRed << 16;
		totalGreen = totalGreen << 8;

		return totalRed | totalGreen | totalBlue;
	}

	public static BufferedImage toMonochrome(BufferedImage img) {

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {

				Color c = new Color(img.getRGB(x, y));

				int red = (int) (c.getRed() * 0.299);
				int green = (int) (c.getGreen() * 0.587);
				int blue = (int) (c.getBlue() * 0.114);

				Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);

				img.setRGB(x, y, newColor.getRGB());
			}
		}

		return img;
	}

	public static BufferedImage[] toMonochrome(BufferedImage[] images) {
		for (int i = 0; i < images.length; ++i) {
			images[i] = toMonochrome(images[i]);
		}
		return images;
	}

	public static int meanMonochrome(BufferedImage img) {

		long total = 0;
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int clr = img.getRGB(x, y);
				total += clr;

			}
		}
		int num_pixel = img.getHeight() * img.getWidth();

		return (int) (total / num_pixel);
	}

	public static int[] meanMonochrome(BufferedImage[] images) {
		int means[] = new int[images.length];
		for (int i = 0; i < images.length; ++i) {
			means[i] = ImageProcessing.meanMonochrome(images[i]);
		}

		return means;
	}

	public static BufferedImage reduce(BufferedImage img, int numBlocX, int numBlocY) {
		// return computeClusterMonochrome_mean(img, numBlocX, numBlocY);
		return reduce_jump(img, numBlocX, numBlocY);
	}

	private static BufferedImage reduce_mean(BufferedImage img, int numBlocX, int numBlocY) {
		BufferedImage newImg = new BufferedImage(numBlocX, numBlocY, BufferedImage.TYPE_INT_RGB);

		int block_width = img.getWidth() / numBlocX;
		int block_height = img.getHeight() / numBlocY;

		for (int y = 0; y < numBlocY; y++) {
			for (int x = 0; x < numBlocX; x++) {

				int rgb = meanMonochrome(img.getSubimage(x * block_width, y * block_height, block_width, block_height));
				newImg.setRGB(x, y, rgb);
			}
		}

		return toMonochrome(newImg);
	}

	private static BufferedImage reduce_jump(BufferedImage img, int numBlocX, int numBlocY) {
		BufferedImage newImg = new BufferedImage(numBlocX, numBlocY, BufferedImage.TYPE_INT_RGB);

		int block_width = img.getWidth() / numBlocX;
		int block_height = img.getHeight() / numBlocY;

		int boundX = block_width * numBlocX;
		int boundY = block_height * numBlocY;

		for (int y = 0; y < boundY; y += block_height) {
			for (int x = 0; x < boundX; x += block_width) {
				newImg.setRGB(x / block_width, y / block_height, img.getRGB(x, y));
			}
		}

		return newImg;
	}

	public static int findMainRGB(BufferedImage img, int elemPerBucket) {
		int length = 255 / elemPerBucket;
		// if(255 % elemPerBucket != 0) {
		length++;
		// }
		int[][][] bucket = new int[length][length][length];

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int rgb = img.getRGB(x, y);
				Color c = new Color(rgb);
				int red = c.getRed();
				int green = c.getGreen();
				int blue = c.getBlue();

				red = red / elemPerBucket;
				green = green / elemPerBucket;
				blue = blue / elemPerBucket;
				bucket[red][green][blue]++;
			}
		}

		int bestR = 0;
		int bestG = 0;
		int bestB = 0;
		int bestCount = 0;
		for (int r = 0; r < length; ++r) {
			for (int g = 0; g < length; ++g) {
				for (int b = 0; b < length; ++b) {
					int currCount = bucket[r][g][b];

					if (bestCount < currCount) {
						bestR = r;
						bestG = g;
						bestB = b;
						bestCount = currCount;
					}
				}
			}
		}

		bestR *= elemPerBucket;
		bestG *= elemPerBucket;
		bestB *= elemPerBucket;
		/*
		 * if(elemPerBucket >= 2 ) { int midBucket = elemPerBucket / 2; bestR
		 * +=midBucket; bestG +=midBucket; bestB +=midBucket; }
		 */

		return new Color(bestR, bestG, bestB).getRGB();
	}

	public static Map<Integer, List<Integer>> findMainRGB(BufferedImage[] images, int elemPerBucket)
			throws IOException {
		Map m = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < images.length; ++i) {
			int rgb = findMainRGB(images[i], elemPerBucket);
			List<Integer> l = (List<Integer>) m.getOrDefault(rgb, new ArrayList<Integer>());
			l.add(i);
			m.put(rgb, l);
		}
		return m;
	}

	public static int[] findMainRGB2(BufferedImage[] images, int elemPerBucket) {
		int means[] = new int[images.length];
		for (int i = 0; i < images.length; ++i) {
			means[i] = ImageProcessing.findMainRGB(images[i], elemPerBucket);
		}

		return means;
	}

}
