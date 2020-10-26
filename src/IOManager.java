import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

public final class IOManager {

	public static BufferedImage loadSingleImage(String pathToImage, String name) {
		String path = pathToImage + "/" + name;
		File file1 = new File(path);

		try {
			return ImageIO.read(file1);
		} catch (IOException e) {
			System.out.println("Loading error while loading: " + path);
			e.printStackTrace();
		}
		return null;
	}

	public static List<BufferedImage> loadAllImagesFromDirectory(String pathToDirectory) {
		// Creating a File object for directory
		File directoryPath = new File(pathToDirectory);
		// List of all files and directories
		String contents[] = directoryPath.list();

		List images = new ArrayList<BufferedImage>();

		for (int i = 0; i < contents.length; i++) {
			if (contents[i].startsWith(".")) {
				continue;
			}

			BufferedImage img = loadSingleImage(pathToDirectory, contents[i]);
			images.add(img);

			System.out.println("Loaded: " + contents[i]);
		}
		return images;
	}

	public static BufferedImage loadSingleImageFromDirectory(String pathToDirectory) {
		File directoryPath = new File(pathToDirectory);
		// List of all files and directories
		String contents[] = directoryPath.list();

		int index = 0;
		while (contents[index].startsWith(".")) {
			index++;
		}

		if (contents.length - index > 1) {
			System.out.println("More than one file in big");
		}

		if (contents.length - index < 1) {
			System.err.println("Please put a photo in big");
		}

		System.out.println("Trying to load: " + contents[index]);

		BufferedImage img = loadSingleImage(pathToDirectory, contents[index]);

		System.out.println("Loaded: " + contents[index]);

		return img;

	}

	public static BufferedImage loadImage(int idx, String pathToDirectory) {
		return loadSingleImage(pathToDirectory, idx + ".jpeg");
	}

	public static void saveImage(BufferedImage img, String pathToDirectory, String name) {
		String path = pathToDirectory + "/" + name + ".jpeg";
		File final_image = new File(path);

		try {
			ImageIO.write(img, "jpeg", final_image);
		} catch (IOException e) {
			System.out.println("Error while saving: " + path);
			e.printStackTrace();
		}
	}

	public static void saveImageWithIndexName(BufferedImage[] images, String pathToDirectory) {
		for (int i = 0; i < images.length; ++i) {
			IOManager.saveImage(images[i], pathToDirectory, "" + i);
		}
	}

	public static void saveImage(String imageUrl, String pathToDirectory) {
		try {
			URL url = new URL(imageUrl);
			String fileName = url.getFile();
			String destName = pathToDirectory + fileName.substring(fileName.lastIndexOf("/"));

			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(destName);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();
		} catch (Exception e) {
			System.out.println("Error while saving: " + imageUrl);
			e.printStackTrace();
		}
		System.out.println("Image saved: " + imageUrl);
	}

	public static void saveArry(int[] array, String pathToDirectory, String filename) {
		try {
			FileWriter myWriter = new FileWriter(pathToDirectory + "/" + filename);

			myWriter.write("" + array.length + "\n");

			for (int i = 0; i < array.length; ++i) {
				myWriter.write("" + array[i] + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Error while saving array in: " + filename);
			e.printStackTrace();
		}

	}

	public static int[] loadArray(String pathToDirectory, String filename) {
		File myObj = new File(pathToDirectory + "/" + filename);

		try {
			Scanner myReader = new Scanner(myObj);
			String lengthString = myReader.nextLine();
			int length = Integer.parseInt(lengthString);
			int array[] = new int[length];

			for (int index = 0; index < length; ++index) {
				String data = myReader.nextLine();
				// int i = myReader.nextInt();
				int i = Integer.parseInt(data);
				array[index] = i;
			}
			myReader.close();
			return array;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static void cleanDirectory(String pathToDirectory) {
		File directoryPath = new File(pathToDirectory);
		String contents[] = directoryPath.list();

		for (int i = 0; i < contents.length; i++) {
			File file = new File(pathToDirectory + "/" + contents[i]);
			file.delete();
		}
	}

}
