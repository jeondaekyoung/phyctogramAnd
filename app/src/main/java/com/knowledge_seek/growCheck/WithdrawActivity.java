package com.knowledge_seek.growCheck;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.knowledge_seek.growCheck.kakao.common.BaseActivity;
import com.knowledge_seek.growCheck.glowCheck.SaveSharedPreference;
import com.knowledge_seek.growCheck.retrofitapi.MemberAPI;
import com.knowledge_seek.growCheck.retrofitapi.ServiceGenerator;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;

import retrofit.Call;

/**
 * Created by dkfka on 2015-11-27.
 */
public class WithdrawActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private TextView tv_join_route;     //가입경로
    private TextView tv_name;           //이름
    private TextView tv_pw;
    private EditText et_pw;
    private TextView tv_pw1;
    private EditText et_pw1;
    private Button btn_withdraw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_withdraw, ic_screen, true);


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
        btn_left = (ImageButton) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });

        tv_join_route = (TextView) findViewById(R.id.tv_join_route);
        tv_name = (TextView) findViewById(R.id.tv_name);
        et_pw = (EditText) findViewById(R.id.et_pw);
        et_pw1 = (EditText) findViewById(R.id.et_pw1);
        btn_withdraw = (Button) findViewById(R.id.btn_withdraw);
        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String pw = et_pw.getText().toString();
                String pw1 = et_pw1.getText().toString();

                if(member.getJoin_route().equals("phyctogram")) {
                    if (!checkPW(pw, pw1)) {
                        return;
                    }
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(WithdrawActivity.this);
                dialog.setTitle(R.string.withdrawActivity_leaveTitle)
                        .setMessage(R.string.withdrawActivity_leaveAsk)
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능
                        .setPositiveButton(R.string.withdrawActivity_leave, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d("-진우-", "탈퇴하기 : " + member.getMember_seq() + ", " + pw + ", " + member.getJoin_route());

                                //멤버 관련 데이터 삭제(멤버, 아이등)
                                DeleteMemberTask task = new DeleteMemberTask(member.getMember_seq(), pw, member.getJoin_route());
                                task.execute();

                            }
                        })
                        .setNegativeButton(R.string.commonActivity_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();

            }
        });

        switch (member.getJoin_route()){
            case "kakao":
                tv_join_route.setText(R.string.withdrawActivity_kakaoName);
                tv_name.setText(member.getKakao_nickname());

                et_pw.setVisibility(View.GONE);

                et_pw1.setVisibility(View.GONE);

                break;
            case "facebook":
                tv_join_route.setText(R.string.withdrawActivity_facebookName);
                tv_name.setText(member.getFacebook_name());

                et_pw.setVisibility(View.GONE);

                et_pw1.setVisibility(View.GONE);
                break;
            default:
                tv_join_route.setText(R.string.withdrawActivity_growChkName);
                tv_name.setText(member.getName());
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "WithdrawActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());

        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이목록, 계정이미지)
        //WithdrawTask task = new WithdrawTask();
        //task.execute(img_profile);

       // Log.d("-진우-", "WithdrawActivity 에서 onResume() : " + member.toString());

        Log.d("-진우-", "WithdrawActivity.onResume() 끝");
    }


    //패스워드 입력 체크
    private boolean checkPW(String pw, String pw1) {
        //Log.d("-진우-", "pw :" + pw + ", pw1 : " + pw1);
        if (pw.length() <= 0 || pw1.length() <= 0) {
            Toast.makeText(getApplicationContext(), R.string.joinActivity_checkPW, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pw.equals(pw1)) {
            Toast.makeText(getApplicationContext(), R.string.joinActivity_checkPW, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //멤버삭제하기
    private class DeleteMemberTask extends AsyncTask<Void, Void, String>{

        private ProgressDialog dialog = new ProgressDialog(WithdrawActivity.this);
        private int member_seqTask;
        private String pwTask;
        private String join_routeTask;

        public DeleteMemberTask(int member_seqTask, String pwTask, String join_routeTask) {
            this.member_seqTask = member_seqTask;
            this.pwTask = pwTask;
            this.join_routeTask = join_routeTask;
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;

            //멤버 지우기
            MemberAPI service = ServiceGenerator.createService(MemberAPI.class);
            Call<String> call = service.withdrawMember(member_seqTask, pwTask, join_routeTask);
            try {
                result = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "멤버 삭제 실패");
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            switch (s){
                case "wrongPw":
                    Toast.makeText(getApplicationContext(), R.string.joinActivity_checkPW, Toast.LENGTH_SHORT).show();
                    break;
                case "fail":
                    Log.d("-진우-", "멤버 삭제 실패");
                    break;
                case "success":
                    Log.d("-진우-", "멤버 삭제 성공");
                    //로그아웃하기
                    switch (member.getJoin_route()){
                        case "kakao":
                            UserManagement.requestLogout(new LogoutResponseCallback() {
                                @Override
                                public void onCompleteLogout() {
                                    Log.d("-진우-", "카카오 로그아웃 실행");
                                    redirectLoginActivity();
                                }
                            });
                            break;
                        case "facebook":
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            //accessToken 값이 있다면 로그인 상태라고 판단
                            if (accessToken != null) {
                                Log.d("-진우-", "페이스북 로그아웃 실행");
                                LoginManager.getInstance().logOut();
                            }
                            redirectLoginActivity();
                            break;
                        default:
                            SaveSharedPreference.clearMemberSeq(getApplicationContext());
                            Log.d("-진우-", "픽토그램 로그아웃 실행");
                            redirectLoginActivity();
                            break;
                    }
                    break;
                default:
                    Log.d("-진우-", "멤버 삭제 중 에러가 발생하였습니다");
                    break;
            }
            dialog.dismiss();
            super.onPostExecute(s);
        }
    }
}