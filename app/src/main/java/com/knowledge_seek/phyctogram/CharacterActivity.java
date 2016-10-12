package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;

import java.io.IOException;

import com.knowledge_seek.phyctogram.domain.Analysis;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.AnalysisAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.util.Utility;
import retrofit.Call;

/**
 * Created by dkfka on 2015-12-08.
 */
public class CharacterActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드 메뉴
    private LinearLayout ic_screen;
    private ImageButton btn_left;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(com.knowledge_seek.phyctogram.R.id.ic_screen);
        LayoutInflater.from(this).inflate(com.knowledge_seek.phyctogram.R.layout.include_mycharacter, ic_screen, true);

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
                nowUsers = (Users) usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' "+ com.knowledge_seek.phyctogram.R.string.characterActivity_choiceChild, Toast.LENGTH_LONG).show();

                //현재 선택된 내 아이를 맨 뒤로 이동
                Utility.seqChange(usersList, nowUsers.getUser_seq());
                //lv_usersList.setBackgroundColor(getResources().getColor(R.color.yellow));

                //내 아이 목록 셋팅
                usersListSlideAdapter.setUsersList(usersList);
                usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
                usersListSlideAdapter.notifyDataSetChanged();

                //아이 선택시 아이에 맞는 캐릭터를 불러오기
                FindMonthNumAnimalTask task = new FindMonthNumAnimalTask();
                task.execute();
            }
        });
        //레이아웃 정의
        btn_left = (ImageButton) findViewById(com.knowledge_seek.phyctogram.R.id.btn_left);
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
        Log.d("-진우-", "CharacterActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        int height = getListViewHeight(lv_usersList);
        lv_usersList.getLayoutParams().height = height;
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지, 개월수, 캐릭터이미지)
        /*CharacterTask task = new CharacterTask();
        task.execute(img_profile);*/

        //아이의 개월수와 캐릭터 이미지 가져오기
        FindMonthNumAnimalTask task = new FindMonthNumAnimalTask();
        task.execute();

        Log.d("-진우-", "CharacterActivity 에 onResume() : " + member.toString());

        Log.d("-진우-", "CharacterActivity.onResume() 끝");
    }

    //아이의 개월수와 캐릭터 이미지 가져오기
    private class FindMonthNumAnimalTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog = new ProgressDialog(CharacterActivity.this);
        private Analysis analysisTask;

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
        protected Void doInBackground(Void... params) {
            AnalysisAPI service = ServiceGenerator.createService(AnalysisAPI.class, "Analysis");
            Call<Analysis> call = service.findMonthNumAnimalByUserSeq(nowUsers.getUser_seq());
            try {
                analysisTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "캐릭터 조회 실패");
            }
            return null;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Void aVoid) {
            if (analysisTask != null) {
                Log.d("-진우-", "분석 : " + analysisTask.toString());
                ImageViewMyDraw myDraw = new ImageViewMyDraw(analysisTask.getMonth_num());
                myDraw.myDray();
            }

            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    //캐릭터페이지 초기 데이터조회(슬라이드 내 아이 목록, 내 이미지, 내 아이에 맞는 캐릭터 보여주기)
    /*private class CharacterTask extends AsyncTask<Object, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(CharacterActivity.this);
        private List<Users> usersTask;
        private CircularImageView img_profileTask;
        private Analysis analysisTask;

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("잠시만 기다려주세요");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            img_profileTask = (CircularImageView) params[0];

            //슬라이드메뉴에 있는 내 아이 목록
            *//*UsersAPI service = ServiceGenerator.createService(UsersAPI.class, "Users");
            Call<List<Users>> call = service.findUsersByMember(String.valueOf(member.getMember_seq()));
            try {
                usersTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 목록 가져오기 실패");
            }*//*

            //이미지 불러오기
            *//*String image_url = null;
            if (member.getJoin_route().equals("kakao")) {
                image_url = member.getKakao_thumbnailimagepath();
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
            }*//*

            //개월수, 캐릭터이미지
            AnalysisAPI service1 = ServiceGenerator.createService(AnalysisAPI.class, "Analysis");
            Call<Analysis> call1 = service1.findMonthNumAnimalByUserSeq(nowUsers.getUser_seq());
            try {
                analysisTask = call1.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "캐릭터 조회 실패");
            }

            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            *//*if (bitmap != null) {
                Log.d("-진우-", "이미지읽어옴");
                img_profileTask.setImageBitmap(bitmap);
            }*//*

            *//*if (usersTask != null && usersTask.size() > 0) {
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());
                for (Users u : usersTask) {
                    Log.d("-진우-", "내 아이 : " + u.toString());
                }

                if (nowUsers == null) {
                    nowUsers = usersTask.get(0);
                }
                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
                usersList = usersTask;
                //현재 선택된 내 아이를 맨 뒤로 이동
                usersList = Utility.seqChange(usersList, nowUsers.getUser_seq());
                usersListSlideAdapter.setUsersList(usersList);

            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다");
            }

            int height = getListViewHeight(lv_usersList);
            lv_usersList.getLayoutParams().height = height;
            usersListSlideAdapter.notifyDataSetChanged();*//*

            //캐릭터
            if (analysisTask != null) {
                Log.d("-진우-", "분석 : " + analysisTask.toString());
                ImageViewMyDraw myDraw = new ImageViewMyDraw(analysisTask.getMonth_num());
                myDraw.myDray();
            }

            dialog.dismiss();
            super.onPostExecute(bitmap);
        }
    }*/

    //내 아이에 맞는 캐릭터 보여주기
    private class ImageViewMyDraw {

        private int month_num;
        private ImageView iv_my_animal_01, iv_my_animal_02, iv_my_animal_03;
        private ImageView iv_animal_00, iv_animal_01, iv_animal_02, iv_animal_03, iv_animal_04;
        private ImageView iv_animal_05, iv_animal_06, iv_animal_07, iv_animal_08, iv_animal_09;
        private ImageView iv_animal_10, iv_animal_11, iv_animal_12, iv_animal_13, iv_animal_14;
        private ImageView iv_animal_15, iv_animal_16, iv_animal_17, iv_animal_18, iv_animal_19;

        public ImageViewMyDraw(int month_num) {
            Log.d("-진우-", "그리기 초기화");
            this.month_num = month_num;
            iv_my_animal_01 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_my_animal_01);
            iv_my_animal_02 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_my_animal_02);
            iv_my_animal_03 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_my_animal_03);
            iv_animal_00 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_00);
            iv_animal_00.setAlpha(0.1f);
            iv_animal_01 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_01);
            iv_animal_01.setAlpha(0.1f);
            iv_animal_02 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_02);
            iv_animal_02.setAlpha(0.1f);
            iv_animal_03 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_03);
            iv_animal_03.setAlpha(0.1f);
            iv_animal_04 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_04);
            iv_animal_04.setAlpha(0.1f);
            iv_animal_05 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_05);
            iv_animal_05.setAlpha(0.1f);
            iv_animal_06 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_06);
            iv_animal_06.setAlpha(0.1f);
            iv_animal_07 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_07);
            iv_animal_07.setAlpha(0.1f);
            iv_animal_08 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_08);
            iv_animal_08.setAlpha(0.1f);
            iv_animal_09 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_09);
            iv_animal_09.setAlpha(0.1f);
            iv_animal_10 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_10);
            iv_animal_10.setAlpha(0.1f);
            iv_animal_11 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_11);
            iv_animal_11.setAlpha(0.1f);
            iv_animal_12 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_12);
            iv_animal_12.setAlpha(0.1f);
            iv_animal_13 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_13);
            iv_animal_13.setAlpha(0.1f);
            iv_animal_14 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_14);
            iv_animal_14.setAlpha(0.1f);
            iv_animal_15 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_15);
            iv_animal_15.setAlpha(0.1f);
            iv_animal_16 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_16);
            iv_animal_16.setAlpha(0.1f);
            iv_animal_17 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_17);
            iv_animal_17.setAlpha(0.1f);
            iv_animal_18 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_18);
            iv_animal_18.setAlpha(0.1f);
            iv_animal_19 = (ImageView) findViewById(com.knowledge_seek.phyctogram.R.id.iv_animal_19);
            iv_animal_19.setAlpha(0.1f);
        }

        public void myDray() {
            Log.d("-진우-", month_num + " 개월");
            if (month_num <= 6) {
                Log.d("-진우-", "그리기");
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_00_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_00_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_00_03);
                iv_animal_00.setAlpha(1.0f);
            } else if (month_num >= 7 && month_num <= 12) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_01_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_01_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_01_03);
                iv_animal_01.setAlpha(1.0f);
            } else if (month_num >= 13 && month_num <= 24) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_02_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_02_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_02_03);
                iv_animal_02.setAlpha(1.0f);
            } else if (month_num >= 25 && month_num <= 36) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_03_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_03_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_03_03);
                iv_animal_03.setAlpha(1.0f);
            } else if (month_num >= 37 && month_num <= 48) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_04_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_04_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_04_03);
                iv_animal_04.setAlpha(1.0f);
            } else if (month_num >= 49 && month_num <= 60) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_05_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_05_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_05_03);
                iv_animal_05.setAlpha(1.0f);
            } else if (month_num >= 61 && month_num <= 72) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_06_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_06_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_06_03);
                iv_animal_06.setAlpha(1.0f);
            } else if (month_num >= 73 && month_num <= 84) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_07_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_07_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_07_03);
                iv_animal_07.setAlpha(1.0f);
            } else if (month_num >= 85 && month_num <= 96) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_08_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_08_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_08_03);
                iv_animal_08.setAlpha(1.0f);
            } else if (month_num >= 97 && month_num <= 108) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_09_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_09_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_09_03);
                iv_animal_09.setAlpha(1.0f);
            } else if (month_num >= 109 && month_num <= 120) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_10_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_10_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_10_03);
                iv_animal_10.setAlpha(1.0f);
            } else if (month_num >= 121 && month_num <= 132) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_11_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_11_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_11_03);
                iv_animal_11.setAlpha(1.0f);
            } else if (month_num >= 133 && month_num <= 144) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_12_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_12_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_12_03);
                iv_animal_12.setAlpha(1.0f);
            } else if (month_num >= 145 && month_num <= 156) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_13_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_13_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_13_03);
                iv_animal_13.setAlpha(1.0f);
            } else if (month_num >= 157 && month_num <= 168) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_14_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_14_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_14_03);
                iv_animal_14.setAlpha(1.0f);
            } else if (month_num >= 169 && month_num <= 180) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_15_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_15_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_15_03);
                iv_animal_15.setAlpha(1.0f);
            } else if (month_num >= 181 && month_num <= 192) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_16_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_16_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_16_03);
                iv_animal_16.setAlpha(1.0f);
            } else if (month_num >= 193 && month_num <= 204) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_17_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_17_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_17_03);
                iv_animal_17.setAlpha(1.0f);
            } else if (month_num >= 205 && month_num <= 216) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_18_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_18_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_18_03);
                iv_animal_18.setAlpha(1.0f);
            } else if (month_num >= 217 && month_num <= 228) {
                iv_my_animal_01.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_19_01);
                iv_my_animal_02.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_19_02);
                iv_my_animal_03.setImageResource(com.knowledge_seek.phyctogram.R.drawable.animal_19_03);
                iv_animal_19.setAlpha(1.0f);
            } /*else if (month_num == 500) {
                //입력된 데이터가 없다.
                iv_my_animal_01.setImageResource(R.drawable.sample);
                iv_my_animal_02.setImageResource(R.drawable.sample);
                iv_my_animal_03.setImageResource(R.drawable.sample);
            }*/
        }
    }
}
