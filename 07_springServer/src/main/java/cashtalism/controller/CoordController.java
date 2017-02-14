package cashtalism.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cashtalism.dao.CoordDao;

@Controller
public class CoordController {
	//PROPERTIES
	private CoordDao coordDao;
	
	//CONSTRUCTOR
	public CoordController(CoordDao coordDao){
		this.coordDao = coordDao;
	}
	
	//METHODS
	//======================1. getDistanceMap
	@ResponseBody
	@RequestMapping("/distance/distanceMap")
	public String getDistanceMap(
			Model model,
			@RequestParam(value = "curLat", required = false)String curLat, 
			@RequestParam(value = "curLng", required = false)String curLng, 
			@RequestParam(value = "searchDist", required = false)String searchDist){
		return coordDao.getDistanceMap(Double.parseDouble(curLat), Double.parseDouble(curLng), Float.parseFloat(searchDist));
	}
	
	//======================2. getComUpDownInfo
	@ResponseBody
	@RequestMapping("/company/updown")
	public String getComUpDownInfo(Model model,
			@RequestParam(value = "comCode", required = false)String comCode){
		return coordDao.getCompanyUpDown(comCode);
	}
	
	//======================3. getComStock
	@ResponseBody
	@RequestMapping("/company/getStock")
	public void getComStock(Model model,
			@RequestParam(value = "memberNo", required = false)int memberNo,
			@RequestParam(value = "comCode", required = false)String comCode,
			@RequestParam(value = "howMany", required = false)int howMany){
		coordDao.getComStock(memberNo, comCode, howMany);
	}
	
	
	
	
}
