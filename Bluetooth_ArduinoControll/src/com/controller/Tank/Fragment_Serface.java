package com.controller.Tank;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bluetooth_arduinocontroll.R;

public class Fragment_Serface extends Fragment{
	
	private Button button1 ;
	private Button button2 ;
	private Button button3 ;
	private Button button4 ;
	private Button button5 ;
	private Button button6 ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_controll, container, false) ;
		button1 = (Button)v.findViewById(R.id.button1) ;
		button2 = (Button)v.findViewById(R.id.button2) ;
		button3 = (Button)v.findViewById(R.id.button3) ;
		button4 = (Button)v.findViewById(R.id.button4) ;
		button5 = (Button)v.findViewById(R.id.button5) ;
		button6 = (Button)v.findViewById(R.id.button6) ;
		ClickEvent clickEvent = new ClickEvent() ;
		button1.setOnClickListener(clickEvent) ;
		button2.setOnClickListener(clickEvent) ;
		button3.setOnClickListener(clickEvent) ;
		button4.setOnClickListener(clickEvent) ;
		button5.setOnClickListener(clickEvent) ;
		button6.setOnClickListener(clickEvent) ;
		return v ;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	
	private class ClickEvent implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch ( v.getId() ) {
			case R.id.button1:
				TankActivity.command[0] = 0x3f ;
				break;

			case R.id.button2:
				TankActivity.command[0] = 0x30 ;
				break ;

			case R.id.button3:
				TankActivity.command[0] = 0x2f ;
				break ;
			
			case R.id.button4:
				TankActivity.command[1] = 0x1f ;
				break ;
				
			case R.id.button5:
				TankActivity.command[1] = 0x10 ;
				break ;
				
			case R.id.button6:
				TankActivity.command[1] = 0x0f ;
				break ;
				
			default:
				break;
			}
		}
	}
}
