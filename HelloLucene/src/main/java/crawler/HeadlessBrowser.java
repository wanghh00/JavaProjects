package crawler;

import java.io.File;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class HeadlessBrowser {
	
	static final Logger LOG = Logger.getLogger(HeadlessBrowser.class);

	public static void main1(String[] args) {
		String url = "https://www.cnn.com";

		ChromeDriver driver = new ChromeDriver();
		driver.get(url);

		System.out.println(driver.getPageSource());

		// System.out.println("Title of the page is" + driver.getTitle());

		// System.out.println("Title of the page now is " + driver.getTitle());
		driver.quit();

	}

	public static void main(String[] args) {
		
		LOG.info("hehe");
		
		
		String url = "https://www.cnn.com/us";

		//File file = new File("/Users/saraddhungel/Downloads/phantomjs");
		//System.setProperty("phantomjs.binary.path", file.getAbsolutePath());

		WebDriver driver = new PhantomJSDriver();
		driver.get(url);
		
		Document doc = Jsoup.parse(driver.getPageSource());
		for (Element elem : doc.select("article")) {
			LOG.info(elem.text());
		}
		
		// System.out.println(driver.getPageSource());
		// System.out.println(doc.text());
		
		driver.quit();
	}
}
