using UnityEngine;
using System.Collections;

public class slideMenu : MonoBehaviour {
	//PROPERTIES
	public GameObject slidePanel;
	private Animator animator;
	public GameObject building;
	public GameObject btnMenu;
	public GameObject panelDescript;

	private bool flag;

	// Use this for initialization
	void Start () {
		animator = slidePanel.GetComponent<Animator> ();
		//animator.enabled = false;
		flag = false;
		animator.SetBool ("isIn",flag);
		panelDescript.SetActive (false);
	}

	// cancelclicked
	public void cancelClicked(){
		animator.SetBool ("isIn", false);
		btnMenu.SetActive (true);
		building.SetActive (true);
		panelDescript.SetActive (false);
	}
	// Update is called once per frame
	public void menuClicked () {
		animator.SetBool ("isIn", true);
		btnMenu.SetActive (false);
		building.SetActive (false);
		panelDescript.SetActive (true);

//		if (flag == false) {
//			flag = true;
//			building.SetActive (false);
//		} else {
//			flag = false;
//			building.SetActive (true);
//		
//		}
//		animator.SetBool ("isIn", flag);
	}
}
