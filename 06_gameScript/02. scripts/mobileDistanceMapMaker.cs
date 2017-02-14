using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Text;
using UnityEngine.SceneManagement;


public class mobileDistanceMapMaker : MonoBehaviour
{
	//PROPERTIES
	private string distanceMapUrl = UserSession.rootUrl + "/distance/distanceMap";
	private string curUrl;
	private float curLat, curLng;
	private string[] companies;
	public GameObject buildingPrefab;
	private int RADIUS = 1000;
	//1000배로 확대해라는 뜻
	public GameObject buildingContainer;
	private LocationInfo curGPSPosition;
	private bool isRotated = false;
	private bool isOn = false;
	//PROPERTIES for sprite
	private Texture2D txture;
	private SpriteRenderer spriteRenderer;
	private Sprite sprite;
	public GameObject[] sprites;
	private TextMesh[] tempLogoArr;
	//COMPASS properties


	public void closeMap(){
		if (isOn == true) { //켜져있으면 다 없앤다. 
			foreach (Transform child in buildingContainer.transform) {
				Destroy (child.gameObject);
			}
		} else {
			isOn = true;
		}
		SceneManager.LoadScene ("02_main");
	}
	//===========================================METHODS
	//============================================START
	// Use this for initialization
	void Start ()
	{
		//Gps를 켠다.
		Input.location.Start (0.5f);
		int wait = 1000;
		if (Input.location.isEnabledByUser) {
			while (Input.location.status == LocationServiceStatus.Initializing && wait > 0) {
				wait--;
			}	
			if (Input.location.status == LocationServiceStatus.Failed) {
			} else {
				//gpsInit = true;
				InvokeRepeating ("GetGPSData", 0f, 3f); //if gps is inited, then retrieve gps data every 3 seconds
			}
		} else {
			//fail message
		}
	}


	//=========================================makeDistanceMap
	public void makeDistanceMap ()
	{
		
		GetGPSData (); //curlat와 curlng를 만든다. 
//		curLat = 33.4457f;
//		curLng = 126.5715f;
		curUrl = distanceMapUrl + "?curLat=" + curLat + "&curLng=" + curLng + "&searchDist=1";
		WWW www;
		www = new WWW (curUrl);
		StartCoroutine (waitForResponse (www)); //www를 url로 쏴준다.
	}

	//=====================================시연용 gangnam station
	public void makeGanaNamDistanceMap ()
	{
		
		curLat = 37.5045700f;
		curLng = 127.0494460f;
		curUrl = distanceMapUrl + "?curLat=" + curLat + "&curLng=" + curLng + "&searchDist=1";
		WWW www;
		www = new WWW (curUrl);
		StartCoroutine (waitForResponse (www)); //www를 url로 쏴준다.
	}

	//=========================================GetGpsData
	void GetGPSData ()
	{
		curGPSPosition = Input.location.lastData;
		curLat = curGPSPosition.latitude;
		curLng = curGPSPosition.longitude;

	}

	//===============================================coroutine : wait for www
	private IEnumerator waitForResponse (WWW www)
	{
		if (isOn == true) { //켜져있으면 다 없앤다. 
			foreach (Transform child in buildingContainer.transform) {
				Destroy (child.gameObject);
			}
		} else {
			isOn = true;
		}
		if (isRotated == true)
			buildingContainer.transform.Rotate (new Vector3 (0, -90, 0));

		//1. 먼저 usersession.comArr을 init
		UserSession.comArr = null;
		UserSession.comArr = new ArrayList ();

		yield return www; // return only when www is completely downloaded

		companies = www.text.Split ('#');

		foreach (string company in companies) {
			if (company.Equals (""))
				continue;
			//지금 내가 가진 회사정보string 배열을 usersession에 넣어준다. 
			//이제 session에서 parsing이 안된 회사 정보들이 들어있다. 
			UserSession.comArr.Add (company);


			//print (company + "\n");
			string[] companyInfoArr = company.Split ('%');
			float comLat = float.Parse (companyInfoArr [5]);
			float comLng = float.Parse (companyInfoArr [6]);

			//curLat와의 차이만큼 position x 더해줌
			float latDiff = -(comLat - curLat) * 92; //lat
			float lngDiff = (comLng - curLng) * 114; //lng

			//position을 더해주고 instantiate해준다. //이제 빌딩이 완성되었다.
			GameObject tempBuilding = (GameObject)Instantiate (
				                          buildingPrefab,
				                          new Vector3 (latDiff, 0, lngDiff) * RADIUS,
				                          Quaternion.identity
			                          );
			tempBuilding.transform.parent = buildingContainer.transform;
			tempLogoArr = tempBuilding.GetComponentsInChildren<TextMesh> ();
			foreach (TextMesh tm in tempLogoArr) {
				tm.text = companyInfoArr [2]; //comName
			}

		}

		if (isRotated == false) { //아직 한번도 안되어 있다면 돌린다./ 그리고 rotated true로 바꾼다.
			buildingContainer.transform.Rotate (new Vector3 (0, 90, 0));
			isRotated = true;
		} else
			buildingContainer.transform.Rotate (new Vector3 (0, 90, 0));

	}
}
