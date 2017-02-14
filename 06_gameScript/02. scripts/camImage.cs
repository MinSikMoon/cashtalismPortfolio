using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class camImage : MonoBehaviour {
	//PROPERTIES
	private RawImage image;
	private WebCamTexture cam;

	// Use this for initialization
	void Start () {
		image = GetComponent<RawImage> ();
		cam = new WebCamTexture (Screen.width, Screen.height);
		image.texture = cam;
		cam.Play ();


	}

	public void camOff(){
		cam.Stop ();
		SceneManager.LoadScene ("02_main"); //로그인되면 씬전환
	}
	// Update is called once per frame
	void Update () {
		
	}
}
