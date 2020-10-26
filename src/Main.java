import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public final class Main {

	// Available color comparaison methods for you to try
	private static enum TYPES {
		RGB, RGB_RDM, LAB, MONOCHROME
	};

	// =================== Play with this variables ===================//
	private static final TYPES TYPE = TYPES.RGB_RDM;
	private static final String SEARCH_TERM = "landscape";
	private static final boolean DO_YOU_PROVIDE_IMAGES = true;
	private static final int NUMBER_COMPONENT_X = 70;
	private static final int NUMBER_COMPONENT_Y = 70;
	private static final int SCALE_FACTOR_COMPONENT = 22;
	// =================== Play with this variables ===================//

	// =================== Results settings ===================//
	private static final String RESULT_DIR_PATH = "images/output/final";
	private static final String RESULT_NAME = "final";
	private static final String COMP_DIR_PATH = "images/output/comparaison";
	private static final String COMP_NAME = "comp";
	// =================== Results settings ===================//

	// =================== Do not modifiy ===================//
	private static final String COMPONENT_DIR_PATH = DO_YOU_PROVIDE_IMAGES ? "images/input/smalls" : "images/download";
	private static final String MAIN_IMAGE_DIR_PATH = "images/input/big";
	private static final int THREESHOLD = 1000000;
	private static final String TEMP_DIR_PATH = "images/temp";
	private static final String DOWNLOAD_DIR_PATH = "images/download";
	private static final String MEANS_FILENAME = "means.txt";
	// =================== Do not modifiy ===================//

	public static void main(String[] args) throws IOException {

		// Download image library
		if (!DO_YOU_PROVIDE_IMAGES) {
			IOManager.cleanDirectory(DOWNLOAD_DIR_PATH);
			ImageDownloader.downloadFromGoogleImage(SEARCH_TERM, DOWNLOAD_DIR_PATH);
		}

		// Preprocess image
		BufferedImage original = IOManager.loadSingleImageFromDirectory(MAIN_IMAGE_DIR_PATH);
		original = resizeOriginal(original, THREESHOLD);

		// Preprocess components
		int sizeComponentX = SCALE_FACTOR_COMPONENT * original.getWidth() / NUMBER_COMPONENT_X;
		int sizeComponentY = SCALE_FACTOR_COMPONENT * original.getHeight() / NUMBER_COMPONENT_Y;
		preprocessImages(COMPONENT_DIR_PATH, TEMP_DIR_PATH, sizeComponentX, sizeComponentY, TYPE);

		// Build result
		BufferedImage result = buildResult(original, TEMP_DIR_PATH, RESULT_DIR_PATH, RESULT_NAME, TYPE);

		// Build comparaison
		buildComp(original, result, COMP_DIR_PATH, COMP_NAME);

		System.out.println("Done !");
	}

	private static BufferedImage resizeOriginal(BufferedImage original, int threeshold) {
		double width = original.getWidth();
		double height = original.getHeight();

		double size = width * height;
		double scaleFactor = 7.0 / 8.0;
		int count = 0;
		while (size > THREESHOLD) {
			size *= scaleFactor;
			count++;
		}
		while (count != 0) {
			width *= scaleFactor;
			height *= scaleFactor;
			count--;
		}
		return ImageProcessing.resize(original, (int) width, (int) height);
	}

	private static void preprocessImages(String fromDirectory, String toDirectory, int imagesW, int imagesH, TYPES type) {
		System.out.println("Start preprocessing components...");

		List<BufferedImage> l = IOManager.loadAllImagesFromDirectory(fromDirectory);
		BufferedImage images[] = new BufferedImage[l.size()];
		l.toArray(images);

		images = ImageProcessing.resize(images, imagesW, imagesH);

		int[] means = new int[images.length];

		switch (type) {
		case RGB:
		case RGB_RDM:
		case LAB:
			means = ImageProcessing.findMainRGB2(images, 7);
			break;
		case MONOCHROME:
			images = ImageProcessing.toMonochrome(images);
			means = ImageProcessing.meanMonochrome(images);
			break;
		}

		IOManager.cleanDirectory(toDirectory);
		IOManager.saveArry(means, toDirectory, "means.txt");
		IOManager.saveImageWithIndexName(images, toDirectory);

		System.out.println("Preprocessing completed !");
		System.out.println();
	}

	private static BufferedImage buildResult(BufferedImage image, String pathToComponents, String pathToResult, String resultName, TYPES type) {
		System.out.println("Start building result...");

		int[] means = IOManager.loadArray(pathToComponents, MEANS_FILENAME);

		int sizeComponentX = SCALE_FACTOR_COMPONENT * image.getWidth() / NUMBER_COMPONENT_X;
		int sizeComponentY = SCALE_FACTOR_COMPONENT * image.getHeight() / NUMBER_COMPONENT_Y;

		ImageConcatener imgConCat = new ImageConcatener(sizeComponentX, sizeComponentY, NUMBER_COMPONENT_X, NUMBER_COMPONENT_Y);

		if (TYPE == TYPES.MONOCHROME) {
			image = ImageProcessing.toMonochrome(image);
		}

		BufferedImage minimize = ImageProcessing.reduce(image, NUMBER_COMPONENT_X, NUMBER_COMPONENT_Y);

		Random rdm = new Random();
		double[][] meansLAB = type == TYPES.LAB ? ColorTools.RBGtoCIELAB(means) : new double[0][0];

		for (int x = 0; x < minimize.getWidth(); ++x) {
			for (int y = 0; y < minimize.getHeight(); ++y) {
				int idx = 0;
				switch (type) {
				case RGB:
					idx = ColorTools.findClosestRGB(means, minimize.getRGB(x, y));
					break;
				case RGB_RDM:
					int[] idxesRGB = ColorTools.findXClosestsRGB(means, minimize.getRGB(x, y));
					idx = idxesRGB[rdm.nextInt(idxesRGB.length)];
					break;
				case LAB:
					idx = ColorTools.findClosestCIELAB(meansLAB, ColorTools.RBGtoCIELAB(minimize.getRGB(x, y)));
					break;
				case MONOCHROME:
					idx = ColorTools.findClosestMonochrome(means, minimize.getRGB(x, y));
					break;
				}
				imgConCat.addImageAtIndex(IOManager.loadImage(idx, TEMP_DIR_PATH), x, y);
			}
		}

		BufferedImage result = imgConCat.build();
		IOManager.saveImage(result, pathToResult, resultName);

		System.out.println("Result built saved in " + pathToResult + " with name " + resultName + ".jpeg");
		System.out.println();

		return result;
	}

	private static void buildComp(BufferedImage original, BufferedImage result, String pathToComp, String compName) {
		System.out.println("Start building comparaison...");

		result = ImageProcessing.resize(result, original.getWidth(), original.getHeight());

		ImageConcatener comp = new ImageConcatener(original.getWidth(), original.getHeight(), 2, 1);
		comp.addImageAtIndex(original, 0, 0);
		comp.addImageAtIndex(result, 1, 0);

		BufferedImage compFinal = comp.build();
		IOManager.saveImage(compFinal, pathToComp, compName);

		System.out.println("Comparaison saved in " + pathToComp + " with name " + compName + ".jpeg");
		System.out.println();

	}
}
