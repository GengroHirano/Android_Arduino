/**
 * Bluetoothの有効化をするアクティビティ
 * ここから接続履歴の有るデバイスや検出可能なデバイスを列挙する画面に遷移する
 **/

package com.controller.Tank;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

import com.example.bluetooth_arduinocontroll.Bluetooth_Public;
import com.example.bluetooth_arduinocontroll.R;

public class TankActivity extends Activity implements Runnable {

	static Integer[] command = new Integer[2] ;

	private Bluetooth_Public app ;
	
	private BluetoothAdapter bt_Adapter ;
	private BluetoothDevice mDevice ;

	private BluetoothSocket mSocket ;
	private Thread mThread ;
	private Handler mHandler ;
	private Boolean isRunning ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tank_controll);
		command[0] = 0x30 ;
		command[1] = 0x10 ;

		//このApplicationインスタンスでBluetoothAdapterの管理をする
		app = (Bluetooth_Public)getApplication() ;

		//-------------エミュレータでデバッグする際はコメントアウト忘れずにね！---------//
		bt_Adapter = app.getBluetoothAdapter() ;

		//接続履歴の有るデバイス名を取得
		Set<BluetoothDevice> devices = bt_Adapter.getBondedDevices() ;
		for( BluetoothDevice device : devices ){
			Log.i("DeviceName", device.getName()) ;
			//ここのデバイス名のところを修正する必要あり
			if( device.getName().equals(Bluetooth_Public.DVICE_NAME) ){
				mDevice = device ;
			}
		}
		//			スレッドを起動しBluetooth接続
		mThread = new Thread(this) ;
		mHandler = new Handler() ;
		isRunning = true ;
		mThread.start() ;
		//------------------------------------------------------------------//
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		isRunning = false ;
		try {
			mSocket.close() ;
		} catch (Exception e) {
			e.printStackTrace() ;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if( keyCode == KeyEvent.KEYCODE_BACK){
			isRunning = false ;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void run() {
		OutputStream mOutputStream = null ;
		int rightBeforeOut = 0x3f ;
		int leftBeforeOut = 0x1f ;
		try {
			//取得したデバイス名を使ってBlueToothでSoket接続
			mSocket = mDevice.createRfcommSocketToServiceRecord(app.getSerial_UUID()) ;
			mSocket.connect() ;
			mOutputStream = mSocket.getOutputStream() ;

			while(isRunning){
				if( command[0] != null && rightBeforeOut != command[0]){
					mOutputStream.write( command[0] ) ;
					//					Log.v( "value", Integer.toString(command[0]) ) ;
					if( (command[0] & 0x0f) != 0 ){
						rightBeforeOut = command[0] ;
					}
					command[0] = null ;
				}
				if( command[1] != null && leftBeforeOut != command[1]){
					mOutputStream.write( command[1] ) ;
					//					Log.v( "value", Integer.toString(command[1]) ) ;
					if( (command[1] & 0x0f) != 0 ){
						leftBeforeOut = command[1] ;
					}
					command[1] = null ;
				}
			}
		} 
		catch (IOException e) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(TankActivity.this, "接続失敗!", Toast.LENGTH_SHORT).show() ;
				}
			}) ;
			try {
				mSocket.close() ;
			} 
			catch (Exception e2) {}
			isRunning = false ;
		}
		catch (Exception e) {
			e.printStackTrace() ;
			try {
				mSocket.close() ;
			} 
			catch (Exception e2) {}
			isRunning = false ;
		}
	}

}
