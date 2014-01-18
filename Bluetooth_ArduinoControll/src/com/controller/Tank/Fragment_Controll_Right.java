package com.controller.Tank;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bluetooth_arduinocontroll.R;
public class Fragment_Controll_Right extends Fragment {

	//移動座標表示用テキストビュー※後に削除orHide予定
	TextView outTextView ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_controller_right, container, false) ;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//一番上になるビュー※こいつがタッチを検出する
		View rightGroundView = getActivity().findViewById(R.id.rightGround) ;
		//コントローラのレバーのビュー
		View rightLever = getActivity().findViewById(R.id.rightLever) ;
		//出力表示ようのテキストビュー
		outTextView = (TextView)getActivity().findViewById(R.id.rightOut) ;
		//引数には動かしたいビューを渡す
		Tank_ControllTouchEvent touchEvent = new Tank_ControllTouchEvent(rightLever, outTextView, Tank_ControllTouchEvent.RIGHT_FLAG) ;
		rightGroundView.setOnTouchListener(touchEvent) ;
	}

}
