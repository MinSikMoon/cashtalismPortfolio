using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class joinBtnCtr : MonoBehaviour {
	//PROPERTIES
	public InputField emailInput, passwordInput, passwordConfirmInput;
	public Text errorText;
	public GameObject wrongPassword, joinSuccess;
	private string joinUrl = UserSession.rootUrl + "/member/join";

	//==================================confirm password
	private bool confirmPassword(){
		if (passwordInput.text.Equals (passwordConfirmInput.text))
			return true;
		else
			return false;
	}
	//===================================joinSuccess cancelClick
	public void joinSuccessCancelClick(){
		joinSuccess.SetActive (false);
		this.GetComponent<Transform> ().gameObject.SetActive (false);
	}
	//===================================join coroutine
	private IEnumerator waitForResponse(WWW www){
		yield return www;

		if (www.text.Equals ("ok")) {
			print (www.text);
			joinSuccess.SetActive (true);

		} else {
			errorText.text = "가입 오류";
			wrongPassword.SetActive(true);
		}
	}

	//==================================when you hit "가입하기"
	public void clickJoin(){
		//1. confirm password
		if (confirmPassword ()) {
			//www로 보내기
			WWWForm form = new WWWForm();
			WWW www;

			form.AddField("email",emailInput.text);
			form.AddField ("password", passwordInput.text);
			www = new WWW (joinUrl, form);
			StartCoroutine (waitForResponse (www));

		} else { //if password is not equal, show passwordConfirmErrorForm
			errorText.text = "재확인 비밀번호가 맞지 않습니다.";
			wrongPassword.SetActive(true);
		}
	}

}
