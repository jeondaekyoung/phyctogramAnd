package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.domain.Height;
import com.knowledge_seek.phyctogram.domain.Member;
import com.knowledge_seek.phyctogram.domain.SqlCommntyListView;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.gcm.QuickstartPreferences;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity_webView;
import com.knowledge_seek.phyctogram.phyctogram.SaveSharedPreference;
import com.knowledge_seek.phyctogram.retrofitapi.MemberAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.retrofitapi.UsersAPI;
import com.knowledge_seek.phyctogram.util.Utility;
import com.pkmmte.view.CircularImageView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;

/**
 * Created by dkfka on 2015-11-25.
 */
public class MainActivity_webView extends BaseActivity_webView {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;           //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private RelativeLayout btn_users_record;                     //기록조회
    private TextView tv_users_name;                 //아이 이름 출력
    private RelativeLayout btn_users_analysis;               //분석리포트
    private CircularImageView iv_my_animal;                        //캐릭터
    private TextView tv_height;                         //최종신장
    private TextView tv_grow;                           //성장 값
    private TextView tv_rank;                           //상위
    private ImageView img_refresh; //리플래시 이미지

    //실시간 데이터 처리를 위한 webView
    private WebView webView;
    //자바스크립트 스레드 핸들러
    private final Handler jsHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("-진우-", "MainActivity.onCreate() 실행");

/*

        ic_screen = (LinearLayout) findViewById(R.id.tab1);
        LayoutInflater.from(this).inflate(R.layout.include_main, ic_screen, true);
*/

        //데이터셋팅
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            member = (Member) bundle.getSerializable("member");

            Log.d("-진우-", "MainActivity 에서 onCreate() : " + member.toString());

            switch (member.getJoin_route()){
                case "kakao":
                    memberName = member.getKakao_nickname();
                    break;
                case "facebook":
                    memberName = member.getFacebook_name();
                    break;
                default:
                    memberName = member.getName();
                    break;
            }

            if (QuickstartPreferences.token != null){
                Log.d("-진우-", "MainActivity member_seq: " + member.getMember_seq()+", Token: "+QuickstartPreferences.token);
                //push를 위해 토큰 정보를 저장
                RegisterTokenTask task = new RegisterTokenTask(member.getMember_seq(),QuickstartPreferences.token);
                task.execute();
            }else{
                Log.d("-진우-", "MainActivity - Token 발급 불가");
            }
        } else {
            //member = new Member();
            //Log.d("-진우-", "BaseActivity 에서 onCreate() : " + member.toString());
        }


    }//end onCreate
    //웹과 연결
    class AndroidBridge {
        @JavascriptInterface
        public void setHeight(final String height,final String rank,final String glow,final String img) {
            jsHandler.post(new Runnable() {
                public void run() {
                    tv_height.setText(height);
                    tv_rank.setText(rank);
                    tv_grow.setText(glow);

                    String imgName = "@drawable/" + img;
                    String packName = self.getPackageName();

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(imgName, "drawable", packName));

                    bitmap=bitmap.createScaledBitmap(bitmap,540,540, true);

                    iv_my_animal.setImageBitmap(bitmap);
                }
         });
        }

    }
    //private SendInitMessageThread sendInitMessageThread;
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "MainActivity.onResume() 실행");


        //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지,  내 아이 메인(분석)정보)
        /*MainDataTask task = new MainDataTask();
        task.execute(img_profile);
*/
        Log.d("-진우-", "MainActivity.onResume() 끝");
    }


    @Override
    protected void onStop() {
        super.onStop();
        //sendInitMessageThread.stopThread();
//        webView.loadUrl("javaScript:closeSocket()");
    }

    //메인페이지 초기 데이터조회(슬라이드 내 아이 목록, 계정이미지, 수다방인기Top3, 내 아이 메인(분석)정보)
    private class MainDataTask extends AsyncTask<Object, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity_webView.this);
        private List<Users> usersTask;
        private CircularImageView img_profileTask;
        private List<SqlCommntyListView> sqlCommntyListViewTask = null;

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected Bitmap doInBackground(Object... objects) {
            //Bitmap mBitmap = null;
            img_profileTask = (CircularImageView) objects[0];

            //슬라이드메뉴에 있는 내 아이 목록
            UsersAPI service = ServiceGenerator.createService(UsersAPI.class);
            Call<List<Users>> call = service.findUsersByMember(String.valueOf(member.getMember_seq()));
            try {
                usersTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 목록 가져오기 실패");
            }


            String image_url;
            if (member.getJoin_route().equals("kakao")) {
                image_url = member.getKakao_thumbnailimagepath();
                //이미지 불러오기
                InputStream in;
                try {
                    Log.d("-진우-", "이미지 주소 : " + image_url);
                    if(!image_url.equals("")) {
                        in = new URL(image_url).openStream();
                        memberImg = BitmapFactory.decodeStream(in);
                        in.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (member.getJoin_route().equals("facebook")) {
                image_url = "http://graph.facebook.com/" + member.getFacebook_id() + "/picture?type=large";
                //이미지 불러오기
                InputStream in;
                try {
                    //페이스북은 jpg파일이 링크 걸린 것이 아니다.
                    //http://graph.facebook.com/userid/picture?type=large
                    Log.d("-진우-", "이미지 주소 : " + image_url);
                    if(!image_url.equals("")) {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(image_url)
                                .build();
                        Response response = client.newCall(request).execute();
                        in = response.body().byteStream();
                        memberImg = BitmapFactory.decodeStream(in);
                        in.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return memberImg;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.d("-진우-", "이미지 읽어옴");
                img_profileTask.setImageBitmap(bitmap);
            }
            if (usersTask != null && usersTask.size() > 0) {
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());
                Utility.compareList(usersList, usersTask);

                if (nowUsers.getUser_seq() == 0) {
                    nowUsers = usersTask.get(0);
                }
                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
                tv_users_name.setText(nowUsers.getName());

                //현재 선택된 내 아이를 맨 뒤로 이동
                Utility.seqChange(usersList, nowUsers.getUser_seq());
                Log.d("-진우-", "순서 바꾼 후 내 아이 목록 : " + usersList.size());

                //내 아이 메인(분석) 정보 계산하기
                FindUsersMainInfoTask task = new FindUsersMainInfoTask();
                task.execute();
            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다.");

                if(SaveSharedPreference.getGuideFlag(getApplicationContext())){
                    Intent intent=new Intent(getApplicationContext(),GuideActivity.class);
                    startActivity(intent);
                }
            }

            dialog.dismiss();
            super.onPostExecute(bitmap);
        }
    }

    //내 아이 메인(분석) 정보 계산하기
    private class FindUsersMainInfoTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity_webView.this);
        private List<Height> heightTask = new ArrayList<>();

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected Void doInBackground(Void... params) {
            //내 아이 메인(분석)정보 계산하기
            UsersAPI service = ServiceGenerator.createService(UsersAPI.class, "Height");
            Call<List<Height>> call = service.findUsersMainInfoByUserSeq(nowUsers.getUser_seq());
            try {
                heightTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 메인(분석) 정보 실패");
            }
            return null;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("-진우-", heightTask.size() + " 개 조회(메인분석)");
            for (Height h : heightTask) {
                Log.d("-진우-", "최근신장 : " + h.toString());
            }
            //성장키 계산
            for (int i = 0; i < heightTask.size() - 1; i++) {
                heightTask.get(i).setGrow(String.format("%.1f", (heightTask.get(i).getHeight() - heightTask.get(i + 1).getHeight())));
            }

            if (heightTask.size() == 0) {
                //기록이 없으면 끝
                iv_my_animal.setImageResource(R.drawable.sample);
                tv_height.setText("-");
                tv_grow.setText("-");
                tv_rank.setText("-");
                dialog.dismiss();
                super.onPostExecute(aVoid);
                return;
            }

            //내 아이 이미지
            String imgName = "@drawable/" + heightTask.get(0).getAnimal_img().substring(0, 12);
            String packName = self.getPackageName();
            Log.d("-진우-", "확인 : " + imgName);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(imgName, "drawable", packName));

            bitmap=bitmap.createScaledBitmap(bitmap,540,540, true);

             iv_my_animal.setImageBitmap(bitmap);

            //iv_my_animal.setImageResource(getResources().getIdentifier(imgName, "drawable", packName));
            //최종신장
            tv_height.setText(String.format("%.1f", heightTask.get(0).getHeight()));
            //성장키
            if (heightTask.size() == 2) {
                if (Double.valueOf(heightTask.get(0).getGrow()) >= 0) {
                    tv_grow.setText("+" + heightTask.get(0).getGrow());
                }
            }
            //상위
            tv_rank.setText(String.valueOf(heightTask.get(0).getRank()));
            webView.loadUrl("http://www.phyctogram.com/height/webSocket.do?user_seq="+ heightTask.get(0).getUser_seq());
            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    //토큰 저장
    private class RegisterTokenTask extends AsyncTask<Void, Void, String>{

        private int memberSeq;
        private String token;

        public RegisterTokenTask(int memberSeq, String token) {
            this.memberSeq = memberSeq;
            this.token = token;
        }

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            Log.d("-진우-", "RegisterTokenTask onPreExecute");
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected String doInBackground(Void... params) {
            String result = null;

            MemberAPI service = ServiceGenerator.createService(MemberAPI.class);
            Call<String> call = service.registerToken(memberSeq, token);
            try {
                result = call.execute().body();
                Log.d("-진우-", "Token 저장 결과 : " + result);
            } catch (IOException e){
                Log.d("-진우-", "Token 저장 실패");
            }
            return result;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(String result) {

            if(result!=null&&result.equals("success")){
                Log.d("-진우-", "Token  저장에 성공하였습니다");
            } else {
                Log.d("-진우-", "Token 저장에 실패하였습니다");
            }
            super.onPostExecute(result);
        }
    }

//////////////////////////////////////////////////////////////////////
 /*
private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    reptView();
                    break;
            }
        }
    };
    public void reptView(){
        Log.d("-진우-", "reptView() 실행");
        //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지, 수다방인기Top3, 내 아이 메인(분석)정보)
        MainDataTask task = new MainDataTask();
        task.execute(img_profile);
    }*/
    /*//init send message thread
    class SendInitMessageThread extends Thread {
        private boolean isPlay = false;

        public SendInitMessageThread(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        @Override
        public void run() {
            super.run();
            while (isPlay) {
                Log.d("-진우-", "SendInitMessageThread");
                Message msg = mHandler.obtainMessage();
                msg.what = 3;
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(1000*30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

}