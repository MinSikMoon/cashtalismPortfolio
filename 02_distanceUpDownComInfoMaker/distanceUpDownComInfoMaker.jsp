<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.JSONValue"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="com.mysql.jdbc.Driver"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%!//코드 만들어주는 내부 메서드
	private String codeMaker(String comCode) {

		if (comCode.substring(5).equals("0")) {
			comCode = comCode.substring(0, 5);
		}
		return comCode;
	}
	//infoUrl 만들어주는 메서드
	private String infoUrlMaker(String comCode) {
		comCode = codeMaker(comCode);
		String infoUrl = "http://kind.krx.co.kr/common/companysummary.do?method=searchCompanySummary&strIsurCd=";
		infoUrl += comCode;
		return infoUrl;
	}%>
<%
	//먼저 request로 좌표와 검색반경 받아온다.
	//서블릿으로 토스해서 json리스트 받아온다.
	//일단은 그냥 통짜로 다 넣어보자. 귀찮으니까
	double curLat = Double.parseDouble(request.getParameter("curLat"));
	double curLng = Double.parseDouble(request.getParameter("curLng"));
	float searchDist = Float.parseFloat(request.getParameter("searchDist"));

	//리스트를 받아와서 json 리스트를 만든다.
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String query = null;
	String comJson = "";

	query = "select * from ((select * from allCompany where abs(" + curLat + "-lat) < (" + searchDist + "/92))"
			+ " UNION" + " (select * from allCompany where abs(" + curLng + "-lng) < (" + searchDist
			+ "/114)))as roaster" + " where SQRT((POW(((roaster.lat-" + curLat + ")*92),2)+POW(((roaster.lng-"
			+ curLng + ")*114),2))) < " + searchDist;
	//System.out.println(query);
	try {
		//System.out.println("in");

		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		conn = DriverManager.getConnection("jdbc:mysql://localhost/studydb", "[dbid]", "[dbpassword]");
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);

		//rs로 돌면서 json 만들어주자. //infoUrl도 만들어서 붙여줘야함. 
		comJson += "[";
		//comJson을 만들때 이미 upDown 정보를 가르쳐 주자. 
		//baseUrl에 회사코드를 덧 붙여준다. 
		String baseUrl = "http://polling.finance.naver.com/api/realtime.nhn?query=SERVICE_ITEM:";
		//URL url = new URL("http://polling.finance.naver.com/api/realtime.nhn?query=SERVICE_ITEM:047560");
		
		while (rs.next()) {
			comJson += "{";
			comJson += ("comCode: \'" + rs.getString("code") + "\', ");
			//-------------------------upDown 알아보기
			//comCode를 알았으니 upDown 정보도 알아보자. 
			String targetUrl = baseUrl + rs.getString("code");
			URL url = new URL(targetUrl);
			InputStreamReader inReader = new InputStreamReader(url.openConnection().getInputStream(), "UTF-8");
			JSONObject obj = (JSONObject)JSONValue.parseWithException(inReader);
			JSONObject result = (JSONObject)(obj.get("result")); //result json을 받는다.
			// Json 객체를 파싱한다. 
			//result안의 areas JSON 배열을 받는다.
			JSONArray areasArr = (JSONArray)result.get("areas");
			//areas 안에 datas라는 json 배열을 받는다.
			JSONObject datasObj = (JSONObject)areasArr.get(0);
			JSONArray datasArr = (JSONArray)datasObj.get("datas");
			//datasArr 안의 nv를 가져온다.
			// ov에서 전일가인 sv를 뺐을때 +이면 상승 0 이면 보합 -이면 하락했다는 뜻/ 상승은 1/ 보합은 0/ 하락은 -1로 나타내자. /상하락률은 cv이다.
			JSONObject finalObj = (JSONObject)datasArr.get(0);
			//System.out.println(finalObj.get("ov").toString());
			//1. 현재가 구하기
			int curValue = Integer.parseInt(finalObj.get("nv").toString());
			int prevValue = Integer.parseInt(finalObj.get("sv").toString());
			int howMuch =  Integer.parseInt(finalObj.get("cv").toString());
			int upDown = curValue-prevValue;
			// UpDown 정보를 comJson에 기록해준다.//보합은 0/ 상승은 1/ 하락은 -1
			comJson += ("curValue: " + curValue + ", ");
			comJson += ("howMuch: " + howMuch + ", ");
			if(upDown == 0){
				comJson += ("updown: 0, ");
			}else if(upDown < 0){
				comJson += ("updown: -1, ");
			}else{
				comJson += ("updown: 1, ");
			}
			//==============================
			comJson += ("title: \'" + rs.getString("name") + "\', ");
			comJson += ("addr: \'" + rs.getString("addr") + "\', ");
			comJson += ("comLogo: \'" + rs.getString("imgUrl")+"\', ");
			comJson += ("latlng: new daum.maps.LatLng(" + rs.getDouble("lat") + ", " + rs.getDouble("lng")
					+ ") , ");
			comJson += ("infoUrl: \'" + infoUrlMaker(rs.getString("code")) + "\'");
			comJson += "}";
			if (!rs.isLast()) {
				comJson += ",";

			}
		}
		comJson += "]";
		//comJson 완성!
		
		System.out.println(comJson);
		//System.out.println("done");
	} catch (Exception e) {

	} finally {
		try {
			stmt.close();
			conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	//============================================HTML START==============================================
%>
<html>
<head>
<script src='http://code.jquery.com/jquery.min.js'></script>
<script type="text/javascript"
	src="//apis.daum.net/maps/maps3.js?apikey=[api키]&libraries=clusterer"></script>
<link rel="stylesheet" type="text/css" href="../css/daumMapCss.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>현재 좌표(위도경도)를 받아서 자바스크립트로 내 주변 기업들의 좌표를 뿌려주는 프로그램</title>
</head>

<body>

	<!-- 1. 지도가 표시될 맵div/ 일명 map 이라는 id로 불린다.  -->
	<div id="map" style="width: 100%; height: 500px;"></div>

	<script>
		//맵 컨테이너와 맵 옵션을 만든다. 
		var mapContainer = document.getElementById('map'), // 지도의 중심좌표
		mapOption = {
			center : new daum.maps.LatLng(<%=curLat%>, 	<%=curLng%>	), // 지도의 중심좌표
			level : 7	// 지도의 확대 레벨
		};
		//지도를 생성한다. 
		var map = new daum.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
		//클러스터러를 만들고 내가 만들 클러스터들에 대해 정의한다. 
		// 포지션 배열을 가져온다.

		var positions =	<%=comJson%>;  //comJson을 넣었다. 

		//컨텐츠 html 조립인자 //contents1 과 contets2 사이에 closeoverlay 함수를 조립한다. 
		var contentsMaker = function(title, addr, i,curValue, howMuch, infoUrl, comLogo) {
			return '<div class="wrap">'
					+ '    <div class="info">'
					+ '        <div class="title">'
					+ title
					+ '<div class="close" onclick="closeOverlay('
					+ i
					+ ')"'
					+ 'title="닫기"></div>'
					+ '        </div>'
					+ '        <div class="body">'
					+ '            <div class="img">'
					+ '                <img src="'
					+ comLogo + '" width="73" height="70">'
					+ '           </div>'
					+ '            <div class="desc"> <div class="ellipsis">'
					+ '현재가 : '+curValue + '원 (' + howMuch + '원)' 
					+ '</div>'
					+ '                <div class="jibun ellipsis">'
					+ addr
					+'</div>'
					+ '                <div><a href="'
					+ infoUrl					
					+'" target="_blank" class="link">회사재무요약</a></div>'
					+ '            </div>' + '        </div>' + '    </div>'
					+ '</div>';
		}
	
		//마커의 이미지 객체를 정의한다. 
		//기본 마커1
		var imageSrc = "http://i1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";
		var imageSize = new daum.maps.Size(12, 17);
		var markerImage = new daum.maps.MarkerImage(imageSrc, imageSize);
		//내 위치 마커2
		var imageSrc_my = "http://pixeljoint.com/files/icons/full/walking2__r1634476901.gif";
		var imageSize_my = new daum.maps.Size(30,40);
		var markerImage_my = new daum.maps.MarkerImage(imageSrc_my, imageSize_my);
		
		var imageSrc_neon = "../neon2.png";
		var imageSize_neon = new daum.maps.Size(40,50);
		var markerImage_neon = new daum.maps.MarkerImage(imageSrc_neon, imageSize_neon);
		//상승, 보합, 하락 마커들을 정해준다. 
		//http://www.epfotamilnadu.tn.nic.in/images/top.gif
		var imageSrc_up= "../markers/up.gif";
		var imageSize_up = new daum.maps.Size(17,22);
		var markerImage_up = new daum.maps.MarkerImage(imageSrc_up, imageSize_up);
		//http://www.gifs.cc/arrows/blue-animated-arrow-down.gif
		var imageSrc_down= "../markers/down.gif";
		var imageSize_down = new daum.maps.Size(17,22);
		var markerImage_down = new daum.maps.MarkerImage(imageSrc_down, imageSize_down);
		//https://d30y9cdsu7xlg0.cloudfront.net/png/110750-200.png
		var imageSrc_equal= "../markers/equal.png";
		var imageSize_equal = new daum.maps.Size(17,17);
		var markerImage_equal = new daum.maps.MarkerImage(imageSrc_equal, imageSize_equal);
		
		//----------------------내 현재 위치 찍어주는것
		var myPosition =  new daum.maps.LatLng(<%=curLat%>, <%=curLng%>);
		//neon 효과 주기
	 	var myNeonMarker = new daum.maps.Marker({
			position : myPosition,
			title : "네온"
		});
	 	myNeonMarker.setMap(map);
	 	myNeonMarker.setImage(markerImage_neon);
	 	
		var myMarker = new daum.maps.Marker({
			position : myPosition,
			title : "현재 위치"
		});
	 	myMarker.setMap(map);
	 	myMarker.setImage(markerImage_my);
	 	
	 	
	 	//내 위치 주변으로 반경 원도 그려준다. 
	 	var circle = new daum.maps.Circle({
		    center : myPosition,  // 원의 중심좌표 입니다 
		    radius: <%=(searchDist+Math.pow((searchDist*0.195),1.15))*1000%>, // 미터 단위의 원의 반지름입니다 
		    strokeWeight: 5, // 선의 두께입니다 
		    strokeColor: '#48FD2C', // 선의 색깔입니다
		    strokeOpacity: 1, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
		    strokeStyle: 'dashed', // 선의 스타일 입니다
		    fillColor: '#48FD2C', // 채우기 색깔입니다
		    fillOpacity: 0.3  // 채우기 불투명도 입니다   
		}); 

		// 지도에 원을 표시합니다 
		circle.setMap(map); 
		//------------------------------------------------------------------
		var overlayArr = new Array();
		var closeOverlay = function(i) {
			overlayArr[i].setMap(null);
		}
		var comIdx = 0; //얘를 증가시키자.
		//기본적으로 다음맵을 다룰때는 jquery를 쓰는듯
		var markers = $.map(positions, function(company){
			var marker = new daum.maps.Marker({
				position : company.latlng,
				title : company.title,
				//updown : company.updown //이걸로 상승마커를 달지 하락마커를 달지 보합마커를 달지 결정
			});
			marker.setMap(map);
			var comContent;
			
			if(company.updown == 0){
				marker.setImage(markerImage_equal);
				comContent = contentsMaker(marker.getTitle(), company.addr , comIdx, company.curValue, company.howMuch, company.infoUrl, company.comLogo);
			}else if(company.updown == -1){
				marker.setImage(markerImage_down);
				comContent = contentsMaker(marker.getTitle(), company.addr , comIdx, company.curValue, '-' + company.howMuch, company.infoUrl, company.comLogo);
			}else{
				marker.setImage(markerImage_up);
				comContent = contentsMaker(marker.getTitle(), company.addr , comIdx, company.curValue, '+' + company.howMuch, company.infoUrl, company.comLogo);
			}
			//marker.setImage(markerImage);
			
			var overlay = new daum.maps.CustomOverlay({
				content : comContent,
				map : map,
				position : marker.getPosition()
			});
			overlayArr[comIdx] = overlay; //배열에 연결시켜줌
			comIdx++; //하나씩 증가시켜줌.				
			daum.maps.event.addListener(marker, 'click', function() {
				overlay.setMap(map);
				overlay.setVisible(true);
			});
			overlay.setVisible(false);
			return marker;
		});

	</script>
</body>

</html>