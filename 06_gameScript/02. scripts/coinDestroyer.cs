using UnityEngine;
using System.Collections;

public class coinDestroyer : MonoBehaviour {

	//destroy coin
	void OnTriggerEnter(Collider other){
		if (other.gameObject.tag == "Coin")
			Destroy (other.gameObject);
	}
}
