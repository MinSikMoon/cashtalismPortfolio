package cashtalism.vo;

public class Company {
	
	//PROPERTIES
	int no;
	String code;
	String name;
	String addr;
	double lat,lng;
	String imgUrl;
	
	//CONSTRUCTOR
	public Company(int no, String code, String name, String addr, double lat, double lng, String imgUrl){
		this.no = no;
		this.code = code;
		this.name = name;
		this.addr = addr;
		this.lat = lat;
		this.lng = lng;
		this.imgUrl = imgUrl;
	}
	
	//SETTER & GETTER
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	//TO STRING
	@Override
	public String toString() {
		return "Company [no=" + no + ", code=" + code + ", name=" + name + ", addr=" + addr + ", lat=" + lat + ", lng="
				+ lng + ", imgUrl=" + imgUrl + "]";
	}
	
	
}
