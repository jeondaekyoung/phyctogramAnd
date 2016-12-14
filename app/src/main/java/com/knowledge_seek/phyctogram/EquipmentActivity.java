package com.knowledge_seek.phyctogram;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Wifi;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.listAdapter.WifiListAdapter;
import com.pkmmte.view.CircularImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/*
 * Created by dkfka on 2015-12-02.
 */
public class EquipmentActivity extends BaseActivity {
    final String TAG = EquipmentActivity.class.getName();
    //wifi관련
    private ScanResult scanResult;
    private WifiManager wm;
    private List apList;
    private WifiListAdapter wifiListAdapter;
    private List<Wifi> wifiList = new ArrayList<>();
    private ListView lv_wifilist;
    private ArrayList<String> E_response;


    //레이아웃정의 - 슬라이드메뉴
    private LinearLayout ic_screen;
    private ImageButton btn_left;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름
    private Button btn_connWifi,guide_show;


    private ImageView img_btn;

    //requestCode
    private static final int REQUEST_ACT = 111;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_equipment, ic_screen, true);

        //test
        img_btn = (ImageView) findViewById(R.id.img_btn);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //어드민 모드 화면 진입
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
            }
        });

        //슬라이드 내 이미지, 셋팅
        img_profile = (CircularImageView) findViewById(R.id.img_profile);
        if (memberImg != null) {
            img_profile.setImageBitmap(memberImg);
        }

        //기기 검색 버튼 셋팅
        btn_connWifi = (Button) findViewById(R.id.connWifiBtn);
        btn_connWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_connWifi.setText(R.string.equipmentActivity_searching);
                searchStartWifi();

            }
        });
        guide_show =(Button) findViewById(R.id.guide_show);
        guide_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),GuideActivity.class);
                startActivity(intent);
            }
        });

        //슬라이드 내 이름, 셋팅
        tv_member_name = (TextView) findViewById(R.id.tv_member_name);
        if (memberName != null) {
            tv_member_name.setText(memberName);
        }

        //슬라이드 내 아이 목록(ListView)에서 아이 선택시
        lv_usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*nowUsers = (Users) usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' 아이를 선택하였습니다", Toast.LENGTH_LONG).show();*/
            }
        });
        //레이아웃 정의
        btn_left = (ImageButton) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });
    }

    //wifi 초기화 및 검색 start
    public void searchStartWifi(){
        //마시멜로 버전 이상이라면 권한 체크 요청함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
            }catch (Exception e){
                Log.d("-진우-","ParingActivity requestPermissions Exception : "+ e.getMessage());
            }
        }else{
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
    }
    //권한 체크 결과 받음
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 11) {
            //허용 0, 비허용 -1
            if (grantResults[0] == 0){
                //WifiManager 초기화
                wm = (WifiManager) getSystemService(WIFI_SERVICE);

                boolean checkWifi = wm.isWifiEnabled();

                Log.d("-진우-", "6.0이상 checkWifi : "+checkWifi);
                if(!checkWifi){
                    wm.setWifiEnabled(true);
                }

                //검색 하기
                wm.startScan();
                IntentFilter filter = new IntentFilter();
                //검색 결과로 리시버를 등록하고 SCAN_RESULTS_AVAILABLE_ACTION 네임으로 설정한다.
                filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(wifiReceiver, filter);
            }else{
                Log.d("-진우-", "fail");
            }
        }
    }

    //wifi 검색 완료 receiver
    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //네임이 SCAN_RESULTS_AVAILABLE_ACTION 인 방송이 들어오면 wifi 연결함
            wifiList.clear();
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                //2016.10.13  테스트
                int index = 0;
                //boolean isWifiConnect = false;
                apList = wm.getScanResults(); //스캔된 wifi 정보를 받음
                if (wm.getScanResults() != null) {
                    int size = apList.size();
                    for (int i = 0; i < size; i++) {
                        scanResult = (ScanResult) apList.get(i); //wifi 정보를 하나씩 선택
                        if(scanResult.SSID.contains("phyctogram_")){//wifi 정보를 걸러내어 list에 입력
                            wifiList.add(new Wifi(scanResult.SSID,scanResult.capabilities,String.valueOf(scanResult.level)));
                            //isWifiConnect = true;// 연결유무

                        }

                    }
                    unregisterReceiver(wifiReceiver);    //리시버 해제
                }

                if(wifiList.size()>=2){//기기 복수일시 리스트로 선택
                    lv_wifilist = (ListView)findViewById(R.id.lv_wifiList);
                    wifiListAdapter = new WifiListAdapter(getApplication(), wifiList, R.layout.list_wifi);
                    lv_wifilist.setAdapter(wifiListAdapter);

                    //ListView Item 클릭 시
                    lv_wifilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //선택된 wifi 정보를 뽑음
                            Wifi wifi = (Wifi) wifiListAdapter.getItem(position);
                            try {
                                E_response=new Equipment_TCP_Client_Task(getApplicationContext(),wm).execute("w",wifi.getSsid(), "phyctogram", wifi.getCapabilities()).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if(E_response==null){
                                Log.d("-대경-", "재연결");
                                try {
                                    E_response=new Equipment_TCP_Client_Task(getApplicationContext(),wm).execute("w",wifi.getSsid(), "phyctogram", wifi.getCapabilities()).get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    btn_connWifi.setText(R.string.equipmentActivity_endSearch);
                }
                else if(wifiList.size()==1) {// 기기가 한대일땐 자동 연결
                    Wifi wifi=new Wifi(wifiList.get(0).getSsid(),wifiList.get(0).getCapabilities(),wifiList.get(0).getSignal());
                        Log.d("-대경-", "바로연결");
                    try {
                        E_response=new Equipment_TCP_Client_Task(getApplicationContext(),wm).execute("w",wifi.getSsid(), "phyctogram", wifi.getCapabilities()).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if(E_response==null){
                        Log.d("-대경-", "재연결");
                        try {
                            E_response=new Equipment_TCP_Client_Task(getApplicationContext(),wm).execute("w",wifi.getSsid(), "phyctogram", wifi.getCapabilities()).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
            Toast.makeText(getApplicationContext(), R.string.equipmentActivity_noDevice, Toast.LENGTH_SHORT).show();
                    wifiList.clear();
                }

                btn_connWifi.setText(R.string.equipmentActivity_endSearch);
              // searchWifi();
            }
        }
    };

    /**
     *  오름차순
     * @author falbb
     *
     */
    static class Signal_AscCompare implements Comparator<Wifi> {
        @Override
        public int compare(Wifi arg0,Wifi arg1) {
           return Integer.valueOf(arg0.getSignal()) > Integer.valueOf(arg1.getSignal())? -1 : Integer.valueOf(arg0.getSignal()) <Integer.valueOf(arg1.getSignal())  ? 1:0;

        }

    }

    //wifi List view 셋팅
    public void search_Wifi() {

        //w 명령어
        if(E_response.get(0).equals("w")&& E_response!=null){
            wifiList.clear();
            String ssid;
            String singal;
            String encription;
            String temp;
            for (int i = 2; i < E_response.size(); i++) {// i=2이유는 0번방에는 command, 1번방에는 기기에서 보내준 wifi 리스트 갯수, 2번방부터 리스트정보들
                // 0) INVENTURE	Signal: -66 dBm	Encription: WPA2
                //ssid=E_response.get(i).substring(E_response.get(i).indexOf(") "),E_response.get(i).indexOf("\tSignal:"));
                temp=E_response.get(i);
                if(temp.contains(") ")&&temp.contains("Signal: ")&&temp.contains("Encription:")){//BufferedReader로 읽을때 인코딩
                    ssid=temp.substring(temp.indexOf(") ")+2,temp.indexOf("\tSignal:")).replace("�","");
                    singal = temp.substring(temp.indexOf("Signal: ")+8,temp.indexOf(" dBm"));
                    encription = temp.substring(temp.indexOf("Encription: ")+12,temp.length());
                    wifiList.add(new Wifi(ssid,encription,singal)); //wifi 정보를 걸러내어 list에 입력
                }
                else{
                    continue;
                }
                Log.d(TAG, "search_Wifi ssid="+ssid+",singal="+singal +",Encription="+encription);
                temp="";

            }
            Collections.sort(wifiList, new Signal_AscCompare());
            Log.d("-대경-", "wifiList: "+wifiList);
        }

        //wifi 리스트를 adapter를 통하여 ListView에 셋팅함
        lv_wifilist = (ListView)findViewById(R.id.lv_wifiList);
        wifiListAdapter = new WifiListAdapter(this, wifiList, R.layout.list_wifi);
        lv_wifilist.setAdapter(wifiListAdapter);

        //ListView Item 클릭 시
        lv_wifilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //선택된 wifi 정보를 봅음
                Wifi wifi = (Wifi) wifiListAdapter.getItem(position);
                String capabilities = wifi.getCapabilities(); //보안 방식을 가져옴
                //보안이 걸려있다면 비밀번호 입력 팝업을 오픈
                if(capabilities.contains("WEP")||capabilities.contains("WPA")||capabilities.contains("WPA2")){
                    openPopup(wifi.getSsid(), wifi.getCapabilities());
                }else{
                    //보안이 없다면 바로 연결
                    Log.d(TAG, "onItemClick: wifi.getSsid() : "+ wifi.getSsid());
                    new Equipment_TCP_Client_Task(getApplicationContext(),wm).execute("s "+wifi.getSsid());
                }
            }
        });

        btn_connWifi.setText(R.string.equipmentActivity_endSearch);

    }

    //보안 wifi 일 경우 팝업 오픈하여 비밀번호 입력하게 함
    public void openPopup(String ssid, String capabilities){
        //현재 activity를 팝업에 셋팅함
        //PopUpActivity.activity = this;

        //팝업 오픈
        Intent i = new Intent(getApplicationContext(), PopUpActivity.class);
        i.putExtra("ssid", ssid);
        i.putExtra("capabilities", capabilities);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(i,REQUEST_ACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: 실행");

        if (resultCode != RESULT_OK) {
            Log.d(TAG, "onActivityResult: 결과가 성공이 아님.");
            return;
        }

        if (requestCode == REQUEST_ACT) {
            //popupActiviy에서 가져온 ap 정보
            String p_ssid = data.getStringExtra("p_ssid");
            String p_password = data.getStringExtra("p_password");
            String p_capabilities = data.getStringExtra("p_capabilities");
            Log.d(TAG, "onActivityResult: 결과:" +p_ssid+","+p_password+","+p_capabilities);
            new Equipment_TCP_Client_Task(getApplicationContext(),wm).execute("s "+p_ssid+" "+p_password);

        } else {
            Log.d(TAG, "onActivityResult: REQUEST_ACT가 아님.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "EquipmentActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);

        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

        Log.d("-진우-", "EquipmentActivity 에서 onResume() : " + member.toString());

        Log.d("-진우-", "EquipmentActivity.onResume() 끝");

    }


    private class Equipment_TCP_Client_Task extends AsyncTask <Object, Integer, ArrayList<String>> {

        protected 	String SERV_IP		=	"192.168.4.1"; //server ip
        protected 	int		  PORT		=	80;

        private BufferedReader networkReader;
        private BufferedWriter networkWriter;
        ArrayList<String> response ;
        private WifiManager mWm;
        private Context mContext;

        public Equipment_TCP_Client_Task (Context context,WifiManager wm){
            mContext = context;
            mWm= wm;

        }
        private ProgressDialog dialog = new ProgressDialog(EquipmentActivity.this);


        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {

            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait)+"\n"+
                    getString(R.string.equipmentActivity_searching));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Object... params) {

            if(mWm!=null&&!mWm.getConnectionInfo().getSSID().contains("phyctogram_")){
                try {
                    Log.d("-대경-", "phytogram으로연결되어있지않음. ");
                    String ssid = (String) params[1];
                    String password = (String) params[2];
                    String capabilities = (String) params[3];
                    Log.d("-진우-", "ssid: " + ssid + ",password: " + password + ",capablities: " + capabilities);
                    WifiConfiguration wfc = EquipmentActivity.getWifiConfiguration(ssid, password, capabilities);

                    Log.d("-진우-", "wfc : " + wfc.toString());

                    int networkId = -1; //-1 연결 정보 없음
                    List<WifiConfiguration> networks = mWm.getConfiguredNetworks();
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
                        networkId = mWm.addNetwork(wfc);
                    }
                    Log.d("-진우-", "networkId : " + networkId);

                    //연결 여부 : false
                    boolean connection;

                    if (networkId != -1) {
                        //Toast.makeText(getApplicationContext(), R.string.equipmentActivity_connectionAlert, Toast.LENGTH_LONG).show();
                        //해당 networkId로 wifi를 연결함
                        connection = mWm.enableNetwork(networkId, true); //연결이 되면 true를 반환
                        Log.d("-진우-", "connection : " + connection);
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();

                    return null;
                }
                try {
                    Thread.sleep(5000);
                    Log.d( "thread.sleep ","wifi 연결을 위한 sleep 5초");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Log.d("TCP","server connecting");
                String  command = (String) params[0];

                Log.d(TAG, "command :"+command);

                Socket socket = new Socket(SERV_IP,PORT);
                networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"ASCII"));
                WriteSocket(networkWriter,command);

                PrintWriter out = new PrintWriter(networkWriter, true);
                out.flush();

                String  line;
                response = new ArrayList<String>();

                //디버깅을위한 list(0) command로
                response.add(command);
                if(command.equals("w")){
                    int i =0;
                    while (true){
                        line = networkReader.readLine();
                        Log.d("1 line",line);
                        if(!line.contains("Signal:")){//ssid 가 30비트 이상일 경우(길 경우) 개행이 되어 signal:이 포함되있지 않으면 read라인
                            if(!line.contains("number of available networks")){//첫라인은 제외
                                line += networkReader.readLine();
                            }

                            Log.d("2 line",line);
                        }
                        Log.d("3 line",line);
                        response.add(line);
                        i++;
                        if(i==Integer.valueOf(response.get(1).substring(response.get(1).indexOf(":")+1,response.get(1).length()))+1){//기기에서 보내준 wifi 갯수일때  break(첫라인은 info므로 +1)
                            break;
                        }
                    }
                    Log.w("-대경-", "while문 종료");

                }
                else{
                    line = networkReader.readLine();
                    Log.d("line",line);
                    response.add(line);
                }
                Log.d("response: ",response.toString());
                socket.close();
                networkWriter.close();
                networkReader.close();



            } catch(IOException e){
                e.printStackTrace();
            }
            return response;
        }
        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(ArrayList<String>  response) {
            if(response==null){
                dialog.dismiss();
                Toast.makeText(mContext,"기기와 연결이 비정상적으로 종료 되었습니다.\n기기와 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                return;
            }
            // Toast.makeText(mContext,"명령어: "+response.get(0)+"response 1 Line:"+response.get(1) , Toast.LENGTH_LONG).show();
            //E_response=response;
            search_Wifi();
            dialog.dismiss();
            super.onPostExecute(response);


        }


        public void WriteSocket(BufferedWriter data,String s) throws IOException{
            //	data send
            data.write(s);
            data.flush();
            System.out.println("데이터를 송신 하였습니다.");
        }

    }

    @NonNull
    public static WifiConfiguration getWifiConfiguration(String ssid, String password, String capabilities) {
        WifiConfiguration wfc = new WifiConfiguration();

        //wifi 공통 규칙
        wfc.SSID = "\"".concat( ssid ).concat("\"");
        wfc.status = WifiConfiguration.Status.DISABLED;
        wfc.priority = 40;

        //wifi 보안 종류별 개별 규칙
        if(capabilities.contains("WEP")){
            Log.d("-진우-", "WEP 셋팅");
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.wepKeys[0] = "\"".concat(password).concat("\"");
            wfc.wepTxKeyIndex = 0;

        }else if(capabilities.contains("WPA")  ) {
            Log.d("-진우-", "WPA 셋팅");
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            wfc.preSharedKey = "\"".concat(password).concat("\"");

            Log.d("-진우-", "패스워드 확인 : " + wfc.preSharedKey);
        }else if(capabilities.contains("WPA2")  ) {
            Log.d("-진우-", "WPA2 셋팅");
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            wfc.preSharedKey = "\"".concat(password).concat("\"");
        }else if(capabilities.contains("OPEN")  ) {
            Log.d("-진우-", "OPEN 셋팅");

            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedAuthAlgorithms.clear();
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
        return wfc;
    }

}