package com.knowledge_seek.phyctogram;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.domain.Wifi;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.listAdapter.WifiListAdapter;
import com.knowledge_seek.phyctogram.util.EqAsyncTask;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by dkfka on 2015-12-02.
 */
public class EquipmentActivity extends BaseActivity {
    final String TAG = EquipmentActivity.class.getName();
    //wifi관련
    private ScanResult scanResult;
    private WifiManager wm;
    private List apList;

    //레이아웃정의 - 슬라이드메뉴
    private LinearLayout ic_screen;
    private ImageButton btn_left;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름
    private Button btn_connWifi;
    private List<Wifi> wifiList = new ArrayList<>();
    private ListView lv_wifilist;
    private WifiListAdapter wifiListAdapter;
    private ImageView img_btn;

    //레이아웃정의

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.ic_screen);
        LayoutInflater.from(this).inflate(com.knowledge_seek.phyctogram.R.layout.include_equipment, ic_screen, true);

        //test
        img_btn = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.img_btn);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //어드민 모드 화면 진입
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
            }
        });

        //슬라이드 내 이미지, 셋팅
        img_profile = (CircularImageView) findViewById(com.knowledge_seek.phyctogram.R.id.img_profile);
        if (memberImg != null) {
            img_profile.setImageBitmap(memberImg);
        }

        //기기 검색 버튼 셋팅
        btn_connWifi = (Button) findViewById(com.knowledge_seek.phyctogram.R.id.connWifiBtn);
        btn_connWifi.setText(com.knowledge_seek.phyctogram.R.string.equipmentActivity_searchHW);
        btn_connWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_connWifi.setText(com.knowledge_seek.phyctogram.R.string.equipmentActivity_searching);
                searchStartWifi();
            }
        });

        //슬라이드 내 이름, 셋팅
        tv_member_name = (TextView) findViewById(com.knowledge_seek.phyctogram.R.id.tv_member_name);
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
        btn_left = (ImageButton) findViewById(com.knowledge_seek.phyctogram.R.id.btn_left);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
                boolean isWifiConnect = false;
                apList = wm.getScanResults(); //스캔된 wifi 정보를 받음
                if (wm.getScanResults() != null) {
                    int size = apList.size();
                    for (int i = 0; i < size; i++) {
                        scanResult = (ScanResult) apList.get(i); //wifi 정보를 하나씩 선택
                        if(scanResult.SSID.contains("PHYCTOGRAM")){//wifi 정보를 걸러내어 list에 입력
                            wifiList.add(new Wifi(scanResult.SSID,scanResult.capabilities));
                            isWifiConnect = true;// 연결유무

                        }

                    }
                    unregisterReceiver(wifiReceiver);    //리시버 해제
                }

                if(wifiList.size()>=2){//기기 복수일시 리스트로 선택
                    lv_wifilist = (ListView)findViewById(com.knowledge_seek.phyctogram.R.id.lv_wifiList);
                    wifiListAdapter = new WifiListAdapter(getApplication(), wifiList, com.knowledge_seek.phyctogram.R.layout.list_wifi);
                    lv_wifilist.setAdapter(wifiListAdapter);

                    //ListView Item 클릭 시
                    lv_wifilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //선택된 wifi 정보를 봅음
                            Wifi wifi = (Wifi) wifiListAdapter.getItem(position);
                            String capabilities = wifi.getCapabilities(); //보안 방식을 가져옴
                            //보안이 걸려있다면 비밀번호 입력 팝업을 오픈
                            if(capabilities.contains("WEP")||capabilities.contains("WPA")||capabilities.contains("WPA2")||capabilities.contains("OPEN")){
                                openPopup(wifi.getSsid(), wifi.getCapabilities());
                            }else{
                                //보안이 없다면 바로 연결
                                connectWifi(wifi.getSsid(), "", wifi.getCapabilities());
                            }
                        }
                    });

                    btn_connWifi.setText(com.knowledge_seek.phyctogram.R.string.equipmentActivity_endSearch);
                }
                else if(wifiList.size()==1) {// 기기가 한대일땐 자동 연결
                    Wifi wifi=new Wifi(wifiList.get(0).getSsid(),wifiList.get(0).getCapabilities());
                    String capabilities = wifi.getCapabilities(); //보안 방식을 가져옴

                    //보안이 걸려있다면 비밀번호 입력 팝업을 오픈
                    if(capabilities.contains("WEP")||capabilities.contains("WPA")||capabilities.contains("WPA2")||capabilities.contains("OPEN")){
                        Log.d(TAG, "오픈팝업");
                        openPopup(wifi.getSsid(), wifi.getCapabilities());
                    }else{
                        //보안이 없다면 바로 연결
                        Log.d(TAG, "바로연결");
                        connectWifi(wifi.getSsid(), "", wifi.getCapabilities());
                    }
                }
                else{

                    Toast.makeText(getApplicationContext(), R.string.equipmentActivity_noDevice, Toast.LENGTH_SHORT).show();
                    wifiList.clear();
                    Log.d(TAG, "searchWifi: wifiList 사이즈: "+wifiList.size());
                }


             /*   if (isWifiConnect){
                    Log.d(TAG, "isWifiConnect: "+isWifiConnect+" wifi 리시버 수신끊김");
                }
                else{
                    Log.d(TAG, "isWifiConnect: "+isWifiConnect+ " wifi 리시버 수신중");
                    unregisterReceiver(wifiReceiver);    //리시버 해제
                    Toast.makeText(getApplicationContext(), R.string.equipmentActivity_noDevice, Toast.LENGTH_SHORT).show();
                }*/
                btn_connWifi.setText(com.knowledge_seek.phyctogram.R.string.equipmentActivity_endSearch);

                Log.d(TAG, "searchWifi: wifiList 사이즈: "+wifiList.size());

              // searchWifi();
            }
        }
    };

    //wifi List view 셋팅
    public void searchWifi() {
        unregisterReceiver(wifiReceiver);    //리시버 해제
        apList = wm.getScanResults(); //스캔된 wifi 정보를 받음
        if (wm.getScanResults() != null) {
            int size = apList.size();
            for (int i = 0; i < size; i++) {
                scanResult = (ScanResult) apList.get(i); //wifi 정보를 하나씩 선택

                wifiList.add(new Wifi(scanResult.SSID,scanResult.capabilities)); //wifi 정보를 걸러내어 list에 입력
            }
        }

        //wifi 리스트를 adapter를 통하여 ListView에 셋팅함
        lv_wifilist = (ListView)findViewById(com.knowledge_seek.phyctogram.R.id.lv_wifiList);
        wifiListAdapter = new WifiListAdapter(this, wifiList, com.knowledge_seek.phyctogram.R.layout.list_wifi);
        lv_wifilist.setAdapter(wifiListAdapter);

        //ListView Item 클릭 시
        lv_wifilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //선택된 wifi 정보를 봅음
                Wifi wifi = (Wifi) wifiListAdapter.getItem(position);
                String capabilities = wifi.getCapabilities(); //보안 방식을 가져옴
                //보안이 걸려있다면 비밀번호 입력 팝업을 오픈
                if(capabilities.contains("WEP")||capabilities.contains("WPA")||capabilities.contains("WPA2")||capabilities.contains("OPEN")){
                    openPopup(wifi.getSsid(), wifi.getCapabilities());
                }else{
                    //보안이 없다면 바로 연결
                    connectWifi(wifi.getSsid(), "", wifi.getCapabilities());
                }
            }
        });

        btn_connWifi.setText(com.knowledge_seek.phyctogram.R.string.equipmentActivity_endSearch);

    }

    //보안 wifi 일 경우 팝업 오픈하여 비밀번호 입력하게 함
    public void openPopup(String ssid, String capabilities){
        //현재 activity를 팝업에 셋팅함
        PopUpActivity.activity = this;

        //팝업 오픈
        Intent i = new Intent(getApplicationContext(), PopUpActivity.class);
        i.putExtra("ssid", ssid);
        i.putExtra("capabilities", capabilities);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    //wifi 연결 담당
    public boolean connectWifi(String ssid, String password, String capabilities) {
        Log.d("-진우-", "ssid: " + ssid + ",password: " + password + ",capablities: " + capabilities);
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

            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.wepKeys[0] = "\"".concat(password).concat("\"");
            wfc.wepTxKeyIndex = 0;
        }else if(capabilities.contains("WPA")  ) {
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
        }else if(capabilities.contains("WPA2")  ) {
            Log.d("-진우-", "WPA2 셋팅");
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
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

            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }

        Log.d("-진우-", "wfc : " + wfc.toString());

        int networkId = -1; //-1 연결 정보 없음
        List<WifiConfiguration> networks = wm.getConfiguredNetworks();
     //   Log.d("-진우-", "networks : " + networks.toString());

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

        if(networkId != -1){
            Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.equipmentActivity_connectionAlert, Toast.LENGTH_LONG).show();
            //해당 networkId로 wifi를 연결함
            connection = wm.enableNetwork(networkId, true); //연결이 되면 true를 반환
            Log.d("-진우-", "connection : "+connection);
        }else{
            Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.equipmentActivity_failPW, Toast.LENGTH_LONG).show();
        }

        //연결이 되었다면
        if(connection) {
            //wifi정보를 저장
            wm.setWifiEnabled(true);
            Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.equipmentActivity_successConnection, Toast.LENGTH_LONG).show();

            //연결된 wifi에 ip를 가져옴 필요 여부 판단 필요
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            DhcpInfo dhcpInfo = wm.getDhcpInfo() ;
            int serverIp = dhcpInfo.gateway;
            String ipAddress = String.format(
                    "%d.%d.%d.%d",
                    (serverIp & 0xff),
                    (serverIp >> 8 & 0xff),
                    (serverIp >> 16 & 0xff),
                    (serverIp >> 24 & 0xff));

            Log.d("-진우-", "ipAddress: " + ipAddress);

            //기기에 member_seq 전송
            new EqAsyncTask().execute("192.168.4.1:80", "member_seq", member.getMember_seq()+"**");

            //연결된 픽토그램 기기에 보내줄 wifi 선택 팝업을 오픈
            Intent i = new Intent(getApplicationContext(), WifiPopUpActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("ipAddress", ipAddress);
            startActivity(i);
        }else{
            Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.equipmentActivity_failConnection, Toast.LENGTH_SHORT).show();
        }
        btn_connWifi.setText(com.knowledge_seek.phyctogram.R.string.equipmentActivity_endSearch);
        return true;
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

        //슬라이드메뉴 셋팅(내 아이목록, 계정이름, 계정이미지)
        //EquipmentTask task = new EquipmentTask();
        //task.execute(img_profile);

        Log.d("-진우-", "EquipmentActivity 에서 onResume() : " + member.toString());

        Log.d("-진우-", "EquipmentActivity.onResume() 끝");
    }

    //기록조회페이지 초기 데이터조회(슬라이드 내 아이 목록, 계정이미지)
    private class EquipmentTask extends AsyncTask<Object, Void, Bitmap> {
        private ProgressDialog dialog = new ProgressDialog(EquipmentActivity.this);
        private List<Users> usersTask;
        private CircularImageView img_profileTask;

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(com.knowledge_seek.phyctogram.R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            img_profileTask = (CircularImageView)params[0];

            //슬라이드메뉴에 있는 내 아이 목록
            /*UsersAPI service = ServiceGenerator.createService(UsersAPI.class);
            Call<List<Users>> call = service.findUsersByMember(String.valueOf(member.getMember_seq()));
            try {
                usersTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 목록 가져오기 실패");
            }*/

            /*String image_url = null;
            if(member.getJoin_route().equals("kakao")){
                image_url = member.getKakao_thumbnailimagepath();
                //이미지 불러오기
                InputStream in = null;
                try {
                    Log.d("-진우-", "이미지 주소 : " + image_url);
                    in = new URL(image_url).openStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else if(member.getJoin_route().equals("facebook")){
                image_url = "http://graph.facebook.com/" + member.getFacebook_id() + "/picture?type=large";
                //이미지 불러오기
                InputStream in = null;
                try {
                    //페이스북은 jpg파일이 링크 걸린 것이 아니다.
                    //http://graph.facebook.com/userid/picture?type=large
                    Log.d("-진우-", "이미지 주소 : " + image_url);

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(image_url)
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    in = response.body().byteStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }*/

            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*if(bitmap != null){
                Log.d("-진우-", "이미지읽어옴");
                img_profileTask.setImageBitmap(bitmap);
            }

            if (usersTask != null && usersTask.size() > 0) {
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());
                for (Users u : usersTask) {
                    Log.d("-진우-", "내 아이 : " + u.toString());
                }
                usersList = usersTask;

                usersListSlideAdapter.setUsersList(usersList);
                if (nowUsers == null) {
                    nowUsers = usersTask.get(0);
                }
                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다.");
            }

            int height = getListViewHeight(lv_usersList);
            lv_usersList.getLayoutParams().height = height;
            usersListSlideAdapter.notifyDataSetChanged();*/

            dialog.dismiss();
            super.onPostExecute(bitmap);
        }
    }
}