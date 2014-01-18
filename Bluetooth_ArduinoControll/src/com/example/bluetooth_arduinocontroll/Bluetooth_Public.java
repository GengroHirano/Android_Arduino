/**
 * BluetoothAdapterや共用で管理したいオブジェクトを定義しておくクラス
 * 今はBluetoothAdapterのみとなっているが後々色々と追加されるかもしれない
 **/

package com.example.bluetooth_arduinocontroll;

import java.io.IOException;
import java.util.UUID;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class Bluetooth_Public extends Application {
	
	private BluetoothAdapter bt_Adapter ;
	private BluetoothSocket mSocket ;
	//Bluetoothでシリアル通信するときに必要なプロファイル
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") ;
	//接続先Device名
	public static final String DVICE_NAME = "FireFly-9471";
	
	//ハード識別変数
	private String deviceCategory ;
	
	@Override
	public void onCreate() {
		super.onCreate();
		//BluetoothAdapterを取得
		bt_Adapter = BluetoothAdapter.getDefaultAdapter() ;
	}
	
	//BluetoothSocketを返す
	public BluetoothSocket getBluetoothSocket(BluetoothDevice mDevice) {
		if(mSocket != null){
			return mSocket ;
		}
		else {
			try {
				mSocket = mDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID) ;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return mSocket ;
		}
	}
	
	//BluetoothAdapterを返す
	public BluetoothAdapter getBluetoothAdapter() {
		return bt_Adapter ;
	}
	
	//シリアル通信に必要なプロファイルのUUIDを返す
	public UUID getSerial_UUID() {
		return MY_UUID ;
	}

	//deviceのカテゴリーのゲッター
	public String getDeviceCategory() {
		return deviceCategory;
	}

	//deviceのカテゴリーのセッター
	public void setDeviceCategory(String deviceCategory) {
		this.deviceCategory = deviceCategory;
	}

	
	
}
