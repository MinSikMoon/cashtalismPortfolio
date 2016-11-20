package minsik.googleParsingTest;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class GoogleParsingTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "https://www.google.co.kr/search?q=하늘";
		Document doc;
		try {
			doc = Jsoup.connect(url).timeout(4*1000).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
			Elements elements = doc.select(".rg_meta"); //class가 rg_meta인 얘들 
			if(elements.size() == 0){
				System.out.println("no data");
			}
			for(Element e : elements){
				String temp = e.text();
				System.out.println(temp);
				System.out.println(temp.length());
				//첫번째 ou의 위치를 확인한다.
				int ouIdx = temp.indexOf("ou");
				int ouIdx2 = temp.indexOf("\"ou\"");
				System.out.println(ouIdx);
				System.out.println(ouIdx2);
				int owIdx = temp.indexOf("ow");
				int owIdx2 = temp.indexOf("\"ow\"");
				System.out.println(owIdx);
				System.out.println(owIdx2);
				System.out.println(temp.substring(ouIdx2+6, owIdx2-2));
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		//String requestDecoded = new String(request.getParameter("comName").getBytes("8859_1"), "UTF-8");
	}

}
