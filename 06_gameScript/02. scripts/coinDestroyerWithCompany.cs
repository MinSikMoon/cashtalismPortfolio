using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class coinDestroyerWithCompany : MonoBehaviour {
	//PROPERTIES
	public Text coinText;
	//company properties
	private string comCode;
	private string comName;


	//destroy coin
	void OnTriggerEnter(Collider other){
		if (other.gameObject.tag == "Coin")

			//맞으면 getStock(comCode) 하자.
			// 

			coinText.text = "맞았당!";
			Destroy (other.gameObject);
	}
}
