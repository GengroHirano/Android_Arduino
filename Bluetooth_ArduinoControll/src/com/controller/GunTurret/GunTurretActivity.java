package com.controller.GunTurret;

import com.example.bluetooth_arduinocontroll.R;

import android.app.Activity;
import android.os.Bundle;

public class GunTurretActivity extends Activity {

	static Integer[] command  = new Integer[2];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gunturret) ;
	}
	
}
