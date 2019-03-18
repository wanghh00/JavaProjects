package hello;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Spider {
	static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36";

	public static void main(String[] args) {
		String url = "https://www.cnn.com/";
		
		
		try {
			Connection conn = Jsoup.connect(url).userAgent(USER_AGENT);
			Document html = conn.get();
			
			System.out.println(html.outerHtml());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
