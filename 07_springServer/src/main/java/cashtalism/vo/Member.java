package cashtalism.vo;

public class Member {
	//PROPERTIES
	private String email;
	private String password;
	
	
	//CONSTRUCTOR
	public Member(String email, String password){
		this.email = email;
		this.password = password;
	}

	//GETTER & SETTER
	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	//TO STRING
	@Override
	public String toString() {
		return "Member [email=" + email + ", password=" + password + "]";
	}
	
	
	
}
