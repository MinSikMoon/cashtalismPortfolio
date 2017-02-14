package cashtalism.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cashtalism.dao.MemberDao;
import cashtalism.vo.Member;

@Controller
public class MemberController {
	//PROPERTIES
	private MemberDao memberDao;

	//CONSTRUCTOR
	public MemberController(MemberDao memberDao){
		this.memberDao = memberDao;
	}

	//METHODS
	//=====================1. LOGIN CHECK
	@ResponseBody
	@RequestMapping("/member/login")
	public String loginCheck(Model model, 
			@RequestParam(value = "email", required = false)String email, 
			@RequestParam(value = "password", required = false)String password){

		return memberDao.login(email, password);
	}

	//=====================2. JOIN
	@ResponseBody
	@RequestMapping("/member/join")
	public String join(Model model, 
			@RequestParam(value = "email", required = false)String email,
			@RequestParam(value = "password", required = false)String password){
		//1. request를 받아서 vo를 만들어준다. //model에 담아줌
		Member newMember = new Member(email,password);
		model.addAttribute("newMember", newMember);
		memberDao.join(model);
		//join시켜주고 50000원 넣어줘야한다. 
		int memberNo = memberDao.getMemberNo(email);
		memberDao.setNewbieMoney(memberNo);
		memberDao.setNewbieSearchDistance(memberNo);
		return "ok";
	}

	//====================3. getUserMoney
	@ResponseBody
	@RequestMapping("/member/money")
	public String getUserMoney(Model model,
			@RequestParam(value = "memberNo", required = false)String memberNo){
		return (""+ memberDao.getUserMoney(Integer.parseInt(memberNo)));
	}


}
