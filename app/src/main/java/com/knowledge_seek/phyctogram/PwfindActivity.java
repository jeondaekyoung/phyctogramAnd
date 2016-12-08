package com.knowledge_seek.phyctogram;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.gcm.QuickstartPreferences;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.MemberAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;

import java.io.IOException;

import retrofit.Call;

/**
 * Created by dkfka on 2016-03-29.
 */
public class PwfindActivity extends BaseActivity {

    //레이아웃정의
    private LinearLayout ic_screen;
    private EditText editTextMail;
    private Button buttonFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_pw_find, ic_screen, true);

        editTextMail = (EditText) findViewById(R.id.editTextMail);
        buttonFind = (Button) findViewById(R.id.buttonFind);
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextMail.getText().length() < 0) {
                    Toast.makeText(v.getContext(), R.string.pwfindActivity_registerEmail, Toast.LENGTH_SHORT).show();
                } else {
                    //저장된 토큰이 있다면
                    if (QuickstartPreferences.token != null) {
                        Log.d("-진우-", "PwfindActivity editTextMail.getText: " + editTextMail.getText() + ", Token: " + QuickstartPreferences.token);
                        FindPwTask task = new FindPwTask(editTextMail.getText().toString(), QuickstartPreferences.token);
                        task.execute();
                    } else {
                        Toast.makeText(v.getContext(), R.string.pwfindActivity_notPush, Toast.LENGTH_SHORT).show();
                        Log.d("-진우-", "PwfindActivity - Token 발급 불가");
                    }
                }
            }
        });
    }

    //비밀번호 찾기
    private class FindPwTask extends AsyncTask<Void, Void, String> {

        private String mailAddr;
        private String token;

        public FindPwTask(String mailAddr, String token) {
            this.mailAddr = mailAddr;
            this.token = token;
        }

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            Log.d("-진우-", "FindPwTask onPreExecute");
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected String doInBackground(Void... params) {
            String result = null;

            MemberAPI service = ServiceGenerator.createService(MemberAPI.class);
            Call<String> call = service.findPw(mailAddr, token);
            try {
                result = call.execute().body();
                Log.d("-진우-", "비밀번호 찾기 결과 : " + result);
            } catch (IOException e) {
                Log.d("-진우-", "비밀번호 찾기 실패");
            }
            return result;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                    switch (result){
                        case "success":
                            Toast.makeText(PwfindActivity.this, R.string.pwfindActivity_sendPush, Toast.LENGTH_LONG).show();
                            Log.d("-진우-", "비밀번호 찾기에 성공하였습니다.");
                            finish();
                            break;
                        case "wrongMail":
                            Toast.makeText(PwfindActivity.this, R.string.pwfindActivity_failEmail, Toast.LENGTH_LONG).show();
                            Log.d("-진우-", "메일주소가 잘못되었습니다.");
                            break;
                        default:
                            Toast.makeText(PwfindActivity.this, R.string.pwfindActivity_failFindPW, Toast.LENGTH_LONG).show();
                            Log.d("-진우-", "비밀번호 찾기에 실패하였습니다.");
                            break;
                    }
            }

            else {
                Log.d("-진우-", "result null");
            }


            super.onPostExecute(result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}