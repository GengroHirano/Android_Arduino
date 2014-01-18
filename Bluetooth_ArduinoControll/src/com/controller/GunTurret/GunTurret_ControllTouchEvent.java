package com.controller.GunTurret;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class GunTurret_ControllTouchEvent implements OnTouchListener{

	//上下か左右を判別するコマンド
	private static final int SIDE_OUT = 2 ;
	private static final int LENGTH_OUT = 0 ;

	//正転か逆転かを判別するコマンド
	private static final int FRONT_OUT = 1 ; 
	private static final int BACK_OUT = 0 ;

	//送信する添字を管理するコマンド
	private static final int SIDE = 0 ;
	private static final int LENGTH = 1 ;

	//コマンド格納用の変数群
	//出力方向を定める変数
	private int commandSide ;
	private int commandLength ;
	//出力値を定める変数
	private String outValueHexX ;
	private String outValueHexY ;

	//座標の初期値
	private float defaultRightX ;
	private float defaultRightY ;

	private TextView outSideTextView ;
	private TextView outLengthTextView ;

	//移動させたいビュー
	private View movingView ;
	//移動させるビューの初期中心座標
	private float defaultViewCenterX ;
	private float defaultViewCenterY ;
	//移動させるビューの最大中心座標(下方向になるので注意!)
	private float maximumViewCenterX ;
	private float maximumViewCenterY ;
	//移動させるビューの中心座標(上方向の最小中心座標なので更に注意！)
	private float viewCenterX ;
	private float viewCenterY ;

	public GunTurret_ControllTouchEvent(View _movingView, TextView _outSideTextView, TextView _outLengthTextView) {
		movingView = _movingView ;
		outSideTextView = _outSideTextView ;
		outLengthTextView = _outLengthTextView ;
	}

	@Override
	public boolean onTouch(View selfView, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			defaultRightX = movingView.getX() ;
			defaultRightY = movingView.getY() ;
			//動かすビューの中心XY座標を求める(最小中心座標でもある)
			viewCenterX = movingView.getWidth() / 2f ;
			viewCenterY =  movingView.getHeight() / 2f ;
			//動かすビューの最大中心Y座標を求める
			maximumViewCenterX = selfView.getWidth() - movingView.getWidth() / 2f ;
			maximumViewCenterY = selfView.getHeight() - movingView.getHeight() / 2f ;
			//動かすビューの初期中心座標を求める
			defaultViewCenterX = defaultRightX + viewCenterX ;
			defaultViewCenterY = defaultRightY + viewCenterY ;
			break;


		case MotionEvent.ACTION_MOVE:
			float x ;
			float y ;
			x = event.getX() - viewCenterX ;
			y = event.getY() - viewCenterY ;
			//はみ出して移動させるのを防止する
			if( y < 0.0 ){
				y = (float)0 ;
			}
			if( y + movingView.getHeight() > selfView.getHeight() ){
				y = selfView.getHeight() - movingView.getHeight() ;
			}
			if( x < 0.0 ){
				x = (float)0 ;
			}
			if( x + movingView.getWidth() > selfView.getWidth() ){
				x = selfView.getWidth() - movingView.getWidth() ;
			}
			movingView.setY(y) ;
			movingView.setX(x) ;
			//中心より左だったら
			if( defaultViewCenterX > (movingView.getX() + viewCenterX) ){
				float div15 = ((defaultViewCenterX - viewCenterX) / 15f) ;
				outSideTextView.setText(String.format("%03.1f", ((defaultViewCenterX - (movingView.getX() + viewCenterX)) / div15))) ;
				//出力数値を格納
				outValueHexX = Integer.toHexString((int)((defaultViewCenterX - (movingView.getX() + viewCenterX)) / div15)) ;
				//正転に動かす
				commandSide = FRONT_OUT ;
				movingView.setBackgroundColor(Color.CYAN) ;
			}
			//中心より右だったら
			else if ( defaultViewCenterX < (movingView.getX() + viewCenterX) ){
				float div15 = ((maximumViewCenterX - defaultViewCenterX) / 15f) ;
				outSideTextView.setText(String.format("%03.1f", Math.abs((maximumViewCenterX - (movingView.getX() + viewCenterX)) / div15 - 15f))) ;
				//出力数値を格納
				outValueHexX = Integer.toHexString((int)Math.abs((maximumViewCenterX - (movingView.getX() + viewCenterX)) / div15 - 15f)) ;
				//逆転に動かす
				commandSide = BACK_OUT ;
				movingView.setBackgroundColor(Color.GREEN) ;
			}
			//中心より上方向だったら
			if( defaultViewCenterY > (movingView.getY() + viewCenterY ) ){
				float div15 = ((defaultViewCenterY - viewCenterY) / 15f) ;
				outLengthTextView.setText(String.format("%03.1f", ((defaultViewCenterY - (movingView.getY() + viewCenterY)) / div15))) ;
				//出力数値を格納
				outValueHexY = Integer.toHexString((int)((defaultViewCenterY - (movingView.getY() + viewCenterY)) / div15)) ;
				//正転に動かす
				commandLength = FRONT_OUT ;
				movingView.setBackgroundColor(Color.RED) ;
			}
			//中心より下方向だったら
			else if( defaultViewCenterY < (movingView.getY() + viewCenterY ) ){
				//255分割(17分割にする必要があるかも)
				float div15 = ((maximumViewCenterY - defaultViewCenterY) / 15f) ;
				outLengthTextView.setText(String.format("%03.1f", Math.abs((maximumViewCenterY - (movingView.getY() + viewCenterY)) / div15 - 15f))) ;
				//出力数値を格納
				outValueHexY = Integer.toHexString((int)Math.abs((maximumViewCenterY - (movingView.getY() + viewCenterY)) / div15 - 15f)) ;
				//逆転に動かす
				commandLength = BACK_OUT ;
				movingView.setBackgroundColor(Color.BLUE) ;
			}
			Log.v("limit", Integer.toString(selfView.getWidth())) ;
			//数字にする
			GunTurretActivity.command[SIDE] = Integer.valueOf(Integer.decode("0x" + Integer.toHexString(SIDE_OUT + commandSide) + outValueHexX));
			GunTurretActivity.command[LENGTH] = Integer.valueOf(Integer.decode("0x" + Integer.toHexString(LENGTH_OUT + commandLength) + outValueHexY));
//							Log.v("outHexX", "0x" + Integer.toHexString(SIDE_OUT + commandSide) + outValueHexX) ;
//							Log.v("outHexY", "0x" + Integer.toHexString(LENGTH_OUT + commandLength) + outValueHexY) ;
			break;

		case MotionEvent.ACTION_UP:
			movingView.setX(defaultRightX) ;
			movingView.setY(defaultRightY) ;
			movingView.setBackgroundColor(Color.BLACK) ;
			outLengthTextView.setText(String.format("%03d", (int)(movingView.getY() + movingView.getHeight() / 2))) ;
			//出力無しに
			//				Log.v("value", "0x" + Integer.toHexString(command + command2) + "0") ;
			GunTurretActivity.command[SIDE] = Integer.valueOf(Integer.decode("0x" + Integer.toHexString(SIDE_OUT + commandSide) + "0"));
			GunTurretActivity.command[LENGTH] = Integer.valueOf(Integer.decode("0x" + Integer.toHexString(SIDE_OUT + commandLength) + "0")) ;
			break ;

		default:
			break;
		}
		return true;
	}


}
