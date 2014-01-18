/**
 * 接続履歴の有るデバイスや検出可能なデバイスを列挙するアクティビティ
 * 項目をタップすると項目のデバイスとペアリング、接続をする
 **/

package com.example.deviceList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.controller.GunTurret.GunTurretActivity;
import com.controller.Tank.TankActivity;
import com.example.bluetooth_arduinocontroll.BlueToothConnect_Read;
import com.example.bluetooth_arduinocontroll.Bluetooth_Public;
import com.example.bluetooth_arduinocontroll.R;

public class BlueToothDeviceActivity extends Activity{

	private ListView pearedList ; //過去に接続したデバイスを入れるリストビュー
	private ListView nonPearList ; //接続したことの無いデバイスを入れるリストビュー
	private TextView nonPearDeviceTitle ; //接続したことないでデバイスを入れるリストビューのタイトル
	private ProgressBar nonPearProgressBar ; //検出中に出すプログレスバー
	private ArrayList<String> deviceNameList = new ArrayList<String>() ; //検出デバイスの重複チェック用リスト

	private final int REQUEST_ENABLE_BLUETOOTH = 10;

	private ArrayAdapter<String> pearedAdapter ; //接続したことのあるデバイスの名前を詰めるアダプター
	private ArrayAdapter<String> nonPearAdapter ; //接続したことの無いデバイスの名前を詰めるアダプター 
	private BluetoothAdapter bt_Adapter ;
	private Bluetooth_Public app ;

	//スレッド挙動監視用変数
	private boolean isRunning = false;

	//仮
	private  BluetoothSocket mSocket = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list) ;

		app = (Bluetooth_Public)getApplication() ;
		bt_Adapter = app.getBluetoothAdapter() ;
		pearedList = (ListView)findViewById(R.id.pearedList) ;
		pearedList.setOnItemClickListener(new ListTouchEvent()) ;
		pearedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) ;

		if( bt_Adapter.equals(null) ){
			Toast.makeText(this, "BlueTooth未対応端末です", Toast.LENGTH_SHORT).show() ;
			finish() ;
		}

		//BlueToothが有効になっていないなら
		if( !bt_Adapter.isEnabled() ){
			Intent bluetoothON = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) ;
			startActivityForResult(bluetoothON, REQUEST_ENABLE_BLUETOOTH) ;
		}
		//有効だったら
		else{
			setDviceList() ;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		isRunning = false ;
	}

	//bluetooth有効かのダイアログから帰ってきたら
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUEST_ENABLE_BLUETOOTH ){
			//blueToothが有効にされたら
			if( resultCode == Activity.RESULT_OK ){
				Log.v("タグ", "BlueToothが有効にされました") ;
				setDviceList() ;
			}
			else{
				Toast.makeText(this, "都合が悪いようなので終了しときます", Toast.LENGTH_SHORT).show() ;
				finish() ;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bluetooth_listmenu, menu) ;
		return true;
	}

	private final BroadcastReceiver DeviceFoundReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction() ;
			String deviceName = null ;
			BluetoothDevice foundDevice ;
			nonPearList = (ListView)findViewById(R.id.nonpearedList) ;
			nonPearList.setOnItemClickListener(new ListTouchEvent()) ;
			if( BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action) ){
				Log.v(action, "スキャン開始") ;
				deviceNameList.clear() ;
				nonPearDeviceTitle.setVisibility(View.VISIBLE) ;
				nonPearProgressBar.setVisibility(View.VISIBLE) ;
			}
			//一時的なStringリストを作ってアドレスを格納、かぶったらAdapter.addをしないような処理を追加する必要あり
			//デバイスが検出された
			if( BluetoothDevice.ACTION_FOUND.equals(action) ){
				foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ;
				if( (deviceName = foundDevice.getName()) != null ){
					//接続したことのないリストにまだ載せてないデバイスか？
					if( foundDevice.getBondState() != BluetoothDevice.BOND_BONDED && !checkOverlap(deviceName)){
						nonPearAdapter.add( deviceName + "\n" + foundDevice.getAddress() ) ;
						deviceNameList.add(deviceName) ;
						Log.v(action, deviceName) ;
					}
				}
				nonPearList.setAdapter(nonPearAdapter) ;
			}

			//名前が検出された
			if( BluetoothDevice.ACTION_NAME_CHANGED.equals(action) ){
				foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ;
				if( (deviceName = foundDevice.getName()) != null ){
					//接続したことのないリストにまだ載せてないデバイスか？
					if( foundDevice.getBondState() != BluetoothDevice.BOND_BONDED && !checkOverlap(deviceName)){
						nonPearAdapter.add( deviceName + "\n" + foundDevice.getAddress() ) ;
						deviceNameList.add(deviceName) ;
					}
				}
				nonPearList.setAdapter(nonPearAdapter) ;
			}

			if( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) ){
				Log.v(action, "スキャン終了") ;
				nonPearProgressBar.setVisibility(View.INVISIBLE) ;
				unregisterReceiver(this) ;
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.search:

			IntentFilter filter = new IntentFilter() ;
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) ;
			filter.addAction(BluetoothDevice.ACTION_FOUND) ;
			filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED) ;
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) ;
			registerReceiver(DeviceFoundReceiver, filter) ;

			//			nonPearAdapter = new ArrayAdapter<String>(this, R.layout.item_row) ;
			nonPearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) ;

			if( bt_Adapter.isDiscovering() ){
				//検索中なら検索をキャンセル
				bt_Adapter.cancelDiscovery() ;
			}
			//デバイスの検索
			//一定時間検索を行う
			bt_Adapter.startDiscovery() ;

			break;

		default:
			Toast.makeText(this, "他のボタンやで", Toast.LENGTH_SHORT).show() ;
			//自デバイスの検出を有効にする
			Intent discoverableOn = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableOn.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
			startActivity(discoverableOn);
			break;
		}


		return true ;
	}

	public void setDviceList(){
		//接続履歴の有るデバイス名を取得
		Set<BluetoothDevice> devices = bt_Adapter.getBondedDevices() ;
		for( BluetoothDevice device : devices ){
			Log.i("DeviceName", device.getName()) ;
			pearedAdapter.add( device.getName() + "\n" + device.getAddress()) ;
		}
		pearedList.setAdapter(pearedAdapter) ;

		//接続したことのないデバイスを検索するときに関連するインスタンスを生成
		nonPearDeviceTitle = (TextView)findViewById(R.id.nonpear) ;
		nonPearProgressBar = (ProgressBar)findViewById(R.id.load) ;
	}

	private boolean checkOverlap(String foundDevice){
		for(String deviceName : deviceNameList){
			if(foundDevice.equals(deviceName)){
				return true ;
			}
		}
		return false ; 
	}
	
	public class ListTouchEvent implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ListView list = (ListView)parent ;
			String deviceNames = (String)list.getItemAtPosition(position) ;
			String[] deviceName = deviceNames.split("\n") ;
			Bluetooth_Public bluetooth_Public = (Bluetooth_Public)BlueToothDeviceActivity.this.getApplicationContext() ;
			//			bt_Adapter = bluetooth_Public.getBluetoothAdapter() ;

			BluetoothDevice mDevice = null;
			Set<BluetoothDevice> devices = bt_Adapter.getBondedDevices() ;
			for( BluetoothDevice device : devices ){
				Log.i("DeviceName", device.getName()) ;
				if( device.getName().equals(deviceName[0]) ){
					mDevice = device ;
				}
			}
			if(mDevice != null){
				mSocket = bluetooth_Public.getBluetoothSocket(mDevice) ;
			}
			if(bt_Adapter.isEnabled()){
				//deviceの検索中なら検索をキャンセル
				if(bt_Adapter.isDiscovering()){
					bt_Adapter.cancelDiscovery() ;
				}
				//プログレスダイアログを起動
				final ProgressDialog pDialog = new ProgressDialog(BlueToothDeviceActivity.this) ;
				pDialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						isRunning = false ;
					}
				}) ;
				pDialog.setTitle("接続中") ;
				pDialog.setMessage(deviceName[0]) ;
				pDialog.show() ;
				BlueToothConnect_Read connect_Read = new BlueToothConnect_Read(BlueToothDeviceActivity.this, mSocket);
				connect_Read.start() ;
				//スレッド処理(ものすごい見づらいので注意！)
				isRunning = true ;
				new Thread(new Runnable() {
					Bluetooth_Public bluetooth_Public = (Bluetooth_Public)getApplication() ;
					Handler mHandler = new Handler() ;
					//この処理はバックキーを押したりしてダイアログを消したりしたら終了されるようにする必要が有る
					@Override
					public void run() {
						while (isRunning) {
							if(bluetooth_Public.getDeviceCategory() != null){
								if(bluetooth_Public.getDeviceCategory().equals("T")){
									mHandler.post(new Runnable() {

										@Override
										public void run() {
											pDialog.dismiss() ;
											try {
												mSocket.close() ;
											} catch (IOException e) {
												e.printStackTrace();
											}
											bluetooth_Public.setDeviceCategory(null) ;
											Intent intent = new Intent(BlueToothDeviceActivity.this, TankActivity.class) ;
											startActivity(intent) ;
										}

									}) ;
									isRunning = false ;
									break ;
								}
								else if(bluetooth_Public.getDeviceCategory().equals("G")){
									mHandler.post(new Runnable() {

										@Override
										public void run() {
											pDialog.dismiss() ;
											try {
												mSocket.close() ;
											} catch (IOException e) {
												e.printStackTrace();
											}
											bluetooth_Public.setDeviceCategory(null) ;
											Intent intent = new Intent(BlueToothDeviceActivity.this, GunTurretActivity.class) ;
											startActivity(intent) ;
										}

									}) ;
									isRunning = false ;
									break ;
								}
								else{
									Log.v("ReadData=", bluetooth_Public.getDeviceCategory()) ;
									mHandler.post(new Runnable() {

										@Override
										public void run() {
											pDialog.dismiss() ;
										}

									}) ;
									try {
										mSocket.close() ;
									} catch (IOException e) {
										e.printStackTrace();
									}
									isRunning = false ;
									break ;
								}
							}
						}
					}
				}).start() ;
			}
			else{
				Intent bluetoothON = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) ;
				startActivityForResult(bluetoothON, REQUEST_ENABLE_BLUETOOTH + 1) ;
			}

		}

	}

}
