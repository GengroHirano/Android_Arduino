package com.controller.GunTurret;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bluetooth_arduinocontroll.R;

public class Fragment_Controll_Trigger extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_trigger, container, false) ;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View graundView = getActivity().findViewById(R.id.trigger) ;
		graundView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Toast.makeText(getActivity(), "発射", Toast.LENGTH_SHORT).show() ;
					break;

				default:
					break;
				}
				return true;
			}
		}) ;
	}
	
}
