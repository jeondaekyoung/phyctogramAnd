package com.knowledge_seek.phyctogram.kakao.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.knowledge_seek.phyctogram.LoginActivity;
import com.knowledge_seek.phyctogram.R;
import com.knowledge_seek.phyctogram.domain.Member;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.gcm.MyRegistrationIntentService;
import com.knowledge_seek.phyctogram.gcm.QuickstartPreferences;
import com.knowledge_seek.phyctogram.kakao.common.widget.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjw on 2015-11-26.
 */
public class BaseActivity_webView extends Activity {

    protected static Activity self;

    //데이터정의
    public static Member member = null;                 //멤버
    public static List<Users> usersList = new ArrayList<>();           //내 아이 목록
    public static Users nowUsers = new Users();                                        //메인유저
    public static String memberName = null;                                 //슬라이드 멤버 이름
    public static Bitmap memberImg = null;                                  //슬라이드 멤버 이미지

    //제스처
    //private GestureDetector mGestures = null;

    long backKeyPressedTime = 0;

    //GCM
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //키보드 숨기기
    private InputMethodManager imm;


    BottomNavigationView bottom_navigation;

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, MyRegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action){
                    case QuickstartPreferences.REGISTRATION_READY :
                        // 액션이 READY일 경우
                        Log.d("-진우-", "ACTION READY");
                    break;
                    case QuickstartPreferences.REGISTRATION_GENERATING :
                        // 액션이 GENERATING일 경우
                        Log.d("-진우-", "ACTION GENERATING");
                        break;
                    case QuickstartPreferences.REGISTRATION_COMPLETE :
                        // 액션이 COMPLETE일 경우
                        String token = intent.getStringExtra("token");
                        QuickstartPreferences.token = token;
                        Log.d("-진우-", "ACTION COMPLETE : "+token);
                }

            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("-진우-", "BaseActivity.onCreate() 실행");
        setContentView(R.layout.activity_base_webview);

        bottom_navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_dashBoard:
                        Log.d("-대경-", "onNavigationItemSelected:  대쉬보드");
                        return true;
                    case R.id.action_album:
                        Log.d("-대경-", "onNavigationItemSelected:  앨범");
                        return true;
                    case R.id.action_settings:
                        Log.d("-대경-", "onNavigationItemSelected:  세팅");
                        return true;
                }
                return false;
            }
        });

         }//end create

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "BaseActivity.onResume() 실행");
        if(QuickstartPreferences.token == null)
            getToken();

        GlobalApplication.setCurrentActivity(this);
        self = BaseActivity_webView.this;
        setStatusBarColor(self,R.color.purpledk);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    }

    public void getToken(){
        registBroadcastReceiver();
        //앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        getInstanceIdToken();
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    //백버튼 클릭시
    @Override
    public void onBackPressed() {
        Activity nowActivity = GlobalApplication.getCurrentActivity();
        Log.d("-진우-", "지금 실행중인 액티비티 : " + (nowActivity != null ? nowActivity.getClass().getSimpleName() : ""));
        Log.d("-진우-", "시간 : " + backKeyPressedTime);

        Intent intent = null;
        if( nowActivity.getClass().getSimpleName().equals("LoginActivity_webView")){
            Log.d("-대경-", "onBackPressed: "+"LoginActivity_webView 의 뒤로가기");
           return;
        }
        if (nowActivity != null && nowActivity.getClass().getSimpleName().equals("MainActivity")) { //뒤로가기 테스트
            //두번 클릭시 종료
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();

                Toast.makeText(getApplicationContext(), R.string.baseActivity_exit, Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                //moveTaskToBack(true);
                finish();
              //android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
        else {
            super.onBackPressed();
        }
    }


    ///////////메소드

    private void clearReferences() {
        Activity currActivity = GlobalApplication.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this)) {
            GlobalApplication.setCurrentActivity(null);
        }
    }

    protected static void showWaitingDialog() {
        WaitingDialog.showWaitingDialog(self);
    }

    protected static void cancelWaitingDialog() {
        WaitingDialog.cancelWaitingDialog();
    }

    protected void redirectLoginActivity() {
        //데이터초기화
        member = null;                                       //멤버
        usersList.clear();                                      //내 아이 목록
        nowUsers = new Users();                                      //메인유저
        memberName = null;                                //슬라이드 멤버 이름
        memberImg = null;                                  //슬라이드 멤버 이미지
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //finish();
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("-진우-", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }



    /*
    * 리스트뷰의 높이를 구함
    * */
    public static int getListViewHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        int listViewHeight;
        listView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        listViewHeight = listView.getMeasuredHeight() * adapter.getCount() + (adapter.getCount() * listView.getDividerHeight());
        return listViewHeight;
    }

    //키보드 제어
    public void linearOnClick(View v) {
        Log.d("linearOnClick: ","실행");
        View view = this.getCurrentFocus();
        if (view != null) {
            imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
    //스테이터스바 색 변경
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(color);

        }
    }



}
