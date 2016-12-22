package com.knowledge_seek.phyctogram;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Wifi;
import com.knowledge_seek.phyctogram.guide.page_1;
import com.knowledge_seek.phyctogram.guide.page_2;
import com.knowledge_seek.phyctogram.guide.page_4;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.listAdapter.WifiListAdapter;
import com.knowledge_seek.phyctogram.phyctogram.SaveSharedPreference;

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

/**
 * Created by dkfka on 2016-11-16..
 */
public class Guide_wifiActivity extends FragmentActivity {

    final String TAG = Guide_wifiActivity.class.getName();

    //프레그먼트
    int MAX_PAGE=3;
    Fragment cur_fragment=new Fragment();
    public static ViewPager viewPager;

    //다이얼로그
    AlertDialog.Builder dialog_page3;
    public  static AlertDialog.Builder  dialog_close;


    //wifi관련
    private ScanResult scanResult;
    private WifiManager wm;
    private List apList;
    private WifiListAdapter wifiListAdapter;
    private List<Wifi> wifiList = new ArrayList<>();
    private ListView guide_lv;
    private TextView guide_lvEmpty;
    private Button btn_searchWifi;
    private TextView guide_ref;
    private EditText guide_adj;
    public static final int REQUEST_ACT = 112;
    private ArrayList<String> E_response;
    private static Wifi device;


    private ProgressDialog mDialog ;
    //CountDownLatch latch1 = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog = new ProgressDialog(Guide_wifiActivity.this);
        dialog_close=new AlertDialog.Builder(this).setMessage(R.string.includeGuide_btnclose).setPositiveButton(R.string.commonActivity_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SaveSharedPreference.setGuideFlag(getApplicationContext(),false);
                finish();

            }
        }).setNegativeButton(R.string.commonActivity_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        setContentView(R.layout.activity_guide);
         viewPager=(ViewPager)findViewById(R.id.viewpager);
         viewPager.setAdapter(new adapter(getSupportFragmentManager()));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                 int targetPage= -1;
                  @Override
                  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    targetPage =position;

                  }
                  @Override
                  public void onPageSelected(int position) {

                      if(position==1){
                          //page2 기기 검색
                            new ListView_AsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        }

                  }
                  @Override
                  public void onPageScrollStateChanged(int state) {}
            }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseActivity.setStatusBarColor(this,R.color.purpledk);
    }

    //리스트뷰
    private class ListView_AsyncTask extends AsyncTask<Void, Void, Void> {

            private ProgressDialog dialog =mDialog;

            //Background 작업 시작전에 UI 작업을 진행 한다.
            @Override
            protected void onPreExecute() {

                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage(getApplicationContext().getString(R.string.commonActivity_wait)+"\n"+
                        getApplicationContext().getString(R.string.equipmentActivity_searching));
                dialog.setCancelable(false);
                dialog.show();
                //프레그먼트가 먼저 만들어지고 리스너를 부착 해야함.
                btn_searchWifi = page_2.btn_searchWifi;
                btn_searchWifi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Guide_wifiActivity.ListView_AsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                });
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                Log.d("-대경-", "phytogram 기기와 검색 시작.... " +Thread.currentThread().getName());
                Log.d(TAG, "Pid: "+Process.myPid());
                Log.d(TAG, "우선순위: "+Thread.currentThread().getPriority());
                searchStartWifi();
                Log.d("-대경-", "phytogram 기기와 검색 끝.... " +Thread.currentThread().getName());
                return null;
            }

            protected void onPostExecute(Void result) {

                //dialog.dismiss();
                super.onPostExecute(result);

            }
    }

    //-------------------------------------------receiver & BroadcastReceiver

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
        //검색 결과로 리시버를 등록하고 SCAN_RESULTS_AVAILABLE_ACTION 네임으로 설정한다.
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReceiver, filter);
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
                            if(scanResult.SSID.length()!=0){
                                wifiList.add(new Wifi(scanResult.SSID,scanResult.capabilities,String.valueOf(scanResult.level)));
                            }
                            //isWifiConnect = true;// 연결유무
                        }
                    }
                    unregisterReceiver(wifiReceiver);    //리시버 해제
                }
                guide_lv = page_2.guide_lv;
                guide_lvEmpty = page_2.guide_lvEmpty;
                if(guide_lv.getVisibility()==View.GONE){//재검색일때 GONE을 INVISIBLE로 변경
                    guide_lv.setVisibility(View.VISIBLE);
                    guide_lvEmpty.setVisibility(View.GONE);
                }
                int size = wifiList.size();
                if(size>=2){
                    size =2;
                }

                switch (size){
                    case 1:// 기기가 한대일땐 자동 연결
                        Wifi wifi=new Wifi(wifiList.get(0).getSsid(),wifiList.get(0).getCapabilities(),wifiList.get(0).getSignal());
                        device= wifi;
                        Log.d("-대경-", "바로연결");
                            //page2 단수 일때 기기 연결
                            new guide_device_connect_Task().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,wifi.getSsid(),"phyctogram",wifi.getCapabilities());

                        break;
                    case 2://기기 복수일시 리스트로 선택
                        Collections.sort(wifiList, new Signal_AscCompare());
                        wifiListAdapter = new WifiListAdapter(getApplication(), wifiList, R.layout.list_wifi);
                        guide_lv.setAdapter(wifiListAdapter);
                        Log.d(TAG, "onReceive: "+wifiList);
                        //ListView Item 클릭 시
                        guide_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //선택된 wifi 정보를 뽑음
                                Wifi wifi = (Wifi) wifiListAdapter.getItem(position);
                                device = wifi;
                                    new guide_device_connect_Task().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,wifi.getSsid(),"phyctogram",wifi.getCapabilities());
                            }
                        });
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), R.string.equipmentActivity_noDevice, Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        wifiList.clear();

                        guide_lv.setVisibility(View.GONE);
                        guide_lvEmpty.setVisibility(View.VISIBLE);
                        break;


                }

            }
        }
    };


    //----------------------------------------------Task----------------------------------------------------------------

    //기기 연결 Task
    private  class guide_device_connect_Task extends  AsyncTask <Object,Void,Boolean>{

        private ProgressDialog dialog = mDialog;


        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {super.onPreExecute();}
        @Override
        protected Boolean doInBackground(Object... params) {
            Log.d("-대경-", "phytogram 기기와 연결 시작.... " +Thread.currentThread().getName());
            Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.d(TAG, "id: "+Thread.currentThread().getId());
            Log.d(TAG, "우선순위: "+Thread.currentThread().getPriority());
            //접속여부
            Boolean connection = false;
          if(wm!=null){

                try {
                    String ssid = (String) params[0];
                    String password = (String) params[1];
                    String capabilities = (String) params[2];
                    Log.d("-대경- 기기 Ap 정보:", "ssid: " + ssid + ",password: " + password + ",capablities: " + capabilities);
                    WifiConfiguration wfc = EquipmentActivity.getWifiConfiguration(ssid, password, capabilities);
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
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getActiveNetworkInfo();
                    Log.d(TAG, "mWifi.isConnected() : " + mWifi.isConnected() + ",wm ssid : " + wm.getConnectionInfo().getSSID()+",state :"+mWifi.isConnected());
                    if(connection){
                        connection=wm.getConnectionInfo().getSSID().contains("phyctogram_")&&mWifi.isConnected();
                    }
                  }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();

                    return connection;
                }

            }

             Log.d("-대경-", "phytogram 기기와 연결 끝.... " +Thread.currentThread().getName());
            return connection;
        }
        @Override
        protected void onPostExecute(Boolean  response) {

            if(!response){
                dialog.dismiss();
                Toast.makeText(Guide_wifiActivity.this,R.string.includeGuide_tcpConectFail, Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getActiveNetworkInfo();
                Toast.makeText(Guide_wifiActivity.this,R.string.includeGuide_tcpConectSuccess, Toast.LENGTH_SHORT).show();
                if(wm.getConnectionInfo().getSSID().contains("phyctogram_")&&mWifi.isConnected()){
                    new guide_TCP_Client_Task().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,"w");
                }
            }

            super.onPostExecute(response);


        }

    }


    //기기 통신 Task
    private class guide_TCP_Client_Task extends AsyncTask<Object, Integer, ArrayList<String>> {

        protected 	String SERV_IP		=	"192.168.4.1"; //server ip
        protected 	int		  PORT		=	80;

        private BufferedReader networkReader;
        private BufferedWriter networkWriter;
        private ArrayList<String> response ;

        private ProgressDialog dialog =mDialog;


        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {

            /*dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getApplicationContext().getString(R.string.commonActivity_wait)+"\n"+
                    getApplicationContext().getString(R.string.equipmentActivity_searching));
            dialog.setCancelable(false);
            dialog.show();*/
            if(!dialog.isShowing()){
                Log.d(TAG, "프로그래스다이얼로그 가 안떠있음.");
                dialog.show();
            }

            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Object... params) {
            Log.d("-대경-", "phytogram 기기와 통신 시작.... " +Thread.currentThread().getName());
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            Log.d(TAG, "우선순위: "+Thread.currentThread().getPriority());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if(wm!=null&&wm.getConnectionInfo().getSSID().contains("phyctogram_")) {
                    try {
                        Log.d("TCP", "server connecting");
                        String command = (String) params[0];
                        Log.d(TAG, "command :" + command);
                        Socket socket = new Socket(SERV_IP, PORT);
                        networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ASCII"));
                        WriteSocket(networkWriter, command);

                        PrintWriter out = new PrintWriter(networkWriter, true);
                        out.flush();

                        String line;
                        response = new ArrayList<>();

                        //디버깅을위한 list(0) command로
                        response.add(command);
                        if (command.equals("w")) {
                            int i = 0;
                            while (true) {
                                line = networkReader.readLine();
                                //Log.d("1 line", line);
                                if (!line.contains("Signal:")) {//ssid 가 30비트 이상일 경우(길 경우) 개행이 되어 signal:이 포함되있지 않으면 read라인
                                    if (!line.contains("number of available networks")) {//첫라인은 제외
                                        line += networkReader.readLine();
                                    }

                                  //  Log.d("2 line", line);
                                }
                                //Log.d("3 line", line);
                                response.add(line);
                                i++;
                                if (i == Integer.valueOf(response.get(1).substring(response.get(1).indexOf(":") + 1, response.get(1).length())) + 1) {//기기에서 보내준 wifi 갯수일때  break(첫라인은 info므로 +1)
                                    break;
                                }
                            }
                            Log.w("-대경-", "while문 종료");

                        } else {
                            line = networkReader.readLine();
                            Log.d("line", line);
                            response.add(line);
                        }
                        Log.d("response: ", response.toString());
                        socket.close();
                        networkWriter.close();
                        networkReader.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                        response = new ArrayList<>();

                        response.add("IOException");
                        response.add("Fail");
                        return response;
                    }/* finally {
                        try {
                            latch1.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/

            }
            Log.d("-대경-", "phytogram 기기와 통신 끝.... " +Thread.currentThread().getName());
            return response;
        }
        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(ArrayList<String>  response) {

            if(response.get(0).equals("IOException")){
                dialog.dismiss();
                super.onPostExecute(response);
                Toast.makeText(Guide_wifiActivity.this,R.string.includeGuide_tcpConectFail, Toast.LENGTH_SHORT).show();
                return;
            }
            if(response!=null) {
                E_response=response;
                if (response.get(0).contains("w")) {
                    search_Wifi();
                } else if (response.get(0).contains("r")) {
                     Log.d("-대경-","명령어: "+response.get(0)+"response 1 Line:"+response.get(1));
                }
            }
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


    //-------------------------------------------utill------------------------------------------------------------

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
                //Log.d(TAG, "search_Wifi ssid="+ssid+",singal="+singal +",Encription="+encription);
                temp="";

            }
            Collections.sort(wifiList, new Signal_AscCompare());
            Log.d("-대경-", "wifiList: "+wifiList);
        }

        //wifi 리스트를 adapter를 통하여 ListView에 셋팅함
        wifiListAdapter = new WifiListAdapter(this, wifiList, R.layout.list_wifi);
        guide_lv.setAdapter(wifiListAdapter);

        //ListView Item 클릭 시
        guide_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //선택된 wifi 정보를 뽑음
                Wifi wifi = (Wifi) wifiListAdapter.getItem(position);
                String capabilities = wifi.getCapabilities(); //보안 방식을 가져옴
                //보안이 걸려있다면 비밀번호 입력 팝업을 오픈
                if(capabilities.contains("WEP")||capabilities.contains("WPA")||capabilities.contains("WPA2")){
                    openPopup(wifi.getSsid(), wifi.getCapabilities());
                }else{
                    //보안이 없다면 바로 연결
                    Log.d(TAG, "onItemClick: wifi.getSsid() : "+ wifi.getSsid());
                    new guide_TCP_Client_Task().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,"s "+wifi.getSsid());
                }
            }
        });
    }

    //신호세기 재정렬
    static class Signal_AscCompare implements Comparator<Wifi> {
        @Override
        public int compare(Wifi arg0,Wifi arg1) {
            return Integer.valueOf(arg0.getSignal()) > Integer.valueOf(arg1.getSignal())? -1 : Integer.valueOf(arg0.getSignal()) <Integer.valueOf(arg1.getSignal())  ? 1:0;
        }
    }

    //보안 wifi 일 경우 팝업 오픈하여 비밀번호 입력하게 함
    public void openPopup(String ssid, String capabilities){
        //팝업 오픈
        Intent i = new Intent(getApplicationContext(), PopUpActivity.class);
        i.putExtra("className",Guide_wifiActivity.class.getSimpleName());
        i.putExtra("ssid", ssid);
        i.putExtra("capabilities", capabilities);
        i.putExtra("device_ssid",device.getSsid());
        i.putExtra("device_capabilities",device.getCapabilities());
        startActivityForResult(i,REQUEST_ACT);
    }


    //------------------------------------------- etc ----------------------------------------------------------
    private class adapter extends FragmentPagerAdapter {
        public adapter(FragmentManager fm) {
            super(fm);

        }
        @Override
        public Fragment getItem(int position) {
            if(position<0 || MAX_PAGE<=position)
                return null;
            switch (position){
                case 0:
                    cur_fragment=new page_1();
                    break;
                case 1:
                    cur_fragment=new page_2();
                    break;
                case 2:
                    cur_fragment=new page_4();
                    break;

            }
            return cur_fragment;
        }
        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }

}