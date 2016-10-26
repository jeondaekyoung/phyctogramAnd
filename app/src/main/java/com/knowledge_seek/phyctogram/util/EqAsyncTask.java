package com.knowledge_seek.phyctogram.util;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shj on 2016-05-13.
 */
public class EqAsyncTask extends AsyncTask<Object, Integer, Void> {
    // doInBackground 메소드가 실행되기 전에 실행되는 메소드
    @Override
    protected void onPreExecute() {
        // UI 작업을 수행하는 부분
        super.onPreExecute();
    }

    // 실제 비즈니스 로직이 처리될 메소드(Thread 부분이라고 생각하면 됨)
    @Override
    protected Void doInBackground(Object... params) {
        String ip = (String) params[0];
        String key = (String) params[1];
        String value = (String)params[2];
        Log.d("-진우-", "ip : " + ip + ", key : " + key + ", value : " + value);
        try {
            //url 생성
                    URL reqUrl = new URL("http://"+ip+"/"+key+"="+value);
            //connection open
            HttpURLConnection urlConn = (HttpURLConnection) reqUrl.openConnection();
            //set method type
            urlConn.setRequestMethod("GET");

            try {
                //현재 기기에서 수신 처리가 제대로 안되서 보내면서 오류 발생으로 인해
                //커넥션을 닫아주어야 해서 인셉션 다시 처리함
                int resCode = urlConn.getResponseCode();
            }catch (Exception e){
                Log.d("-진우-", "Exception1 : " );
                e.printStackTrace();

            }

            //close connect
            urlConn.disconnect();

            Log.d("-진우-", "urlConn.disconnect()");

            //기기에서 result 처리가 될 경우 아래 내용을 응용 필요
            /*if (resCode != HttpURLConnection.HTTP_OK) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String input;
            StringBuffer sb = new StringBuffer();

            while ((input = reader.readLine()) != null){
                sb.append(input);
            }*/
        }catch (Exception e){
            Log.d("-진우-", "Exception2 : " + e.getMessage());
        }
        return null;
    }

    // 모든 작업이 끝난 후 처리되는 메소드
    @Override
    protected void onPostExecute(Void result) {
        //Log.d("-진우-", "result : " + result.toString());
        super.onPostExecute(result);
    }
}
