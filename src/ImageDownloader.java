import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class ImageDownloader {

	public static void downloadFromGoogleImage(String searchTerm, String pathToDirectory, int numberMaxImages) {
		String url = "https://www.google.com/search?tbm=isch&q=" + searchTerm + "/";
		System.out.println("Search url: " + url);
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			System.out.println("Error while trying to connect: " + url);
			e.printStackTrace();
		}
		Elements img = doc.getElementsByTag("img");
		for (Element el : img) {
			String src = el.absUrl("data-src");
			System.out.println("Image Found!");
			System.out.println("src attribute is : " + src);
			int count = 0;
			if (src.contains(".")) {
				if (count >= numberMaxImages) {
					return;
				}
				IOManager.saveImage(src, pathToDirectory);
				count++;
			}
		}
	}

	public static void downloadFromGoogleImage(String searchTerm, String pathToDirectory) {
		downloadFromGoogleImage(searchTerm, pathToDirectory, 500);
	}

}
