package com.knowledge_seek.phyctogram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.knowledge_seek.phyctogram.domain.Wifi;
import com.knowledge_seek.phyctogram.listAdapter.WifiListAdapter;
import com.knowledge_seek.phyctogram.util.EqAsyncTask;

import java.util.ArrayList;
import java.util.List;


public class WifiPopUpActivity extends Activity{
	private WifiManager wm;
	private ScanResult scanResult;
	private List apList;
	private List<Wifi> wifiList = new ArrayList<>();
	private ListView lv_wifilist;
	private WifiListAdapter wifiListAdapter;
	private String ipAddress;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.knowledge_seek.phyctogram.R.layout.popup_wifi_list);
		searchStartWifi();
	}
	@Override
	protected void onResume() {

		super.onResume();

		ipAddress = getIntent().getStringExtra("ipAddress"); //기기의 ip address 현재 필요 없음
	}

	//wifi 초기화 및 검색 start
	public void searchStartWifi(){
		//WifiManager 초기화
		wm = (WifiManager) getSystemService(WIFI_SERVICE);

		boolean checkWifi = wm.isWifiEnabled();

		Log.d("-진우-", "checkWifi : "+checkWifi);
		if(!checkWifi){
			wm.setWifiEnabled(true);
		}

		//검색 하기
		wm.startScan();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(wifiReceiver, filter);
	}

	//wifi 검색 완료 receiver
	private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				searchWifi();
			}
		}
	};

	//wifi List view 셋팅
	public void searchWifi() {
		unregisterReceiver(wifiReceiver);    //리시버 해제
		apList = wm.getScanResults();
		if (wm.getScanResults() != null) {
			int size = apList.size();
			for (int i = 0; i < size; i++) {
				scanResult = (ScanResult) apList.get(i);
				wifiList.add(new Wifi(scanResult.SSID,scanResult.capabilities));
			}
		}

		lv_wifilist = (ListView)findViewById(com.knowledge_seek.phyctogram.R.id.list_wifi);
		wifiListAdapter = new WifiListAdapter(this, wifiList, com.knowledge_seek.phyctogram.R.layout.list_wifi);
		lv_wifilist.setAdapter(wifiListAdapter);

		//wifi 목록 클릭
		lv_wifilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Wifi wifi = (Wifi) wifiListAdapter.getItem(position); //선택된 wifi 정보 가져옴
				String capabilities = wifi.getCapabilities(); //보안방식을 가져옴
				//wifi에 암호가 있다면
				if (capabilities.contains("WEP") || capabilities.contains("WPA") || capabilities.contains("WPA2") || capabilities.contains("OPEN")) {
					PopUpActivity.activity = null; //첫번째 wifi 선택창과 구분
					PopUpActivity.wifiPopUpActivity = WifiPopUpActivity.this; //WifiPopUpActivity의 주소를 넘겨서 WifiPopUpActivity의 메소드를 사용가능하게 함
					Intent i = new Intent(getApplicationContext(), PopUpActivity.class); //set PopUpActivity
					i.putExtra("ipAddress", ipAddress); //set ipAddress
					i.putExtra("ssid", wifi.getSsid()); //set ssid
					i.putExtra("capabilities", capabilities); //set 보안 방식
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i); //비밀번호 입력 팝업 생성
				//wifi에 암호가 없다면	바로 기기로 메시지를 보냄.
				} else {
					SendMessageThread sendMessageThread = new SendMessageThread(true, 0, ipAddress, wifi.getSsid(), capabilities);
					sendMessageThread.start();
				}
			}
		});
	}

	//기기에 메세지를 보내고 보낸 후 입력한 wifi로 다시 연결
	//기기에 데이터 전송 쓰레드 (기기에서 데이터를 수신하는대 시간이 걸려서 쓰레드 사용)
	class SendMessageThread extends Thread {
		private ProgressDialog dialog = new ProgressDialog(WifiPopUpActivity.this);
		private boolean isPlay = false;
		private int i;
		private String ipAddress;
		private String ssid;
		private String capabilities;

		public SendMessageThread(boolean isPlay, int i, String ipAddress, String ssid, String capabilities) {
			this.i = i;
			this.isPlay = isPlay;
			this.ipAddress = ipAddress;
			this.ssid = ssid;
			this.capabilities = capabilities;
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage(getString(com.knowledge_seek.phyctogram.R.string.commonActivity_wait));
			dialog.show();
		}

		public void stopThread() {
			dialog.dismiss();
			isPlay = !isPlay;
		}

		@Override
		public void run() {
			super.run();
			while (isPlay) {
				if(i==0){
					new EqAsyncTask().execute("192.168.4.1:80", "SSID", ssid+"**"); //wifi ssid 전송
				}else if(i==1){
					new EqAsyncTask().execute("192.168.4.1:80", "PW", "**"); //wifi pw 전송
				}else {
					connectWifi(ssid, "", capabilities); //기기 연결을 끊고 선택한 wifi로 접속
				}
				if (i==2){
					stopThread();
				}else {
					i++;
				}
				try {
					Thread.sleep(1000*5); //기기에서 수신처리 속도때문에 5초 간격으로 설정
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean connectWifi(String ssid, String password, String capabilities) {
		Log.d("-진우-", "ssid: " + ssid+",password: "+password+",capablities: "+capabilities);
		WifiConfiguration wfc = new WifiConfiguration();

		wfc.SSID = "\"".concat( ssid ).concat("\""); //보안 방식 별 공통 사항 set ssid
		wfc.status = WifiConfiguration.Status.DISABLED; //보안 방식 별 공통 사항 set status
		wfc.priority = 40; ////보안 방식 별 공통 사항 set priority

		if(capabilities.contains("WEP") ){
			Log.d("-진우-", "WEP 셋팅");
			wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			wfc.wepKeys[0] = "\"".concat(password).concat("\"");
			wfc.wepTxKeyIndex = 0;
		}else if(capabilities.contains("WPA") ) {
			Log.d("-진우-", "WPA 셋팅");
			wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wfc.preSharedKey = "\"".concat(password).concat("\"");
			Log.d("-진우-", "패스워드 확인 : " + wfc.preSharedKey);
		}else if(capabilities.contains("WPA2") ) {
			Log.d("-진우-", "WPA2 셋팅");
			wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wfc.preSharedKey = "\"".concat(password).concat("\"");
		}else if(capabilities.contains("OPEN") ) {
			Log.d("-진우-", "OPEN 셋팅");
			wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			wfc.allowedAuthAlgorithms.clear();
			wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		}

		int networkId = -1; //-1 연결 정보 없음
		List<WifiConfiguration> networks = wm.getConfiguredNetworks();
		Log.d("-진우-", "networks : " + networks.toString());

		for(int i=0; i<networks.size(); i++){
			Log.d("-진우-", "networks.get(i).SSID : " + networks.get(i).SSID);
			if(networks.get(i).SSID.equals("\"".concat( ssid ).concat("\""))){
				Log.d("-진우-", "networks.get(i).networkId : " + networks.get(i).networkId);
				networkId = networks.get(i).networkId; //-1을 연결 정보가 있다면 해당 id로 변경
			}
		}

		//연결 정보가 없다면 네트워크를 추가하고 id를 받음
		if(networkId == -1) {
			networkId = wm.addNetwork(wfc);
		}
		Log.d("-진우-", "networkId : " + networkId);

		//연결 여부 : false
		boolean connection = false;

		//해당 부분은 쓰레드로 동작하며, Toast도 쓰레드로 돌아가기 때문에 Toast 사용을 할 수 없음 (이중 쓰레드 안됨)

		if(networkId != -1){
			//Toast.makeText(getApplicationContext(), R.string.wifiPopUpActivity_settingHW, Toast.LENGTH_SHORT).show();
			//해당 networkId로 wifi를 연결함
			connection = wm.enableNetwork(networkId, true); //연결이 되면 true를 반환
			Log.d("-진우-", "connection : "+connection);
		}
		/*else{
			//Toast.makeText(getApplicationContext(), R.string.wifiPopUpActivity_reSettingHWPW, Toast.LENGTH_SHORT).show();
		}*/

		//연결이 되었다면
		if(connection) {
			//wifi정보를 저장
			wm.setWifiEnabled(true);
			//Toast.makeText(getApplicationContext(), R.string.wifiPopUpActivity_successSettingHW, Toast.LENGTH_SHORT).show();
		}
		/*else{
			//Toast.makeText(getApplicationContext(), R.string.wifiPopUpActivity_reSettingHWConnection, Toast.LENGTH_SHORT).show();
		}*/

		finish();

		return true;
	}
}
