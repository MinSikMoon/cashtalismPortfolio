using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class menuPickCtrl : MonoBehaviour
{
	// PROPERTIES
	public Text explainText;
	public GameObject btn_close;
	private int screenWidth, screenHeight;
	float bottomMargin;
	public GameObject mapCanvas;
	private WebViewObject wvObj;
	private WebViewObject[] wvArr; 
	public GameObject wvContainer;
	private string koreaMapUrl = UserSession.rootUrl + "/htmlpages/finalOverlayCluster.html";
	private string comInfoMapUrl = UserSession.rootUrl + "/distanceMapMaker/distanceUpDownComInfoMaker.jsp?";
	private float curLat, curLng;
	private LocationInfo curGPSPosition;
	// START => 화면의 너비,높이를 구한다./
	void Start ()
	{
		screenWidth = Screen.width;
		screenHeight = Screen.height;
		bottomMargin = screenHeight / 10 * 1.5f;

		wvArr = new WebViewObject[1];
		wvArr [0] = null;

		btn_close.GetComponent<RectTransform> ().sizeDelta = new Vector2 (screenWidth, bottomMargin);
		btn_close.GetComponent<RectTransform> ().position = new Vector2 (0, 0);


	}

	//close clicked
	public void closeClicked ()
	{
		wvArr[0].SetVisibility (false);

		//Destroy (wvArr [0]);
		//Destroy(wvContainer);
		foreach(Transform child in wvContainer.transform){
			Destroy (child.gameObject);
		}
		mapCanvas.SetActive (false);
	}

	//1. korea map clicked
	public void koreaMapClicked ()
	{
		mapCanvas.SetActive (true);
		explainText.text = "전국상장기업지도";

		//웹을 띄워준다.

		Destroy (wvArr [0]);


		wvArr[0] = (new GameObject ("wvObj")).AddComponent<WebViewObject> ();
		wvArr [0].transform.parent = wvContainer.transform;
		//wvArr [0] = wvObj;
		//wvObj.transform.parent = webviewContainer.transform;
		wvArr[0].Init ((msg) => {
			Debug.Log (string.Format ("CallFromJs[{0}]", msg));
		});

		wvArr[0].LoadURL (koreaMapUrl);
		wvArr[0].SetVisibility (true);
		wvArr[0].SetMargins (0, 0, 0, (int)bottomMargin); //webVIEW is on screen/ now let's make button size
		btn_close.GetComponent<RectTransform> ().sizeDelta = new Vector2 (screenWidth, bottomMargin);
		btn_close.GetComponent<RectTransform> ().position = new Vector2 (0, 0);

	}

	//2. 3d map clicked
	public void threeDimensionMapClicked(){
		explainText.text = "내 주변 기업보기";
		SceneManager.LoadScene("03_coordTest"); 
	}

	//3. throwing game clicked
	public void throwingGameClicked(){
		explainText.text = "기업잡기";
		SceneManager.LoadScene("04_throwing"); 
	}

	void GetGPSData ()
	{
		curGPSPosition = Input.location.lastData;
		curLat = curGPSPosition.latitude;
		curLng = curGPSPosition.longitude;

	}

	//4. 2d distance map clicked
	public void distanceUpDownComInfoClicked(){
		mapCanvas.SetActive (true);
		explainText.text = "전국상장기업지도";

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
		//웹을 띄워준다.

		Destroy (wvArr [0]);


		wvArr[0] = (new GameObject ("wvObj")).AddComponent<WebViewObject> ();
		wvArr [0].transform.parent = wvContainer.transform;

		wvArr[0].Init ((msg) => {
			Debug.Log (string.Format ("CallFromJs[{0}]", msg));
		});

		wvArr[0].LoadURL (comInfoMapUrl+"curLat="+curLat+"&curLng="+curLng+"&searchDist="+UserSession.searchDist);
		wvArr[0].SetVisibility (true);
		wvArr[0].SetMargins (0, 0, 0, (int)bottomMargin); //webVIEW is on screen/ now let's make button size
		btn_close.GetComponent<RectTransform> ().sizeDelta = new Vector2 (screenWidth, bottomMargin);
		btn_close.GetComponent<RectTransform> ().position = new Vector2 (0, 0);

	}

	

}
