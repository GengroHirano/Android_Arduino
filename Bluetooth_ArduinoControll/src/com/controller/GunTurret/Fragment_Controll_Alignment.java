package com.controller.GunTurret;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bluetooth_arduinocontroll.R;

public class Fragment_Controll_Alignment extends Fragment {

	TextView outSideTextView ;
	TextView outLengthTextView ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_alignment, container, false) ;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View graundView = getActivity().findViewById(R.id.ground) ;
		View crosshairView = getActivity().findViewById(R.id.Crosshair) ;
		outLengthTextView = (TextView)getActivity().findViewById(R.id.lengthValue) ;
		outSideTextView = (TextView)getActivity().findViewById(R.id.sideValue) ;
		GunTurret_ControllTouchEvent touchEvent = new GunTurret_ControllTouchEvent(crosshairView, outSideTextView, outLengthTextView) ;
		graundView.setOnTouchListener(touchEvent) ;
	}
	
}
