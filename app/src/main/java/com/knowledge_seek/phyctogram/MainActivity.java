package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Height;
import com.knowledge_seek.phyctogram.domain.Member;
import com.knowledge_seek.phyctogram.domain.SqlCommntyListView;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.gcm.QuickstartPreferences;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
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
public class MainActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;           //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private RelativeLayout btn_users_record;                     //기록조회
    //private RelativeLayout rl_community_list;      //수다방 리스트
    private Button btn_community_list;              //수다방으로 가기
    private TextView tv_users_name;                 //아이 이름 출력
    private RelativeLayout btn_users_analysis;               //분석리포트
    private CircularImageView iv_my_animal;                        //캐릭터
    private TextView tv_height;                         //최종신장
    private TextView tv_grow;                           //성장 값
    private TextView tv_rank;                           //상위
    private ImageView img_refresh; //리플래시 이미지

    private TextView tv_popularTop1_title;          //수다방 인기 Top3
    //private TextView tv_popularTop1_name;
    //private TextView tv_popularTop2_title;
    //private TextView tv_popularTop2_name;
    //private TextView tv_popularTop3_title;
    //private TextView tv_popularTop3_name;
    private RelativeLayout rl_popularTop1;
    //private RelativeLayout rl_popularTop2;
    //private RelativeLayout rl_popularTop3;

    //private ScrollView sv_main;

    //실시간 데이터 처리를 위한 webView
    private WebView webView;
    //자바스크립트 스레드 핸들러
    private final Handler jsHandler = new Handler();


    //데이터정의
    private List<SqlCommntyListView> sqlCommntyListViewList = null;     //수다방 인기 Top3

    private final android.os.Handler mHandler = new android.os.Handler() {
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("-진우-", "MainActivity.onCreate() 실행");

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_main, ic_screen, true);

        //데이터셋팅
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            member = (Member) bundle.getSerializable("member");
            Log.d("-진우-", "MainActivity 에서 onCreate() : " + member.toString());
            if (member.getJoin_route().equals("kakao")) {
                memberName = member.getKakao_nickname();
            } else if (member.getJoin_route().equals("facebook")) {
                memberName = member.getFacebook_name();
            } else {
                memberName = member.getName();
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

        //슬라이드 내 이름
        tv_member_name = (TextView) findViewById(R.id.tv_member_name);
        //슬라이드 내 멤버 이름 셋팅
        if (memberName != null) {
            Log.d("-진우-", memberName);
            tv_member_name.setText(memberName);
        }

        //슬라이드 내 이미지
        img_profile = (CircularImageView) findViewById(R.id.img_profile);

        tv_users_name = (TextView) findViewById(R.id.tv_users_name);
        //슬라이드 내 아이 목록(ListView)에서 아이 선택시
        lv_usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowUsers = (Users) usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' "+getString(R.string.characterActivity_choiceChild), Toast.LENGTH_LONG).show();

                if (tv_users_name != null) {
                    tv_users_name.setText(nowUsers.getName());
                }
                //현재 선택된 내 아이를 맨 뒤로 이동
                Utility.seqChange(usersList, nowUsers.getUser_seq());
                //내 아이 목록 셋팅
                usersListSlideAdapter.setUsersList(usersList);
                usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
                usersListSlideAdapter.notifyDataSetChanged();

                //내 아이 메인(분석) 정보 계산하기
                FindUsersMainInfoTask task = new FindUsersMainInfoTask();
                task.execute();
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

        //캐릭터
        iv_my_animal = (CircularImageView) findViewById(R.id.iv_my_animal);
        iv_my_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        //기록조회
        btn_users_record = (RelativeLayout) findViewById(R.id.btn_users_record);
        btn_users_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersList == null || usersList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), R.string.diaryWriteActivity_registerChild, Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        //분석리포트
        btn_users_analysis = (RelativeLayout) findViewById(R.id.btn_users_analysis);
        btn_users_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersList == null || usersList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), R.string.diaryWriteActivity_registerChild, Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), UsersAnalysisActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        /*//수다방(community)
        btn_community_list = (Button) findViewById(R.id.btn_community_list);
        btn_community_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommunityListActivity.class);
                startActivity(intent);
            }
        });

        //커뮤니티(수다방) 인기 Top3
        tv_popularTop1_title = (TextView) findViewById(R.id.tv_popularTop1_title);
        //tv_popularTop1_name = (TextView) findViewById(R.id.tv_popularTop1_name);
        //tv_popularTop2_title = (TextView) findViewById(R.id.tv_popularTop2_title);
        //tv_popularTop2_name = (TextView) findViewById(R.id.tv_popularTop2_name);
        //tv_popularTop3_title = (TextView) findViewById(R.id.tv_popularTop3_title);
        //tv_popularTop3_name = (TextView) findViewById(R.id.tv_popularTop3_name);
        rl_popularTop1 = (RelativeLayout) findViewById(R.id.rl_popularTop1);
        rl_popularTop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Top1으로 가기", Toast.LENGTH_SHORT).show();
                if (sqlCommntyListViewList != null && sqlCommntyListViewList.size() >= 1) {
                    //Log.d("-진우-", sqlCommntyListViewList.size() + "개, " + sqlCommntyListViewList.get(0));
                    Intent intent = new Intent(getApplicationContext(), CommunityViewActivity.class);
                    //intent.putExtra("member", member);
                    intent.putExtra("sqlCommntyListView", sqlCommntyListViewList.get(0));
                    //intent.putExtra("goMain", true);
                    startActivity(intent);
                    //finish();
                }
            }
        });*/
        /*rl_popularTop2 = (RelativeLayout) findViewById(R.id.rl_popularTop2);
        rl_popularTop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Top2으로 가기", Toast.LENGTH_SHORT).show();
                if (sqlCommntyListViewList != null && sqlCommntyListViewList.size() >= 2) {
                    //Log.d("-진우-", sqlCommntyListViewList.size() + "개, " + sqlCommntyListViewList.get(1));
                    Intent intent = new Intent(getApplicationContext(), CommunityViewActivity.class);
                    //intent.putExtra("member", member);
                    intent.putExtra("sqlCommntyListView", sqlCommntyListViewList.get(1));
                    //intent.putExtra("goMain", true);
                    startActivity(intent);
                    //finish();
                }
            }
        });*/
        /*rl_popularTop3 = (RelativeLayout) findViewById(R.id.rl_popularTop3);
        rl_popularTop3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Top3으로 가기", Toast.LENGTH_SHORT).show();
                if (sqlCommntyListViewList != null && sqlCommntyListViewList.size() >= 3) {
                    //Log.d("-진우-", sqlCommntyListViewList.size() + "개, " + sqlCommntyListViewList.get(2));
                    Intent intent = new Intent(getApplicationContext(), CommunityViewActivity.class);
                    //intent.putExtra("member", member);
                    intent.putExtra("sqlCommntyListView", sqlCommntyListViewList.get(2));
                    //intent.putExtra("goMain", true);
                    startActivity(intent);
                    //finish();
                }
            }
        });*/


        //내 아이 메인(분석) 정보
        tv_height = (TextView) findViewById(R.id.tv_height);
        tv_grow = (TextView) findViewById(R.id.tv_grow);
        tv_rank = (TextView) findViewById(R.id.tv_rank);

        //sv_main = (ScrollView)findViewById(R.id.sv_main);

        img_refresh = (ImageView) findViewById(R.id.img_refresh);
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지, 수다방인기Top3, 내 아이 메인(분석)정보)
                MainDataTask task = new MainDataTask();
                task.execute(img_profile);
            }
        });
        //웹뷰 세팅
        webView = (WebView)findViewById(R.id.webView);
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        webView.addJavascriptInterface(new AndroidBridge() ,"AppJs");
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

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지, 수다방인기Top3, 내 아이 메인(분석)정보)
        MainDataTask task = new MainDataTask();
        task.execute(img_profile);

        /*sendInitMessageThread = new SendInitMessageThread(true);
        sendInitMessageThread.start();*/

        Log.d("-진우-", "MainActivity 에 onResume() : " + member.toString());
        Log.d("-진우-", "MainActivity.onResume() 끝");
    }

    //init send message thread
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        //sendInitMessageThread.stopThread();
        webView.loadUrl("javaScript:closeSocket()");
    }

    //메인페이지 초기 데이터조회(슬라이드 내 아이 목록, 계정이미지, 수다방인기Top3, 내 아이 메인(분석)정보)
    private class MainDataTask extends AsyncTask<Object, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
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


            String image_url = null;
            if (member.getJoin_route().equals("kakao")) {
                image_url = member.getKakao_thumbnailimagepath();
                //이미지 불러오기
                InputStream in = null;
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
                InputStream in = null;
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

            //커뮤니티(수다방) 인기 Top3 읽어오기
            /*SqlCommntyListViewAPI service1 = ServiceGenerator.createService(SqlCommntyListViewAPI.class, "SqlCommntyListView");
            Call<List<SqlCommntyListView>> call1 = service1.findCommntyPopularTop3();
            try {
                List<SqlCommntyListView> resultList1 = call1.execute().body();
                if (resultList1 != null) {
                    for (SqlCommntyListView s : resultList1) {
                        Log.d("-진우-", "인기 Top3 : " + s.toString());
                    }
                    sqlCommntyListViewTask = resultList1;

                }
            } catch (IOException e) {
                Log.d("-진우-", "수다방 목록 조회 실패");
            }*/

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
                usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
                usersListSlideAdapter.setUsersList(usersList);
                lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
                usersListSlideAdapter.notifyDataSetChanged();

                //내 아이 메인(분석) 정보 계산하기
                FindUsersMainInfoTask task = new FindUsersMainInfoTask();
                task.execute();
            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다.");
                //가이드 유무
                SharedPreferences preferences = getSharedPreferences("preferences",MODE_PRIVATE);
                SharedPreferences.Editor editor =preferences.edit();
                editor.putBoolean("guideNeed",true);
                editor.commit();
                boolean guideFlag=preferences.getBoolean("guideNeed",true);

                if(guideFlag){
                    Intent intent=new Intent(getApplicationContext(),GuideActivity.class);
                    startActivity(intent);
                }
            }



            //커뮤니티(수다방) 인기 Top3 셋팅
            /*if (sqlCommntyListViewTask != null) {
                sqlCommntyListViewList = sqlCommntyListViewTask;        //main변수에 저장
            }
            if(sqlCommntyListViewTask != null && sqlCommntyListViewTask.size() > 0){
                tv_popularTop1_title.setText(sqlCommntyListViewTask.get(0).getTitle());
            }*/
            /*if (sqlCommntyListViewTask != null && sqlCommntyListViewTask.size() >= 3) {
                tv_popularTop1_title.setText(sqlCommntyListViewTask.get(0).getTitle());
                tv_popularTop1_name.setText(sqlCommntyListViewTask.get(0).getName());
                tv_popularTop2_title.setText(sqlCommntyListViewTask.get(1).getTitle());
                tv_popularTop2_name.setText(sqlCommntyListViewTask.get(1).getName());
                tv_popularTop3_title.setText(sqlCommntyListViewTask.get(2).getTitle());
                tv_popularTop3_name.setText(sqlCommntyListViewTask.get(2).getName());
            } else if (sqlCommntyListViewTask != null && sqlCommntyListViewTask.size() == 2) {
                tv_popularTop1_title.setText(sqlCommntyListViewTask.get(0).getTitle());
                tv_popularTop1_name.setText(sqlCommntyListViewTask.get(0).getName());
                tv_popularTop2_title.setText(sqlCommntyListViewTask.get(1).getTitle());
                tv_popularTop2_name.setText(sqlCommntyListViewTask.get(1).getName());
                tv_popularTop3_title.setText(null);
                tv_popularTop3_name.setText(null);
            } else if (sqlCommntyListViewTask != null && sqlCommntyListViewTask.size() == 1) {
                tv_popularTop1_title.setText(sqlCommntyListViewTask.get(0).getTitle());
                tv_popularTop1_name.setText(sqlCommntyListViewTask.get(0).getName());
                tv_popularTop2_title.setText(null);
                tv_popularTop2_name.setText(null);
                tv_popularTop3_title.setText(null);
                tv_popularTop3_name.setText(null);
            } else {
                tv_popularTop1_title.setText(null);
                tv_popularTop1_name.setText(null);
                tv_popularTop2_title.setText(null);
                tv_popularTop2_name.setText(null);
                tv_popularTop3_title.setText(null);
                tv_popularTop3_name.setText(null);
            }*/

            dialog.dismiss();
            super.onPostExecute(bitmap);
        }
    }

    //내 아이 메인(분석) 정보 계산하기
    private class FindUsersMainInfoTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private List<Height> heightTask = new ArrayList<Height>();

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


}