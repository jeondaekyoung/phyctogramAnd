package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.MemberAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.pkmmte.view.CircularImageView;

import java.util.List;

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
                        if("wrongPw".equals(result)){
                            Toast.makeText(getApplicationContext(), R.string.pwmodActivity_failPW, Toast.LENGTH_SHORT).show();
                        } else if("fail".equals(result)){
                            Toast.makeText(getApplicationContext(), R.string.pwmodActivity_failChangePW, Toast.LENGTH_SHORT).show();
                        } else if("success".equals(result)){
                            Toast.makeText(getApplicationContext(), R.string.pwmodActivity_successChangePW, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });
            }
        });

        if (member.getJoin_route().equals("kakao")) {
            ll_phyctogram.setVisibility(View.GONE);
            ll_no_phyctogram.setVisibility(View.VISIBLE);
            btn_pw_mod.setVisibility(View.GONE);
            tv_join_route.setText(R.string.pwmodActivity_kakaoPW);
        } else if (member.getJoin_route().equals("facebook")) {
            ll_phyctogram.setVisibility(View.GONE);
            ll_no_phyctogram.setVisibility(View.VISIBLE);
            btn_pw_mod.setVisibility(View.GONE);
            tv_join_route.setText(R.string.pwmodActivity_facebookPW);
        } else {
            ll_phyctogram.setVisibility(View.VISIBLE);
            ll_no_phyctogram.setVisibility(View.GONE);
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

        //슬라이드메뉴 셋팅(내 아이목록, 계정이미지)
        //PwmodTask task = new PwmodTask();
        //task.execute(img_profile);

        Log.d("-진우-", "PwmodActivity 에서 onResume() : " + member.toString());

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



    //탈퇴페이지 초기 데이터조회(슬라이드 내 아이 목록, 계정이미지)
    private class PwmodTask extends AsyncTask<Object, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(PwmodActivity.this);
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
}