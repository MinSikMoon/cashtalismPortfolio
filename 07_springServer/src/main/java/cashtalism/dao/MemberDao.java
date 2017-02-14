package cashtalism.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.ui.Model;

import cashtalism.vo.Member;

public class MemberDao {
	//1. PROPERTIES
	private DataSource dataSource;
	private Connection conn = null;
	private Statement stmt = null;
	private PreparedStatement pstm = null;
	private ResultSet rs = null;

	//CONSTRUCTOR
	public MemberDao(DataSource dataSource){
		this.dataSource = dataSource;
		System.out.println("memberdao탄생");
	}
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

	//METHODS
	
	//1. login=========================================================
	public String login(String email, String password){
		String memberCheck = null;
		boolean dataCheck = false;
		String tempEmail = email;
		String tempPassword = password;

		//SERVICE
		try{
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from member where email = '" + tempEmail + "'" );

			dataCheck = rs.next();

			if(!dataCheck){ //data가 없거나
				memberCheck = "noData";
			}else if(dataCheck == true && !rs.getString("password").equals(tempPassword)){
				memberCheck = "wrongPassword";
			}else{
				memberCheck = "pass%"+rs.getInt("memberNo")+"%"+tempEmail;
				int memberNo = getMemberNo(tempEmail);
				memberCheck += ("%"+getSearchDistance(memberNo));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
		return memberCheck;
	}

	//2. join===========================================================
	public String join(Model model){
		//1. model에서 꺼내서 join 시켜준다.
		Map map = model.asMap();
		Member newMember = (Member)map.get("newMember");

		String email = newMember.getEmail();
		String password = newMember.getPassword();
		String sql = "insert into member (email,password) values(?,?)";
		try{
			conn = dataSource.getConnection();
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, email);
			pstm.setString(2, password);
			pstm.executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
		return "ok";
	}

	//3. getMemberNo
	public int getMemberNo(String email){
		String query = "select memberNo from member where email = '"+email +"'";
		int memberNo = -1;
		try{
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			rs.next();
			memberNo = rs.getInt("memberNo");

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
		return memberNo;
	}

	//4. setNewbieMoney
	public void setNewbieMoney(int memberNo){
		String sql = "insert into money values(?,50000)";

		try{
			conn = dataSource.getConnection();
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, memberNo);
			pstm.executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
	}

	//5. setNewbieSearchDistance
	public void setNewbieSearchDistance(int memberNo){
		String sql = "insert into searchDistance values(?, 0.5)";

		try{
			conn = dataSource.getConnection();
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, memberNo);
			pstm.executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
	}
	
	//6. getSearchDistance
	public double getSearchDistance(int memberNo){
		String sql = "select searchDistance from searchDistance where memberNo = " + memberNo;
		Double searchDist = 0d;
		try{
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			rs.next();
			searchDist = rs.getDouble("searchDistance");

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
		return searchDist;
	}
	
	//7. getUserMoney
	public int getUserMoney(int memberNo){
		String sql = "select money from money where memberNo = " + memberNo;
		int userMoney = 0;
		try{
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			rs.next();
			userMoney = rs.getInt("money");

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			connCloser(stmt, pstm, conn);
		}
		return userMoney;
	}
	
	
}
