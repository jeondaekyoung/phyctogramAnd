package com.knowledge_seek.phyctogram;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.pkmmte.view.CircularImageView;

import java.util.Locale;

/**
 * Created by dkfka on 2015-12-02.
 */
public class SettingActivity extends BaseActivity {

    //데이터정의

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private LinearLayout tv_notice;     //공지사항
    private LinearLayout tv_equip;      //내기기
    private LinearLayout tv_pwmod;      ///비밀번호 변경
    private LinearLayout tv_withdraw;       //회원탈퇴
    private LinearLayout tv_qa;             //문의하기
    private LinearLayout li_lan_en; //언어변경 (영문)
    private LinearLayout li_lan_ko; //언어변경 (한글)
    private LinearLayout li_lan_cn; //언어변경 (중문)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_setting, ic_screen, true);

        //슬라이드 내 이미지, 셋팅
        img_profile = (CircularImageView)findViewById(R.id.img_profile);
        if (memberImg != null) {
            img_profile.setImageBitmap(memberImg);
        }

        //슬라이드 내 이름, 셋팅
        tv_member_name = (TextView)findViewById(R.id.tv_member_name);
        if (memberName != null) {
            tv_member_name.setText(memberName);
        }

        //슬라이드 내 아이 목록(ListView)에서 아이 선택시
        lv_usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowUsers = (Users) usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' 아이를 선택하였습니다", Toast.LENGTH_LONG).show();

            }
        });
        //레이아웃 정의
        btn_left = (ImageButton)findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });

        //공지사항
        tv_notice = (LinearLayout)findViewById(R.id.tv_notice);
        tv_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(getApplicationContext(), "준비중입니다", Toast.LENGTH_LONG).show();*/
                Intent intent = new Intent(getApplicationContext(), NoticeListActivity.class);
                startActivity(intent);
            }
        });
        //내기기
        tv_equip = (LinearLayout)findViewById(R.id.tv_equip);
        tv_equip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "내기기", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), EquipmentActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        //비밀번호 변경
        tv_pwmod = (LinearLayout)findViewById(R.id.tv_pwmod);
        tv_pwmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "비밀번호 변경", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PwmodActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        //회원탈퇴
        tv_withdraw = (LinearLayout)findViewById(R.id.tv_withdraw);
        tv_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "회원탈퇴", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), WithdrawActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        //문의하기
        tv_qa = (LinearLayout)findViewById(R.id.tv_qa);
        tv_qa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "문의하기", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), QaListActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });

        //언어변경 (영문)
        li_lan_en = (LinearLayout)findViewById(R.id.li_lan_en);
        li_lan_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale en = Locale.US;
                Configuration config = new Configuration();
                config.locale = en;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                Intent intent = new Intent(SettingActivity.this , MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        //언어변경 (중문)
        li_lan_cn = (LinearLayout)findViewById(R.id.li_lan_cn);
        li_lan_cn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale cn = Locale.CHINA;
                Configuration config = new Configuration();
                config.locale = cn;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                Intent intent = new Intent(SettingActivity.this , MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //언어변경 (한글)
        li_lan_ko = (LinearLayout)findViewById(R.id.li_lan_ko);
        li_lan_ko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale ko = Locale.KOREAN;
                Configuration config = new Configuration();
                config.locale = ko;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
               Intent intent = new Intent(SettingActivity.this , MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "SettingActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height  = getListViewHeight(lv_usersList);
          usersListSlideAdapter.notifyDataSetChanged();

        Log.d("-진우-", "SettingActivity.onResume() 끝");
    }

}