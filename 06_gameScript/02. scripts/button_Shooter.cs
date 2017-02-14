using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class button_Shooter : MonoBehaviour {
	//PROPERTIES
	private Image img;
	private Text descText;
	public GameObject shooterContainer;
	public GameObject prefabBullet;
	private GameObject instantBullet;
	public Text testText;


	//==============test
	private float SHOTCONST = 5;
	public float baseWidth;
	private float xWidth, yHeight;
	private float shotPower;
	private Vector2 startPos, endPos;
	private float mouseDist;
	private float startTime, endTime, timeDiff;
	public float shotTorque;
	//public GameObject mainCamera;

	//close shooter
	public void closeShooter(){
		SceneManager.LoadScene ("02_main");
	}
	//mouseDown/ mouseUp
	void OnMouseDown(){
		img.color = Color.blue;
		descText.text = "눌러짐";
		startPos = Input.mousePosition;
		startTime = Time.time;

	}
	void OnMouseUp(){
		img.color = Color.green;
		descText.text = "누르세요";

		endPos = Input.mousePosition;
		xWidth = baseWidth * (endPos.x / Screen.width)-(baseWidth/2);
		yHeight = endPos.y / Screen.height;
		endTime = Time.time;

		timeDiff = endTime - startTime;
		mouseDist = Vector2.Distance (startPos, endPos);
		shotPower = Mathf.Abs (mouseDist/(timeDiff*5));
		shotPower *= yHeight;

		if (shotPower < 150)
			shotPower = 150;
		if (shotPower > 430)
			shotPower = 430;


		testText.text = "" + mouseDist + "shotpower:" + shotPower; //거리 잘 구해짐

		//이제 눌렀다 떼면 발사되게끔 만들어보자.
		instantBullet = (GameObject)Instantiate(
			prefabBullet,
			//mainCamera.transform.position,
			shooterContainer.transform.position,
			//mainCamera.transform.rotation
			Quaternion.Euler(new Vector3(-100,0,0))
		);

		instantBullet.GetComponent<Rigidbody> ().AddForce (new Vector3(xWidth,yHeight,1f) * shotPower*SHOTCONST);
		instantBullet.GetComponent<Rigidbody> ().AddTorque (new Vector3 (shotTorque*shotPower, shotTorque*shotPower*yHeight, shotPower));

	}
	// Use this for initialization
	void Start () {
		img = GetComponent<Image> ();
		img.color = Color.green;
		descText = GetComponentInChildren<Text> ();
		descText.text = "누르세요";

	}

	void Update(){
		testText.text = Input.mousePosition.ToString ();
	}


}
