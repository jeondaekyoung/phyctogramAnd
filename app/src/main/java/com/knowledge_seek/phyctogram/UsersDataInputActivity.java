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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Height;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.HeightAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.util.Utility;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.List;

import retrofit.Call;

/**
 * Created by dkfka on 2015-12-10.
 */
public class UsersDataInputActivity extends BaseActivity {

    //데이터
    private Height usersHeight;

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    private EditText et_input_height;       //키
    private Button btn_users_height;       //키 저장
    private TextView tv_users_name;     //아이 이름 출력
    private DatePicker dp_mesure_date;      //날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_users_data_input, ic_screen, true);

        //데이터셋팅
        usersHeight = new Height();

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

        //메인페이지 내 아이 이름 출력
        tv_users_name = (TextView) findViewById(R.id.tv_users_name);
        if (nowUsers != null) {
            tv_users_name.setText(nowUsers.getName());
        }

        //슬라이드 내 아이 목록(ListView)에서 아이 선택시
        lv_usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowUsers = (Users) usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' "+getString(R.string.characterActivity_choiceChild), Toast.LENGTH_LONG).show();

                tv_users_name.setText(nowUsers.getName());

                //선택 아이로 인한 순서 변경
                Utility.seqChange(usersList, nowUsers.getUser_seq());
                //내 아이 목록 셋팅
                usersListSlideAdapter.setUsersList(usersList);
                usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
                usersListSlideAdapter.notifyDataSetChanged();
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


        et_input_height = (EditText)findViewById(R.id.et_input_height);
        dp_mesure_date = (DatePicker)findViewById(R.id.dp_mesure_date);


        //키 저장
        btn_users_height = (Button)findViewById(R.id.btn_users_height);
        btn_users_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String height_str = et_input_height.getText().toString();
                Log.d("-진우-", "입력된 키 : " + height_str);
                if(nowUsers.getUser_seq() == 0){
                    Toast.makeText(getApplicationContext(), R.string.diaryWriteActivity_registerChild, Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(!checkHeight(height_str)){
                    return ;
                }
                double height = Double.valueOf(height_str);
                usersHeight.setUser_seq(nowUsers.getUser_seq());
                usersHeight.setHeight(height);

                String mesure_date = new StringBuilder().append(dp_mesure_date.getYear()).append("-")
                        .append(Utility.dateFormat(dp_mesure_date.getMonth() + 1)).append("-").append(Utility.dateFormat(dp_mesure_date.getDayOfMonth())).toString();
                usersHeight.setMesure_date(mesure_date);

                Log.d("-진우-", "키 저장하기 : " + usersHeight.toString());

                Log.d("-진우-", "json : " + Utility.height2json(usersHeight));

                RegisterHeightTask task = new RegisterHeightTask(usersHeight);
                task.execute();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "UsersDataInputActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

       // Log.d("-진우-", "UsersDataInputActivity 에 onResume() : " + member.toString());

        Log.d("-진우-", "UsersDataInputActivity.onResume() 끝");
    }



    //height 내용 체크
    private boolean checkHeight(String height_str){
        //Log.d("-진우-", "자릿수 : " + height_str.length());
        if(height_str.length() <= 0 || height_str.length() > 7){
            Toast.makeText(getApplicationContext(), R.string.usersDataInputActivity_checkHeight, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //키 저장하기
    private class RegisterHeightTask extends AsyncTask<Void, Void, String>{

        private Height heightTask;
        private ProgressDialog dialog = new ProgressDialog(UsersDataInputActivity.this);

        public RegisterHeightTask(Height height){
            this.heightTask = height;
        }

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            HeightAPI service = ServiceGenerator.createService(HeightAPI.class, "Height");
            Call<String> call = service.registerHeight(heightTask);
            try {
                result = call.execute().body();
            } catch (IOException e){
                Log.d("-진우-", "키 저장 실패");
            }

            return result;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(String result) {
            if(result != null && result.equals("success")){
                Toast.makeText(getApplicationContext(), R.string.commonActivity_save, Toast.LENGTH_SHORT).show();
               onBackPressed();
            } else {
                Log.d("-진우-", "저장하는데 실패하였습니다");
            }

            dialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
