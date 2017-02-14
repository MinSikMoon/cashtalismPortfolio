using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class sessionReferer : MonoBehaviour {

	private Text test;
	// Use this for initialization
	void Start () {
		test = GetComponent<Text> ();
		test.text = UserSession.userNo + "/" + UserSession.userEmail;
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
