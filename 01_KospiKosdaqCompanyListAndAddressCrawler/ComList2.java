package minsik.comList;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



@WebServlet("/ComList2")
public class ComList2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//이 서블릿은 상장회사의 회사코드/ 회사명/ 회사주소를 출력하는 서블릿입니다.
		//1. 일단 회사목록을 가진 html객체를 하나 가져야 겠지? 여기서는 jsoup를 씁시다.
		//org.jsoup.nodes.Document jsoupDoc = Jsoup.connect("http://eng.truefriend.com/tfcommon/popup/CodeSearch.jsp").get();
		org.jsoup.nodes.Document jsoupDoc = Jsoup.connect("http://localhost:8080/jquery01/kospi.html").get();
		Elements elements = jsoupDoc.select("div option");
		// 얘네들은 회사 코드를 가지고 주소를 빼올 목적으로 만든 얘들입니다. / strUrl에 comCode붙여줍니다. 
		String strUrl = "http://dart.fss.or.kr/api/company.xml?auth=[api코드입력]&crp_cd=";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		int checkIdx;
		
		String comChecker = ""; //자회사 입력을 방지합니다.
		int comCheckerLen=0; //자회사 입력 방지용 길이 체크
		boolean escapeFlag;
		
		int cnt = 0; //첫번째 체크용
		
		try {
			builder = factory.newDocumentBuilder();
			//첫 번재 
			for(Element e : elements){
				String temp = e.text(); 
				escapeFlag = false;
				//---------------기본으로 걸러주는 얘들------------
				if(temp.equals("======= 검색결과 ======="))
					continue;
				//알파벳으로 시작되면 걸러준다. 
				if(temp.charAt(0) >= 'A' && temp.charAt(0) <='Z')
					continue;
				//=========================================
				//이제 코드와 이름이 생겼다./ 일단은 이름을 획득했다고 보자. / 여기서도 필터에 걸리면 탈락해줘야함
				String[] comArr = new String[2];
				comArr = temp.split(" ");
				String newCom = comArr[1];
				//System.out.println("i'm " + newCom);
				
				if(newCom.startsWith("KOD") || newCom.startsWith("KOS") || newCom.startsWith("TIGER")
						||newCom.startsWith("TREX")|| newCom.startsWith("KINDEX") 
						||newCom.startsWith("KBSTAR")||newCom.startsWith("GIANT")
						||newCom.startsWith("ARIRANG")||newCom.equals("마이다스")
						||newCom.equals("파워") || newCom.equals("PIONEER")
						||newCom.startsWith("마이티")||newCom.startsWith("KTOP")
						||newCom.startsWith("SMART")||newCom.equals("흥국"))
					continue;
				
				//첫번째 수는 무조건 체커idx가 되니까 무조건 sysout해준다. 
								
				//첫번째로 들어오는 수를 체커로 둔다.
				if(cnt == 0){
					comChecker = comArr[1];
					//comCheckerLen = comArr[1].length();
					//System.out.println("first checker: " + comChecker + "/" + comCheckerLen);
				}
				
				int sameCharNum = 0;
				//체커와 이름을 비교한다. 
				// 체커와 뉴컴 중 길이가 작은 걸로 맞춰준다.
				int minLen;
				
				if(comChecker.length() <= newCom.length()){
					minLen = comChecker.length();
				}else{
					minLen = newCom.length();
					//System.out.println(minLen);
				}
				//System.out.println(comChecker + ":" + newCom + ":" + minLen + ":" + cnt);
				for(int j=0; j<minLen; j++){
					if(cnt == 0) //첫번째 수는 그냥 빠져준다.
						break;
					if(comChecker.charAt(j) == newCom.charAt(j)){
						++sameCharNum;
						//System.out.println("samenum" + sameCharNum);
					}
					if(sameCharNum >= 2){ 
						escapeFlag = true;
						break;
					}
					
				}
				
				//flag가 섰으면 continue한다.
				if(escapeFlag){
					continue;
				}else{ //안섰으면 check바꿔주고 출력해준다.
					comChecker = newCom;
					String comCodeUrl = strUrl + comArr[0]; //이제 이 url로 주소를 빼올 수 있습니다.
					org.w3c.dom.Document w3Doc= builder.parse(comCodeUrl); //이제 해당 주소의 xml파일을 열었습니다.
					NodeList nodeUrl = w3Doc.getElementsByTagName("adr");

					String comAddr = nodeUrl.item(0).getFirstChild().getNodeValue(); //이제 주소가 담겼습니다.
					if(comAddr.charAt(0) >= 'A' && comAddr.charAt(0) <='Z') //외국이면 뺀다.
						continue;
					if(comAddr.charAt(0) >= '0' && comAddr.charAt(0) <='9') //외국이면 뺀다.
						continue;
					if(comAddr.startsWith("일본") || comAddr.startsWith("홍콩"))
						continue;
					if(comAddr == null)
						continue;
					System.out.println(comArr[0] + "&" + comArr[1] + "&" + comAddr);
					
					
				}
				
				cnt++;
			}










			//2. e에는 회사코드/회사명이 한세트로 들어있습니다./ 배열을 만들어서 회사코드만 빼봅시다. 
			/*for(Element e : elements){
				String temp = e.text();

				if(temp.equals("======= 검색결과 ======="))
					continue;
				//알파벳으로 시작되면 걸러준다. 
				if(temp.charAt(0) >= 'A' && temp.charAt(0) <='Z')
					continue;

				String[] comArr = new String[2];
				comArr = temp.split(" ");

				if(cnt == 0){
					comChecker = comArr[1];
					comCheckerLen = comArr[1].length();
					//System.out.println("first checker: " + comChecker + "/" + comCheckerLen);
				}

				//System.out.println(comChecker + "/" + comCheckerLen);
				cnt++;
				//자회사 입력방지 필터 가동 //이름이 같다면 continue 합니다.



				if(comCheckerLen < comArr[1].length() && comChecker.equals(comArr[1].substring(0, comCheckerLen))){
					continue;
				}else if(comCheckerLen == comArr[1].length() && comChecker.substring(0,comCheckerLen-1).equals(comArr[1].substring(0, comCheckerLen-1))){
					continue;
				}else{
					comChecker = comArr[1];
					comCheckerLen = comArr[1].length();
				}
				//이까지 왔으면 주소를 빼줍니다. 
				String comCodeUrl = strUrl + comArr[0]; //이제 이 url로 주소를 빼올 수 있습니다.
				org.w3c.dom.Document w3Doc= builder.parse(comCodeUrl); //이제 해당 주소의 xml파일을 열었습니다.
				NodeList nodeUrl = w3Doc.getElementsByTagName("adr");

				String comAddr = nodeUrl.item(0).getFirstChild().getNodeValue(); //이제 주소가 담겼습니다.
				if(comAddr == null)
					continue;
				System.out.println(temp +" "+ comAddr);

			}*/
		} catch (ParserConfigurationException e1) {

			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
