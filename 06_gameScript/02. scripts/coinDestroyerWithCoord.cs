using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class coinDestroyerWithCoord : MonoBehaviour {
	//PROPERTIES
	private Text coinCoord;

	//destroy coin
	void OnTriggerEnter(Collider other){
		if (other.gameObject.tag == "Coin")
			
			Destroy (other.gameObject);
	}
}
