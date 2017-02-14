using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class moneyCtrl : MonoBehaviour {
	// PROPERTIES
	// 5초에 한번씩 유저 재산의 text가 갱신된다.
	private Text moneyText; 
	private int userNo = UserSession.userNo;
	private string moneyUrl = UserSession.rootUrl + "/member/money?memberNo=" + UserSession.userNo;
	private WWW www;

	// Use this for initialization
	void Start () {
		moneyText = GetComponent<Text> ();
		www = new WWW (moneyUrl);
		StartCoroutine(getMoney (www));
		StartCoroutine (repeator ());
	}

	//5 repeater
	IEnumerator repeator(){
		yield return new WaitForSeconds (3.0f);

		//원하는 메서드 삽입
		www = new WWW (moneyUrl);
		StartCoroutine(getMoney (www));

		//재귀적으로 다시 실행
		StartCoroutine(repeator ());
	}

	// show Money coroutine
	IEnumerator getMoney(WWW www){
		yield return www;
		moneyText.text = www.text;
		print (www.text);
	}
	
}
