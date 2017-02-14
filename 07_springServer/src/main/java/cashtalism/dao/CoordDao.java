package cashtalism.dao;

import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.PreparedStatement;


public class CoordDao {
	//==========================================INTERNAL METHODS
	//private Methods
	private void connCloser(Statement stmt, PreparedStatement pstm, Connection conn){
		if(stmt != null){
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(pstm != null){
			try {
				pstm.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//코드 만들어주는 내부 메서드
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
	}

	//============================================================
	//1. PROPERTIES
	private DataSource dataSource;
	private Connection conn = null;
	private Statement stmt = null;
	private PreparedStatement pstm = null;
	private ResultSet rs = null;

	private String contentTokenizer = "%";
	private String companyTokenizer = "#";

	//============================================================
	//2. CONSTRUCTOR
	public CoordDao(DataSource dataSource){
		this.dataSource = dataSource;
		System.out.println("coord dao 탄생");
	}

	//============================================================
	//3. METHODS
	//1. getCompanyString
	public String getDistanceMap(double curLat, double curLng, float searchDist){
		//System.out.println("distancemap 들어옴");
		//query 작성
		String query = "select * from ((select * from allcompany where abs(" + curLat + "-lat) < (" + searchDist + "/92))"
				+ " UNION" + " (select * from allcompany where abs(" + curLng + "-lng) < (" + searchDist
				+ "/114)))as roaster" + " where SQRT((POW(((roaster.lat-" + curLat + ")*92),2)+POW(((roaster.lng-"
				+ curLng + ")*114),2))) < " + searchDist;
		String result = "";

		//try catch
		try{

			//connection들 만들기, 쿼리 객체들이랑/ rs 구해냄
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			//이제 unity로 보낼 스트링을 만들어보자. 
			while(rs.next()){
				result += (rs.getInt("priKey") + contentTokenizer);
				result += (rs.getString("code") + contentTokenizer);
				result += (rs.getString("name") + contentTokenizer);
				result += (rs.getString("addr") + contentTokenizer);
				result += (rs.getString("imgUrl") + contentTokenizer);
				result += (rs.getDouble("lat") + contentTokenizer);
				result += (rs.getDouble("lng") + contentTokenizer);
				result += (infoUrlMaker(rs.getString("code")));
				result += companyTokenizer;
			}

			//debugging
			System.out.println("unity에서 distanceMap요구");
			System.out.println(result);


		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				connCloser(stmt, pstm, conn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//--------------------------------------->return result
		return result; //회사정보들이 붙어있는 string 배출
	}
	//=============================================================================================

	//2. getCompanyUpDown
	public String getCompanyUpDown(String comCode){
		//conn들 만들어주기
		String baseUrl = "http://polling.finance.naver.com/api/realtime.nhn?query=SERVICE_ITEM:";
		String targetUrl = baseUrl + comCode;
		String tokenizer = "%";
		String comUpDownInfo = "";

		try{
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
			comUpDownInfo += (curValue + tokenizer);
			comUpDownInfo += (howMuch + tokenizer);

			if(upDown == 0){
				comUpDownInfo += (0+"");
			}else if(upDown < 0){
				comUpDownInfo += (-1+"");
			}else{
				comUpDownInfo += (1+"");
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
		return comUpDownInfo;
	}

	//3. getComStock
	public void getComStock(int memberNo, String comCode, int howMany){
		
		String sql = "insert into stock values (?,?,?)";

		try{
			conn = dataSource.getConnection();
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, memberNo);
			pstm.setString(2, comCode);
			pstm.setInt(3, howMany);
			pstm.executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
	}
}
