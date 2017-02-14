using UnityEngine;
using System.Collections;
using UnityEngine.UI;
public class compassCamera : MonoBehaviour
{

	//Transform transform;
	// Use this for initialization
	//public Text txtfield;
	private Transform cameraTr;
	private float filteredCompared, filteredStandard;
	private int stdDegree;
	private int inputDegree;
	private int a, b;
	private int minDiff;


	bool isChangable (int std, int cmp)
	{
		a = Mathf.Abs (std - cmp);
		b = 360 - a;

		if (a <= b) {
			minDiff = a;
		} else {
			minDiff = b;
		}

		if (minDiff >= 7)
			return true;
		else
			return false;
	}


	//start
	void Start ()
	{
		//transform = this.GetComponent<Transform> ();
		Input.compass.enabled = true;
		Input.location.Start ();
		cameraTr = GetComponent<Transform> ();
		stdDegree = (int)Input.compass.magneticHeading; //first degree is stdDegree;
	}

	// Update is called once per frame
	void Update ()
	{
		//txtfield.text = "" + stdDegree;
		inputDegree = (int)Input.compass.magneticHeading; 

		if (isChangable (stdDegree, inputDegree)) {
			stdDegree = inputDegree;
			cameraTr.rotation = Quaternion.Euler (0, stdDegree, 0);
		}
	}

}
