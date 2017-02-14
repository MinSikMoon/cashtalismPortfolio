using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Text;


public class distanceMapMaker : MonoBehaviour {
	//PROPERTIES
	private string distanceMapUrl = UserSession.rootUrl + "/distance/distanceMap";
	private string curUrl;
	private float curLat, curLng;
	private string[] companies;
	public GameObject buildingPrefab;
	private int RADIUS = 1000; //1000배로 확대해라는 뜻
	public GameObject buildingContainer;
	private LocationInfo curGPSPosition;
	private bool isRotated = false;
	//PROPERTIES for sprite
	private Texture2D txture;
	private SpriteRenderer spriteRenderer;
	private Sprite sprite;
	public GameObject[] sprites;
	private TextMesh[] tempLogoArr;

	//===========================================METHODS
	//============================================START
	// Use this for initialization
	void Start () {
		//Gps를 켠다.
		Input.location.Start(0.5f);
		int wait = 1000;
		if(Input.location.isEnabledByUser){
			while(Input.location.status == LocationServiceStatus.Initializing && wait>0){
				wait--;
			}	
			if (Input.location.status == LocationServiceStatus.Failed) {
			}
			else {
				//gpsInit = true;
				InvokeRepeating("GetGPSData", 0f, 3f); //if gps is inited, then retrieve gps data every 3 seconds
			}
		}
		else{
			//fail message
			//logtxt = "GPS is not availabel";
		}
	}

	//=========================================makeDistanceMap
	public void makeDistanceMap(){
		//GetGPSData (); //curlat와 curlng를 만든다. 
		curLat = 33.4457f;
		curLng = 126.5715f;
		curUrl = distanceMapUrl + "?curLat=" + curLat + "&curLng=" + curLng + "&searchDist=1";
		WWW www;
		www = new WWW (curUrl);
		StartCoroutine (waitForResponse (www)); //www를 url로 쏴준다.
	}

	//=========================================GetGpsData
	void GetGPSData(){
		curGPSPosition = Input.location.lastData;
		curLat = curGPSPosition.latitude;
		curLng = curGPSPosition.longitude;

	}

	//===============================================coroutine : wait for www
	private IEnumerator waitForResponse(WWW www){
		print ("코루틴시작");
		yield return www; // return only when www is completely downloaded

		companies = www.text.Split ('#');

		foreach (string company in companies) {
			if (company.Equals (""))
				continue;
			//print (company + "\n");
			string[] companyInfoArr = company.Split ('%');
			float comLat = float.Parse(companyInfoArr [4]);
			float comLng = float.Parse(companyInfoArr [5]);

			//curLat와의 차이만큼 position x 더해줌
			float latDiff = -(comLat - curLat)*92; //lat
			float lngDiff = (comLng - curLng)*114; //lng

			//position을 더해주고 instantiate해준다. //이제 빌딩이 완성되었다.
			GameObject tempBuilding =  (GameObject)Instantiate(
				buildingPrefab,
				new Vector3(latDiff, 0, lngDiff)*RADIUS,
				Quaternion.identity
			);
			tempBuilding.transform.parent = buildingContainer.transform;
			//tempBuilding.GetComponentInChildren<TextMesh> ().text = companyInfoArr [0];
			tempLogoArr =  tempBuilding.GetComponentsInChildren<TextMesh> ();
			foreach (TextMesh tm in tempLogoArr) {
				tm.text = companyInfoArr [1]; //comName
			}
			//StartCoroutine (makeLogo(tempBuilding,companyInfoArr[2]));
		}

		if (isRotated == false) {
			buildingContainer.transform.Rotate (new Vector3 (0, 90, 0));
			isRotated = true;
		}
			

	}
}
