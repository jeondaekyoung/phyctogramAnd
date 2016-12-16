package com.knowledge_seek.phyctogram;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class PopUpActivity extends Activity implements View.OnClickListener{

	private String ssid;
	private String capabilities;

	private TextView tv_ssid;
	private EditText edit_password;

	private Button btn_close_popup;
	private Button btn_pwdOk;

	public static EquipmentActivity activity;
	public static WifiPopUpActivity wifiPopUpActivity;

    private WifiManager wm;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_wifi);
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
	}
	@Override
	protected void onResume() {

		super.onResume();
		btn_close_popup = (Button) findViewById(R.id.btn_close_popup);
		btn_pwdOk = (Button) findViewById(R.id.btn_pwdOk);

		btn_close_popup.setOnClickListener(this);
		btn_pwdOk.setOnClickListener(this);

		ssid = getIntent().getStringExtra("ssid");
		capabilities = getIntent().getStringExtra("capabilities");
		//ipAddress = getIntent().getStringExtra("ipAddress");

		tv_ssid = (TextView) findViewById(R.id.tv_ssid);
		edit_password = (EditText) findViewById(R.id.edit_password);
		tv_ssid.setText(ssid);
	}
	@Override
	public void onClick(View v) {

		if(v.getId() == R.id.btn_pwdOk){
			/*Intent i = new Intent(PopUpActivity.this,LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);*/
			Log.d("-대경-", "PopUpActivity ssid : " + ssid + ", capabilities : " + capabilities + ", edit_password : " + edit_password.getText());

            WifiConfiguration wfc = EquipmentActivity.getWifiConfiguration(ssid, edit_password.getText().toString(), capabilities);

            Log.d("-진우-", "wfc : " + wfc.toString());

            int networkId = -1; //-1 연결 정보 없음
            List<WifiConfiguration> networks = wm.getConfiguredNetworks();
            //   Log.d("-진우-", "networks : " + networks.toString());

            /*for (int i = 0; i < networks.size(); i++) {
                //Log.d("-진우-", "networks.get(i).SSID : " + networks.get(i).SSID);
                if (networks.get(i).SSID.equals("\"".concat(ssid).concat("\""))) {
                    Log.d("-진우-", "networks.get(i).networkId : " + networks.get(i).networkId);
                    networkId = networks.get(i).networkId; //-1을 연결 정보가 있다면 해당 id로 변경
                }
            }*/

            //연결 정보가 없다면 네트워크를 추가하고 id를 받음
            if (networkId == -1) {
                networkId = wm.addNetwork(wfc);
            }
            Log.d("-진우-", "networkId : " + networkId);

            //연결 여부 : false
            boolean connection=false;

            if (networkId != -1) {
                //Toast.makeText(getApplicationContext(), R.string.equipmentActivity_connectionAlert, Toast.LENGTH_LONG).show();
                //해당 networkId로 wifi를 연결함

                connection = wm.enableNetwork(networkId, true); //연결이 되면 true를 반환
                Log.d("-진우-", "connection : " + connection);
            }
            if(connection){
				wm.disconnect();
                Intent i = new Intent();
                i.putExtra("p_ssid", ssid);
                i.putExtra("p_capabilities", capabilities);
                i.putExtra("p_password", edit_password.getText().toString());
                setResult(RESULT_OK, i);
                finish();

            }
            else{
                Toast.makeText(getApplicationContext(), R.string.equipmentActivity_failPW, Toast.LENGTH_LONG).show();
                finish();
            }

		}else if(v.getId() == R.id.btn_close_popup){
			finish();
		}
	}


}
