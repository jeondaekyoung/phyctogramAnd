package com.knowledge_seek.phyctogram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.util.EqAsyncTask;


public class PopUpActivity extends Activity implements View.OnClickListener{

	private String ssid;
	private String capabilities;
	//private String password;

	private TextView tv_ssid;
	private EditText edit_password;

	private Button btn_close_popup;
	private Button btn_pwdOk;

	public static EquipmentActivity activity;
	public static WifiPopUpActivity wifiPopUpActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.knowledge_seek.phyctogram.R.layout.popup_wifi);
	}
	@Override
	protected void onResume() {

		super.onResume();
		btn_close_popup = (Button) findViewById(com.knowledge_seek.phyctogram.R.id.btn_close_popup);
		btn_pwdOk = (Button) findViewById(com.knowledge_seek.phyctogram.R.id.btn_pwdOk);

		btn_close_popup.setOnClickListener(this);
		btn_pwdOk.setOnClickListener(this);

		ssid = getIntent().getStringExtra("ssid");
		capabilities = getIntent().getStringExtra("capabilities");
		//ipAddress = getIntent().getStringExtra("ipAddress");

		tv_ssid = (TextView) findViewById(com.knowledge_seek.phyctogram.R.id.tv_ssid);
		edit_password = (EditText) findViewById(com.knowledge_seek.phyctogram.R.id.edit_password);
		tv_ssid.setText(ssid);
	}
	@Override
	public void onClick(View v) {

		if(v.getId() == com.knowledge_seek.phyctogram.R.id.btn_pwdOk){
			/*Intent i = new Intent(PopUpActivity.this,LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);*/
			Log.d("-대경-", "PopUpActivity ssid : " + ssid + ", capabilities : " + capabilities + ", edit_password : " + edit_password.getText());
				Intent i = new Intent();
				i.putExtra("p_ssid", ssid);
				i.putExtra("p_capabilities", capabilities);
				i.putExtra("p_password", edit_password.getText().toString());
				setResult(RESULT_OK, i);
				finish();

		}else if(v.getId() == com.knowledge_seek.phyctogram.R.id.btn_close_popup){
			finish();
		}
	}

	//기기에 메세지를 보내고 보낸 후 입력한 wifi로 다시 연결
	//기기에 데이터 전송 쓰레드 (기기에서 데이터를 수신하는대 시간이 걸려서 쓰레드 사용)
	class SendMessageThread extends Thread {
		private ProgressDialog dialog = new ProgressDialog(wifiPopUpActivity);
		private boolean isPlay = false;
		private int i;
		private String ipAddress;
		private String ssid;
		private String pw;

		public SendMessageThread(boolean isPlay, int i, String ipAddress, String ssid, String pw) {
			this.i = i;
			this.isPlay = isPlay;
			this.ipAddress = ipAddress;
			this.ssid = ssid;
			this.pw = pw;
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
					Log.d("-진우-","1");
					new EqAsyncTask().execute("192.168.4.1:80", "?SSID", ssid+"**"); //wifi ssid 전송
				}else if(i==1){
					Log.d("-진우-","2");
					new EqAsyncTask().execute("192.168.4.1:80", "?PW", pw+"**"); //wifi pw 전송
				}else{
					Log.d("-진우-","3");
					wifiPopUpActivity.connectWifi(ssid, pw, capabilities); //기기 연결을 끊고 선택한 wifi로 접속
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
}
