package com.knowledge_seek.growCheck;

import android.app.ProgressDialog;
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

import com.knowledge_seek.growCheck.domain.Qa;
import com.knowledge_seek.growCheck.kakao.common.BaseActivity;
import com.knowledge_seek.growCheck.retrofitapi.QaAPI;
import com.knowledge_seek.growCheck.retrofitapi.ServiceGenerator;
import com.knowledge_seek.growCheck.util.Utility;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;

import retrofit.Call;

/**
 * Created by dkfka on 2015-11-26.
 */
public class QaWriteActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private Button btn_qa_save;
    private EditText et_title;
    private EditText et_contents;

    //데이터정의
    private Qa qa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_qa_write, ic_screen, true);


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

        et_title = (EditText)findViewById(R.id.et_title);
        et_contents = (EditText)findViewById(R.id.et_contents);

        //수다방(커뮤니티) 글 저장
        btn_qa_save = (Button)findViewById(R.id.btn_qa_save);
        btn_qa_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("-진우-", "저장하기");
                qa = new Qa();
                qa.setTitle(et_title.getText().toString());
                qa.setContents(et_contents.getText().toString());
                qa.setMember_seq(member.getMember_seq());

                if(!checkQa(qa)){
                    return;
                }
                Log.d("-진우-", qa.toString());
                RegisterQaTask task = new RegisterQaTask(qa);
                task.execute();

                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "QaWriteActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());

        lv_usersList.getLayoutParams().height  = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

        //Log.d("-진우-", "QaWriteActivity 에 onResume() : " + member.toString());

        Log.d("-진우-", "QaWriteActivity.onResume() 끝");
    }



    //Commnty의 내용체크
    private boolean checkQa(Qa qa){
        if(qa.getTitle().length() <= 0 || qa.getContents().length() <= 0){
            Toast.makeText(getApplicationContext(), R.string.communityWriteActivity_writeTitleContents, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //글 저장
    private class RegisterQaTask extends AsyncTask<Void, Void, String> {

        private Qa qa;
        private ProgressDialog dialog = new ProgressDialog(QaWriteActivity.this);

        public RegisterQaTask(Qa qa) {
            this.qa = qa;
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
            Log.d("-진우-", Utility.qa2json(qa));
            String result = null;
            QaAPI service = ServiceGenerator.createService(QaAPI.class);
            Call<String> call = service.registerQa(qa);
            try {
                result = call.execute().body();
            } catch (IOException e){
                Log.d("-진우-", "글 저장 실패");
            }
            return result;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(String result) {
            if(result != null && result.equals("success")){
                Toast.makeText(getApplicationContext(), R.string.commonActivity_save, Toast.LENGTH_SHORT).show();
            } else {
                Log.d("-진우-", "저장하는데 실패하였습니다");
            }

            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

}