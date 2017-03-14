package com.knowledge_seek.growCheck;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.growCheck.util.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class PopUpActivity extends Activity implements View.OnClickListener{

	final String TAG = PopUpActivity.class.getName();

	private String ssid;
	private String capabilities;

	private String device_ssid;
	private String device_capabilities;

	private TextView tv_ssid;
	private EditText edit_password;

	private Button btn_close_popup;
	private Button btn_pwdOk;

	public static EquipmentActivity activity;
	public static WifiPopUpActivity wifiPopUpActivity;

    private WifiManager wm;

	private ProgressDialog mDialog;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);


		setContentView(R.layout.popup_wifi);
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
		mDialog =new ProgressDialog(PopUpActivity.this);
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
		device_ssid	=getIntent().getStringExtra("device_ssid");
		device_capabilities = getIntent().getStringExtra("device_capabilities");


		tv_ssid = (TextView) findViewById(R.id.tv_ssid);
		edit_password = (EditText) findViewById(R.id.edit_password);
		tv_ssid.setText(ssid);

	}
	@Override
	public void onClick(View v) {

		if(v.getId() == R.id.btn_pwdOk){
			Log.d("-대경-", "PopUpActivity ssid : " + ssid + ", capabilities : " + capabilities + ", edit_password : " + edit_password.getText());
        	WifiConfiguration wfc = Utility.getWifiConfiguration(ssid, edit_password.getText().toString(), capabilities);
            Log.d("-진우-", "wfc : " + wfc.toString());
          	int networkId = wm.addNetwork(wfc);
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
				new popUp_device_connect_Task().execute(device_ssid,"phyctogram",device_capabilities);

				new guide_TCP_Client_Task().execute("m "+MainActivity.member.getMember_seq(),"s "+ssid+" "+edit_password.getText().toString());
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.equipmentActivity_failPW, Toast.LENGTH_LONG).show();
				finish();
            }

		}else if(v.getId() == R.id.btn_close_popup){
			finish();
		}

	}
		//기기 연결 Task
	private  class popUp_device_connect_Task extends AsyncTask<Object,Void,Boolean> {

			private ProgressDialog dialog = mDialog;


			//Background 작업 시작전에 UI 작업을 진행 한다.
			@Override
			protected void onPreExecute() {

				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setMessage(getApplicationContext().getString(R.string.commonActivity_wait)+"\n"+
						getApplicationContext().getString(R.string.equipmentActivity_searching));
				dialog.setCancelable(false);
				dialog.show();

				super.onPreExecute();

			}
			@Override
			protected Boolean doInBackground(Object... params) {
				Log.d("-대경-", "phytogram 기기와 연결 시작.... " +Thread.currentThread().getName());
				Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);

				//접속여부
				Boolean connection = false;
				if(wm!=null){

					try {
						String ssid = (String) params[0];
						String password = (String) params[1];
						String capabilities = (String) params[2];
						Log.d("-대경- 기기 Ap 정보:", "ssid: " + ssid + ",password: " + password + ",capablities: " + capabilities);
						WifiConfiguration wfc = Utility.getWifiConfiguration(ssid, password, capabilities);
						//Log.d("-진우-", "wfc : " + wfc.toString());
						int networkId = -1; //-1 연결 정보 없음
						List<WifiConfiguration> networks = wm.getConfiguredNetworks();
						//   Log.d("-진우-", "networks : " + networks.toString());
						for (int i = 0; i < networks.size(); i++) {
							//Log.d("-진우-", "networks.get(i).SSID : " + networks.get(i).SSID);
							if (networks.get(i).SSID.equals("\"".concat(ssid).concat("\""))) {
								Log.d("-진우-", "networks.get(i).networkId : " + networks.get(i).networkId);
								networkId = networks.get(i).networkId; //-1을 연결 정보가 있다면 해당 id로 변경
							}
						}
						//연결 정보가 없다면 네트워크를 추가하고 id를 받음
						if (networkId == -1) {
							networkId = wm.addNetwork(wfc);
						}
						Log.d("-진우-", "networkId : " + networkId);
						//연결 여부 : false

						if (networkId != -1) {
							//Toast.makeText(getApplicationContext(), R.string.equipmentActivity_connectionAlert, Toast.LENGTH_LONG).show();
							//해당 networkId로 wifi를 연결함
							connection = wm.enableNetwork(networkId, true); //연결이 되면 true를 반환
							Log.d("-진우-", "connection : " + connection);

						}
						Thread.sleep(3000);
					}catch (ArrayIndexOutOfBoundsException e){
						e.printStackTrace();

						return connection;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				else if(wm.getConnectionInfo().getSSID().contains("phyctogram_")){
					return true;
				}
				Log.d("-대경-", "phytogram 기기와 연결 끝.... " +Thread.currentThread().getName());
				return connection;
			}
			@Override
			protected void onPostExecute(Boolean  response) {

				/*if(!response){
					dialog.dismiss();
					wm.disconnect();
					Intent i = new Intent();
					i.putExtra("p_ssid", ssid);
					i.putExtra("p_capabilities", capabilities);
					i.putExtra("p_password", edit_password.getText().toString());
					i.putExtra("isDevice_connected",response);
					setResult(RESULT_OK, i);
					finish();
					Toast.makeText(getApplicationContext(),R.string.includeGuide_tcpConectFail, Toast.LENGTH_SHORT).show();
					return;
				}
				else{
					wm.disconnect();
					Intent i = new Intent();
					i.putExtra("p_ssid", ssid);
					i.putExtra("p_capabilities", capabilities);
					i.putExtra("p_password", edit_password.getText().toString());
					i.putExtra("isDevice_connected",response);
					setResult(RESULT_OK, i);
					finish();
					dialog.dismiss();
					Toast.makeText(getApplicationContext(),R.string.includeGuide_tcpConectSuccess, Toast.LENGTH_SHORT).show();
				}*/
				super.onPostExecute(response);


			}

	}

	private class guide_TCP_Client_Task extends AsyncTask<Object, Integer, ArrayList<String>> {

			protected 	String SERV_IP		=	"192.168.4.1"; //server ip
			protected 	int		  PORT		=	80;

			private BufferedReader networkReader;
			private BufferedWriter networkWriter;
			private ArrayList<String> response ;

			private ProgressDialog dialog = mDialog;


			//Background 작업 시작전에 UI 작업을 진행 한다.
			@Override
			protected void onPreExecute() {

				super.onPreExecute();
			}

			@Override
			protected ArrayList<String> doInBackground(Object... params) {
				Log.d("-대경-", "phytogram 기기와 통신 시작.... " +Thread.currentThread().getName());
			try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(wm!=null&&wm.getConnectionInfo().getSSID().contains("phyctogram_")) {
					try {
						Log.d("TCP", "server connecting");
						String command = (String) params[0];
						String command2 = (String) params[1];
						Log.d(TAG, "command :" + command);
						Log.d(TAG, "command2 :" + command2);
						Socket socket = new Socket(SERV_IP, PORT);
						networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ASCII"));

						String line;
						response = new ArrayList<>();
						response.add(command+"&"+command2);//디버깅을위한 list(0) command로

					for (int i= 0 ; i<2 ; i++){
						if(i==0){
							WriteSocket(networkWriter, command);
						}
						if(i==1){
							WriteSocket(networkWriter, command2);
						}
						PrintWriter out = new PrintWriter(networkWriter, true);
						out.flush();
						line = networkReader.readLine();
						Log.d("line", line);
						response.add(line);
						Log.d("response: ", response.toString());
					}

						socket.close();
						networkWriter.close();
						networkReader.close();


					} catch (IOException e) {
						e.printStackTrace();
						response = new ArrayList<>();

						response.add("IOException");
						response.add("Fail");
						return response;
					}

				}

				Log.d("-대경-", "phytogram 기기와 통신 끝.... " +Thread.currentThread().getName());
				return response;
			}
			//Background 작업이 끝난 후 UI 작업을 진행 한다.
			@Override
			protected void onPostExecute(ArrayList<String>  response) {
				if(response!=null) {
					Log.d("-대경-",response.toString());
					dialog.dismiss();
					if(response.get(1).equals("OK")&&response.get(2).equals("OK")){
						dialog.dismiss();
						String className= getIntent().getStringExtra("className");
						Log.d(TAG, "-대경- className: "+className);
						switch (className){
							case "GuideActivity":
								GuideActivity.viewPager.setCurrentItem(2);
								break;
							case "Guide_wifiActivity":
								Guide_wifiActivity.viewPager.setCurrentItem(2);
								break;
						}

						finish();
					}
					else{
						dialog.dismiss();
						Toast.makeText(getApplicationContext(), R.string.equipmentActivity_failConnection, Toast.LENGTH_LONG).show();
						finish();

					}

				}else{
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), R.string.equipmentActivity_failConnection, Toast.LENGTH_LONG).show();
					finish();

				}
				super.onPostExecute(response);
			}
			public void WriteSocket(BufferedWriter data,String s) throws IOException{
				//	data send
				data.write(s);
				data.flush();
				System.out.println("데이터를 송신 하였습니다.");
			}

		}

}
