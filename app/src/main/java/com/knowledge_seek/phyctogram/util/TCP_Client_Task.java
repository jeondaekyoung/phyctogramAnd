package com.knowledge_seek.phyctogram.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.EquipmentActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjw on 2016-11-08.
 */
public class TCP_Client_Task extends AsyncTask <Object, Integer, ArrayList<String>> {

    protected static	String SERV_IP		=	"192.168.4.1"; //server ip
    protected static	int		  PORT		=	80;

    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    ArrayList<String> response ;
    private WifiManager mWm;
    private Context mContext;

    public TCP_Client_Task (Context context,WifiManager wm){
        mContext = context;
        mWm= wm;

    }
    ProgressDialog dialog;


    //Background 작업 시작전에 UI 작업을 진행 한다.
    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(mContext.getString(com.knowledge_seek.phyctogram.R.string.commonActivity_wait));
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String> doInBackground(Object... params) {


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
        boolean connection = false;

        if (networkId != -1) {
            //Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.equipmentActivity_connectionAlert, Toast.LENGTH_LONG).show();
            //해당 networkId로 wifi를 연결함
            connection = mWm.enableNetwork(networkId, true); //연결이 되면 true를 반환
            Log.d("-진우-", "connection : " + connection);
        }
        String  command = (String) params[0];

        try {
            Log.d("TCP","server connecting");

 /*           InetSocketAddress socketAddress  = new InetSocketAddress (InetAddress.getByName(SERV_IP), PORT);*/
            Socket socket = new Socket(SERV_IP,PORT);
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));

            WriteSocket(networkWriter,command);

                PrintWriter out = new PrintWriter(networkWriter, true);
                out.flush();

               String  line;
            response = new ArrayList();
            //디버깅을위한 list(0) command로
            response.add(command);
            if(command.equals("w")){
                int i =0;
                while (true){
                    line = networkReader.readLine();
                    Log.d("line:",line);
                    response.add(line);
                    i++;
                    if(i==Integer.valueOf(response.get(1).substring(response.get(1).indexOf(":"),response.get(1).length()))){
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


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch(IOException	e){
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000);
            Log.d( "thread.sleep ","5초");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }
    //Background 작업이 끝난 후 UI 작업을 진행 한다.
    @Override
    protected void onPostExecute(ArrayList<String>  response) {

        Toast.makeText(mContext,"명령어: "+response.get(0)+"response 1 Line:"+response.get(1) , Toast.LENGTH_LONG).show();


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

