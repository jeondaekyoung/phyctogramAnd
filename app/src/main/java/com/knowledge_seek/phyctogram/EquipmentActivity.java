package com.knowledge_seek.phyctogram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.pkmmte.view.CircularImageView;

/*
 * Created by dkfka on 2015-12-02.
 */
public class EquipmentActivity extends BaseActivity {
    final String TAG = EquipmentActivity.class.getName();

    //레이아웃정의 - 슬라이드메뉴
    private LinearLayout ic_screen;
    private ImageButton btn_left;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름
    private Button btn_test_webView,guide_show,wifi_setting,req_setting;


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



        guide_show =(Button) findViewById(R.id.guide_show);
        guide_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),GuideActivity.class);
                startActivity(intent);
            }
        });
        wifi_setting =(Button) findViewById(R.id.wifi_setting);
        wifi_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Guide_wifiActivity.class);
                startActivity(intent);
            }
        });
        req_setting =(Button) findViewById(R.id.req_setting);
        req_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Guide_reqActivity.class);
                startActivity(intent);
            }
        });
        btn_test_webView = (Button) findViewById(R.id.btn_test_webView1);
        btn_test_webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity_webView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        btn_test_webView = (Button) findViewById(R.id.btn_test_webView2);
        btn_test_webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity_webView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

//        Log.d("-진우-", "EquipmentActivity 에서 onResume() : " + member.toString());

        Log.d("-진우-", "EquipmentActivity.onResume() 끝");

    }



}