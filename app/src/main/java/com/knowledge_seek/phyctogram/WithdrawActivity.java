package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.phyctogram.SaveSharedPreference;
import com.knowledge_seek.phyctogram.retrofitapi.MemberAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.List;

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

        if (member.getJoin_route().equals("kakao")) {
            tv_join_route.setText(R.string.withdrawActivity_kakaoName);
            tv_name.setText(member.getKakao_nickname());

            et_pw.setVisibility(View.GONE);

            et_pw1.setVisibility(View.GONE);
        } else if (member.getJoin_route().equals("facebook")) {
            tv_join_route.setText(R.string.withdrawActivity_facebookName);
            tv_name.setText(member.getFacebook_name());

            et_pw.setVisibility(View.GONE);

            et_pw1.setVisibility(View.GONE);
        } else {
            tv_join_route.setText(R.string.withdrawActivity_pyhtogramName);
            tv_name.setText(member.getName());
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
        int height = getListViewHeight(lv_usersList);
        lv_usersList.getLayoutParams().height = height;
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이목록, 계정이미지)
        //WithdrawTask task = new WithdrawTask();
        //task.execute(img_profile);

        Log.d("-진우-", "WithdrawActivity 에서 onResume() : " + member.toString());

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


    //탈퇴페이지 초기 데이터조회(슬라이드 내 아이 목록, 계정이미지)
    private class WithdrawTask extends AsyncTask<Object, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(WithdrawActivity.this);
        private List<Users> usersTask;
        private CircularImageView img_profileTask;

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            img_profileTask = (CircularImageView) params[0];

            //슬라이드메뉴에 있는 내 아이 목록
            /*UsersAPI service = ServiceGenerator.createService(UsersAPI.class, "Users");
            Call<List<Users>> call = service.findUsersByMember(String.valueOf(member.getMember_seq()));
            try {
                usersTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 목록 가져오기 실패");
            }*/

            /*String image_url = null;
            if (member.getJoin_route().equals("kakao")) {
                image_url = member.getKakao_thumbnailimagepath();
                //이미지 불러오기
                InputStream in = null;
                try {
                    Log.d("-진우-", "이미지 주소 : " + image_url);
                    in = new URL(image_url).openStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                    in.close();
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

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(image_url)
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    in = response.body().byteStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*if (bitmap != null) {
                Log.d("-진우-", "이미지읽어옴");
                img_profileTask.setImageBitmap(bitmap);
            }

            if (usersTask != null && usersTask.size() > 0) {
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());
                for (Users u : usersTask) {
                    Log.d("-진우-", "내 아이 : " + u.toString());
                }
                usersList = usersTask;

                usersListSlideAdapter.setUsersList(usersList);
                if (nowUsers == null) {
                    nowUsers = usersTask.get(0);
                }
                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다");
            }

            int height = getListViewHeight(lv_usersList);
            lv_usersList.getLayoutParams().height = height;
            usersListSlideAdapter.notifyDataSetChanged();*/

            dialog.dismiss();
            super.onPostExecute(bitmap);
        }
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
            if("wrongPw".equals(s)){
                Toast.makeText(getApplicationContext(), R.string.joinActivity_checkPW, Toast.LENGTH_SHORT).show();
            } else if("fail".equals(s)) {
                Log.d("-진우-", "멤버 삭제 실패");
            } else if("success".equals(s)) {

                Log.d("-진우-", "멤버 삭제 성공");
                //로그아웃하기
                if(member.getJoin_route().equals("facebook")){
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    //accessToken 값이 있다면 로그인 상태라고 판단
                    if (accessToken != null) {
                        Log.d("-진우-", "페이스북 로그아웃 실행");
                        LoginManager.getInstance().logOut();
                    }
                    redirectLoginActivity();
                } else if(member.getJoin_route().equals("kakao")){
                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Log.d("-진우-", "카카오 로그아웃 실행");
                            redirectLoginActivity();
                        }
                    });
                } else if(member.getJoin_route().equals("phyctogram")){
                    SaveSharedPreference.clearMemberSeq(getApplicationContext());
                    Log.d("-진우-", "픽토그램 로그아웃 실행");
                    redirectLoginActivity();
                }

            } else {
                Log.d("-진우-", "멤버 삭제 중 에러가 발생하였습니다");
            }

            dialog.dismiss();
            super.onPostExecute(s);
        }
    }
}