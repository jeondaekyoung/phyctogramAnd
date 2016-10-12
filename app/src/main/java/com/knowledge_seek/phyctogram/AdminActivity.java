package com.knowledge_seek.phyctogram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.util.EqAsyncTask;

/**
 * Created by dkfka on 2015-11-25.
 */
public class AdminActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private LinearLayout ic_screen;
    private ImageButton btn_left;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private EditText et_ref;
    private EditText et_adj;
    private EditText et_useq;
    private Button btn_ref, btn_adj, btn_useq, btn_end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.ic_screen);
        LayoutInflater.from(this).inflate(com.knowledge_seek.phyctogram.R.layout.include_admin, ic_screen, true);

        //슬라이드 내 이미지, 셋팅
        img_profile = (CircularImageView) findViewById(com.knowledge_seek.phyctogram.R.id.img_profile);
        if (memberImg != null) {
            img_profile.setImageBitmap(memberImg);
        }

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
        btn_left = (ImageButton)findViewById(com.knowledge_seek.phyctogram.R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });

        et_ref = (EditText) findViewById(com.knowledge_seek.phyctogram.R.id.et_ref);
        et_adj = (EditText) findViewById(com.knowledge_seek.phyctogram.R.id.et_adj);
        et_useq = (EditText) findViewById(com.knowledge_seek.phyctogram.R.id.et_useq);

        btn_ref = (Button) findViewById(com.knowledge_seek.phyctogram.R.id.btn_ref);
        btn_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EqAsyncTask().execute("192.168.4.1:80", "?REF", et_ref.getText() + "**");

            }
        });
        btn_adj = (Button) findViewById(com.knowledge_seek.phyctogram.R.id.btn_adj);
        btn_adj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EqAsyncTask().execute("192.168.4.1:80", "?ADJ", et_adj.getText() + "**");
            }
        });
        btn_useq = (Button) findViewById(com.knowledge_seek.phyctogram.R.id.btn_useq);
        btn_useq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EqAsyncTask().execute("192.168.4.1:80", "?USEQ", et_useq.getText() + "**");
            }
        });
        btn_end = (Button) findViewById(com.knowledge_seek.phyctogram.R.id.btn_end);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EqAsyncTask().execute("192.168.4.1:80", "?END_SERVER", "END_SERVER");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "PwmodActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        int height = getListViewHeight(lv_usersList);
        lv_usersList.getLayoutParams().height = height;
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이목록, 계정이미지)
        //PwmodTask task = new PwmodTask();
        //task.execute(img_profile);

        Log.d("-진우-", "PwmodActivity 에서 onResume() : " + member.toString());

        Log.d("-진우-", "PwmodActivity.onResume() 끝");
    }
}