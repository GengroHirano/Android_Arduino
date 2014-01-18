package com.controller.Tank;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class Tank_ControllTouchEvent implements OnTouchListener {

	//右への指令フラグ
	public static final int RIGHT_FLAG = 0 ;
	//左への指令フラグ
	public static final int LEFT_FLAG = 1 ;

	//右か左を判別するコマンド
	private static final int RIGHT_OUT = 2 ;
	private static final int LEFT_OUT = 0 ;
	
	//前か後ろを判別するコマンド
	private static final int FRONT_OUT = 1 ; 
	private static final int BACK_OUT = 0 ;
	
	//送信する添字を管理するコマンド
	private static final int RIGHT = 0 ;
	private static final int LEFT = 1 ;
	
	//コマンド格納用の変数群
	//出力するモータを定める変数
	private int command ;
	//出力方向を定める変数
	private int command2 ;
	//実際に送信するスレッドが持っている配列の添字を管理するメソッド
	private int subScript ;
	//出力値を定める変数
	private String outValueHex ;
	
	//座標の初期値
	private float defaultRightX ;
	private float defaultRightY ;

	private TextView outTextView ;

	//移動させたいビュー
	private View movingView ;
	//移動させるビューの初期中心座標
	private float defaultViewCenter ;
	//移動させるビューの最大中心座標(下方向になるので注意!)
	private float maximumViewCenter ;
	//移動させるビューの中心座標(上方向の最小中心座標なので更に注意！)
	private float viewCenter ;

	public Tank_ControllTouchEvent(View _movingView, TextView _outTextView, int flag) {
		movingView = _movingView ;
		outTextView = _outTextView ;
		switch (flag) {
		case RIGHT_FLAG:
			command = RIGHT_OUT ;
			subScript = RIGHT ;
			break;

		case LEFT_FLAG:
			command = LEFT_OUT ;
			subScript = LEFT ;
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View selfView, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			defaultRightX = movingView.getX() ;
			defaultRightY = movingView.getY() ;
			//動かすビューの中心Y座標を求める(最小中心座標でもある)
			viewCenter =  movingView.getHeight() / 2f ;
			//動かすビューの最大中心Y座標を求める
			maximumViewCenter = selfView.getHeight() - movingView.getHeight() / 2f ;
			//動かすビューの初期中心座標を求める
			defaultViewCenter = defaultRightY + viewCenter ;
			break;


		case MotionEvent.ACTION_MOVE:
			float y ;
			y = event.getY() - movingView.getHeight() / 2 ;
			//はみ出して移動させるのを防止する
			if( y < 0.0 ){
				y = (float)0 ;
			}
			if( y + movingView.getHeight() > selfView.getHeight() ){
				y = selfView.getHeight() - movingView.getHeight() ;
			}
			movingView.setY(y) ;

			//中心より上方向だったら
			if( defaultViewCenter > (movingView.getY() + viewCenter ) ){
				float div15 = ((defaultViewCenter - viewCenter) / 15f) ;
				outTextView.setText(String.format("%03.1f", ((defaultViewCenter - (movingView.getY() + viewCenter)) / div15))) ;
				//出力数値を格納
				outValueHex = Integer.toHexString((int)((defaultViewCenter - (movingView.getY() + viewCenter)) / div15)) ;
				//前方に動かす
				command2 = FRONT_OUT ;
				movingView.setBackgroundColor(Color.RED) ;
			}
			//中心より下方向だったら
			else if( defaultViewCenter < (movingView.getY() + viewCenter ) ){
				//255分割(17分割にする必要があるかも)
				float div15 = (((maximumViewCenter - defaultViewCenter) / 15f)) ;
				outTextView.setText(String.format("%03.1f", Math.abs((maximumViewCenter - (movingView.getY() + viewCenter)) / div15 - 15f))) ;
				//出力数値を格納
				outValueHex = Integer.toHexString((int)Math.abs((maximumViewCenter - (movingView.getY() + viewCenter)) / div15 - 15f)) ;
				//後方に動かす
				command2 = BACK_OUT ;
				movingView.setBackgroundColor(Color.BLUE) ;
			}
			
			//数字にする
			TankActivity.command[subScript] = Integer.valueOf(Integer.decode("0x" + Integer.toHexString(command + command2) + outValueHex));
//			Log.v("outHex", Integer.toString(Integer.valueOf(Integer.decode("0x" + Integer.toHexString(command + command2) + outValueHex)))) ;
//			Log.v("outHex", Integer.toHexString(command + command2) + outValueHex) ;
			break;

		case MotionEvent.ACTION_UP:
			movingView.setX(defaultRightX) ;
			movingView.setY(defaultRightY) ;
			movingView.setBackgroundColor(Color.BLACK) ;
			outTextView.setText(String.format("%03d", (int)(movingView.getY() + movingView.getHeight() / 2))) ;
			//出力無しに
//			Log.v("value", "0x" + Integer.toHexString(command + command2) + "0") ;
			TankActivity.command[subScript] = Integer.valueOf(Integer.decode("0x" + Integer.toHexString(command + command2) + "0"));
			break ;

		default:
			break;
		}
		return true;
	}

}
