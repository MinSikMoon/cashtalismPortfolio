using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.SceneManagement;



public class loginBtnCtr : MonoBehaviour {

	//PROPERTIES
	public InputField email, password;
	public GameObject wrongPassword;
	public GameObject joinForm;
	public GameObject wrongpwdbtnCancel;
	private Text emailText, passwordText;
	public Text errorText;
	private string[] wwwParser;
	private string loginUrl = UserSession.rootUrl + "/member/login";

	public void cancerClicked(){
		wrongPassword.SetActive (false);
	}

	public void joinClicked(){
		joinForm.SetActive (true);
	}
	//submit button clicked
	public void submitClicked(){
		//firstly, get email field's text
		//print(email.text); //work fine
		WWWForm form = new WWWForm();
		WWW www;

		form.AddField ("email", email.text);
		form.AddField ("password", password.text);

		www = new WWW (loginUrl, form);
		StartCoroutine (waitForResponse (www)); //www를 url로 쏴준다.

	}

	//coroutine : wait for www
	private IEnumerator waitForResponse(WWW www){
		print ("코루틴시작");
		yield return www; // return only when www is completely downloaded
		//print(www.text);
		//let's split www.text
		wwwParser = www.text.Split('%');
		// 여기서 www를 파싱해서 에러메시지를 띄워준다. 
		//let's parsing www, if www equals to 'pass' make user pri number to static value;
		//when there's no data
		//==================================================================


		if (www.text.Equals ("wrongPassword")) {
			errorText.text = "비밀번호를 잘못 입력하셨습니다.";
			wrongPassword.SetActive (true);
		} else if (www.text.Equals ("noData")) {
			errorText.text = "회원정보가 없습니다.";
			wrongPassword.SetActive (true);
		} else {
			//first, set session userno and useremail
			UserSession.userNo = int.Parse(wwwParser[1]);
			UserSession.userEmail = wwwParser [2];
			UserSession.searchDist = double.Parse (wwwParser [3]);
			print (UserSession.userNo + "," + UserSession.userEmail + "," + UserSession.searchDist);
			SceneManager.LoadScene("02_main"); //로그인되면 씬전환
		}
	
	}


	

}
