package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import java.util.List;
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
        ic_screen = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.ic_screen);
        LayoutInflater.from(this).inflate(com.knowledge_seek.phyctogram.R.layout.include_setting, ic_screen, true);

        //슬라이드 내 이미지, 셋팅
        img_profile = (CircularImageView)findViewById(com.knowledge_seek.phyctogram.R.id.img_profile);
        if (memberImg != null) {
            img_profile.setImageBitmap(memberImg);
        }

        //슬라이드 내 이름, 셋팅
        tv_member_name = (TextView)findViewById(com.knowledge_seek.phyctogram.R.id.tv_member_name);
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
        btn_left = (ImageButton)findViewById(com.knowledge_seek.phyctogram.R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });

        /*View view1 = findViewById(R.id.ripple01);
        MaterialRippleLayout.on(view1)
                .rippleColor(Color.parseColor("#C0AAE1"))
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .create();

        View view2 = findViewById(R.id.ripple02);
        MaterialRippleLayout.on(view2)
                .rippleColor(Color.parseColor("#C0AAE1"))
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .create();

        View view3 = findViewById(R.id.ripple03);
        MaterialRippleLayout.on(view3)
                .rippleColor(Color.parseColor("#C0AAE1"))
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .create();

        View view4 = findViewById(R.id.ripple04);
        MaterialRippleLayout.on(view4)
                .rippleColor(Color.parseColor("#C0AAE1"))
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .create();

        View view5 = findViewById(R.id.ripple05);
        MaterialRippleLayout.on(view5)
                .rippleColor(Color.parseColor("#C0AAE1"))
                .rippleAlpha(0.2f)
                .rippleHover(true)
                .create();
*/
        //공지사항
        tv_notice = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.tv_notice);
        tv_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(getApplicationContext(), "준비중입니다", Toast.LENGTH_LONG).show();*/
                Intent intent = new Intent(getApplicationContext(), NoticeListActivity.class);
                startActivity(intent);
            }
        });
        //내기기
        tv_equip = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.tv_equip);
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
        tv_pwmod = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.tv_pwmod);
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
        tv_withdraw = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.tv_withdraw);
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
        tv_qa = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.tv_qa);
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
        li_lan_en = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.li_lan_en);
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
        li_lan_cn = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.li_lan_cn);
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
        li_lan_ko = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.li_lan_ko);
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

        //슬라이드메뉴 셋팅(내 아이목록, 계정이름, 계정이미지)
        //SettingTask task = new SettingTask();
        //task.execute(img_profile);

        Log.d("-진우-", "SettingActivity 에서 onResume() : " + member.toString());

        Log.d("-진우-", "SettingActivity.onResume() 끝");
    }

    //설정페이지 초기 데이터조회(슬라이드 내 아이 목록, 계정이름, 계정이미지)
    private class SettingTask extends AsyncTask<Object, Void, Bitmap>{

        private ProgressDialog dialog = new ProgressDialog(SettingActivity.this);
        private List<Users> usersTask;
        private CircularImageView img_profileTask;

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(com.knowledge_seek.phyctogram.R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            img_profileTask = (CircularImageView)params[0];

            //슬라이드메뉴에 있는 내 아이 목록
            /*UsersAPI service = ServiceGenerator.createService(UsersAPI.class, "Users");
            Call<List<Users>> call = service.findUsersByMember(String.valueOf(member.getMember_seq()));
            try {
                usersTask = call.execute().body();
            } catch (IOException e){
                Log.d("-진우-", "내 아이 목록 가져오기 실패");
            }*/

            /*String image_url = null;
            if(member.getJoin_route().equals("kakao")){
                image_url = member.getKakao_thumbnailimagepath();
                //이미지 불러오기
                InputStream in = null;
                try {
                    Log.d("-진우-", "이미지 주소 : " + image_url);
                    in = new URL(image_url).openStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else if(member.getJoin_route().equals("facebook")){
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
                } catch (Exception e){
                    e.printStackTrace();
                }
            }*/
            return mBitmap;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*if(bitmap != null){
                Log.d("-진우-", "이미지읽어옴");
                img_profileTask.setImageBitmap(bitmap);
            }

            if(usersTask != null && usersTask.size() > 0){
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());
                for(Users u : usersTask){
                    Log.d("-진우-", "내 아이 : " + u.toString());
                }
                usersList = usersTask;

                usersListSlideAdapter.setUsersList(usersList);
                if(nowUsers == null){
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