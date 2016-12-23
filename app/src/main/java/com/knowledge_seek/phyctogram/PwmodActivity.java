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
import android.widget.Toast;

import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.MemberAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.pkmmte.view.CircularImageView;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dkfka on 2015-11-25.
 */
public class PwmodActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private LinearLayout ic_screen;
    private ImageButton btn_left;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private LinearLayout ll_phyctogram;
    private LinearLayout ll_no_phyctogram;
    private TextView tv_join_route;                 //"카카오, 페이스북 가입자입니다"
    private EditText et_now_pw;
    private EditText et_pw;
    private EditText et_pw1;
    private Button btn_pw_mod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_pw_modify, ic_screen, true);

        //슬라이드 내 이미지, 셋팅
        img_profile = (CircularImageView) findViewById(R.id.img_profile);
        if (memberImg != null) {
            img_profile.setImageBitmap(memberImg);
        }

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
        btn_left = (ImageButton)findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });

        ll_phyctogram = (LinearLayout)findViewById(R.id.ll_phyctogram);
        ll_no_phyctogram = (LinearLayout)findViewById(R.id.ll_no_phyctogram);
        tv_join_route = (TextView)findViewById(R.id.tv_join_route);
        et_now_pw = (EditText)findViewById(R.id.et_now_pw);
        et_pw = (EditText)findViewById(R.id.et_pw);
        et_pw1 = (EditText)findViewById(R.id.et_pw1);
        btn_pw_mod = (Button)findViewById(R.id.btn_pw_mod);
        btn_pw_mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nowpw = et_now_pw.getText().toString();
                String newpw = et_pw.getText().toString();
                String pw1 = et_pw1.getText().toString();

                if(!checkpw(nowpw, newpw, pw1)){
                    return;
                }

                //Log.d("-진우-", "변경하기 클릭");
                MemberAPI service = ServiceGenerator.createService(MemberAPI.class);
                Call<String> call = service.modifyPwBymember(member.getMember_seq(), nowpw, newpw);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        String result = response.body();
                        switch (result){
                            case "wrongPw" :
                                Toast.makeText(getApplicationContext(), R.string.pwmodActivity_failPW, Toast.LENGTH_SHORT).show();
                            break;
                            case "fail" :
                                Toast.makeText(getApplicationContext(), R.string.pwmodActivity_failChangePW, Toast.LENGTH_SHORT).show();
                            break;
                            case "success" :
                                Toast.makeText(getApplicationContext(), R.string.pwmodActivity_successChangePW, Toast.LENGTH_SHORT).show();
                                finish();
                            break;

                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });
            }
        });

        switch (member.getJoin_route()){
            case "kakao":
                ll_phyctogram.setVisibility(View.GONE);
                ll_no_phyctogram.setVisibility(View.VISIBLE);
                btn_pw_mod.setVisibility(View.GONE);
                tv_join_route.setText(R.string.pwmodActivity_kakaoPW);
                break;
            case "facebook":
                ll_phyctogram.setVisibility(View.GONE);
                ll_no_phyctogram.setVisibility(View.VISIBLE);
                btn_pw_mod.setVisibility(View.GONE);
                tv_join_route.setText(R.string.pwmodActivity_facebookPW);
                break;
            default:
                ll_phyctogram.setVisibility(View.VISIBLE);
                ll_no_phyctogram.setVisibility(View.GONE);
                break;
        }

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

        lv_usersList.getLayoutParams().height  = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();


      //Log.d("-진우-", "PwmodActivity 에서 onResume() : " + member.toString());

        Log.d("-진우-", "PwmodActivity.onResume() 끝");
    }


    //패스워드체크
    private boolean checkpw(String nowpw, String newpw, String pw1) {
        //Log.d("-진우-", nowpw + ", " + newpw + ", " + pw1);
        if(nowpw.length() <= 0 || newpw.length() <= 0 || pw1.length() <= 0){
            Toast.makeText(getApplicationContext(), R.string.joinActivity_checkPW, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!newpw.equals(pw1)) {
            Toast.makeText(getApplicationContext(), R.string.pwmodActivity_checkChangePW, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}