package com.example.bluetooth_arduinocontroll;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

public class BlueToothConnect_Read extends Thread {

	private boolean isRunning = true ;
//	private String deviceName ;
	private Context context ;
	private String readData ;
	private BluetoothSocket mSocket ;
//	private BluetoothDevice mDevice ;
//	private BluetoothAdapter bt_Adapter ;

	public BlueToothConnect_Read(Context _context, BluetoothSocket _mSocket) {
		context = _context ;
		mSocket = _mSocket ;
	}

	@Override
	public void run() {
		super.run();
		Bluetooth_Public bluetooth_Public = (Bluetooth_Public)context.getApplicationContext() ;

		InputStream mInputStream  = null ;
		try {
			//取得したデバイス名を使ってBlueToothでSoket接続
			mSocket.connect() ;
			mInputStream = mSocket.getInputStream() ;
			
			//バッファの格納配列
			byte[] buffer = new byte[1] ;
			//取得したバッファのサイズを格納
			int bytes ;

			//UI識別コマンド送信
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					OutputStream mOutputStream;
					try {
						mOutputStream = mSocket.getOutputStream();
						mOutputStream.write(0x80) ;
						Log.v("OUT", "OUTDATA") ;
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
			}).start() ;
			
			while(readData == null && isRunning){
				//読み込み処理
				bytes = mInputStream.read(buffer) ;
				//String型に変換
				readData = new String(buffer, 0, bytes) ;
			}
			Log.v("readData", readData) ;
			bluetooth_Public.setDeviceCategory(readData) ;
		} 
		catch (IOException e) {
			e.printStackTrace() ;
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
