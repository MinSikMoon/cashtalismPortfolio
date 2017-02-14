using UnityEngine;
using System.Collections;

public class loginPopUp : MonoBehaviour
{
	//fields
	public GameObject loginPanel;
	//private bool isPop = false;
	//popup
	public void popUp ()
	{
		loginPanel.SetActive (true);
	}
	//popdown
	public void popDown ()
	{
		loginPanel.SetActive (false);
	}

}
