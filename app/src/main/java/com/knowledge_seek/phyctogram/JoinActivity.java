package com.knowledge_seek.phyctogram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import com.knowledge_seek.phyctogram.domain.Member;
import com.knowledge_seek.phyctogram.phyctogram.SaveSharedPreference;
import com.knowledge_seek.phyctogram.retrofitapi.MemberAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.util.Utility;
import retrofit.Call;

/**
 * Created by dkfka on 2015-11-26.
 */
public class JoinActivity extends Activity {

    //데이터
    private Member memberActivity;
    private Member member;

    //레이아웃정의
    private EditText et_name;
    private EditText et_email;
    private EditText et_pw;
    private EditText et_pw1;
    private Button btn_join_member;
    private CheckBox allAgreement,agreement1, agreement2;
    private TextView agreement1_view,agreement2_view;
    private ScrollView sv_layout;
    private TextView textViewPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.knowledge_seek.phyctogram.R.layout.activity_join);

        member = new Member();
        textViewPw = (TextView) findViewById(com.knowledge_seek.phyctogram.R.id.textViewPw);
        et_name = (EditText)findViewById(com.knowledge_seek.phyctogram.R.id.et_name);
        et_email = (EditText)findViewById(com.knowledge_seek.phyctogram.R.id.et_email);
        et_pw = (EditText)findViewById(com.knowledge_seek.phyctogram.R.id.et_pw);
        et_pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (et_pw.getText().toString().length()>0) {
                    if (et_pw.getText().toString().equals(et_pw1.getText().toString())) {
                        textViewPw.setTextColor(Color.parseColor("#01DF3A"));
                        textViewPw.setText(com.knowledge_seek.phyctogram.R.string.joinActivity_agreePW);
                    } else {
                        textViewPw.setTextColor(Color.parseColor("#ff0000"));
                        textViewPw.setText(com.knowledge_seek.phyctogram.R.string.joinActivity_discordPW);
                    }
                }
            }
        });
        et_pw1 = (EditText)findViewById(com.knowledge_seek.phyctogram.R.id.et_pw1);
        et_pw1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (et_pw.getText().toString().length()>0) {
                    if (et_pw.getText().toString().equals(et_pw1.getText().toString())) {
                        textViewPw.setTextColor(Color.parseColor("#01DF3A"));
                        textViewPw.setText(com.knowledge_seek.phyctogram.R.string.joinActivity_agreePW);
                    } else {
                        textViewPw.setTextColor(Color.parseColor("#ff0000"));
                        textViewPw.setText(com.knowledge_seek.phyctogram.R.string.joinActivity_discordPW);
                    }
                }
            }
        });
        btn_join_member = (Button)findViewById(com.knowledge_seek.phyctogram.R.id.btn_join_member);
        sv_layout = (ScrollView)findViewById(com.knowledge_seek.phyctogram.R.id.sv_layout);
        sv_layout.setVerticalScrollBarEnabled(false);
        sv_layout.setHorizontalScrollBarEnabled(false);

        //가입
        btn_join_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                member.setName(et_name.getText().toString());
                member.setEmail(et_email.getText().toString());
                member.setPassword(et_pw.getText().toString());
                //멤버 내용 체크
                if(!checkMember(member)){
                    return ;
                }

                if(!checkpw(et_pw.getText().toString(), et_pw1.getText().toString())){
                   return ;
                }

                //약관 동의체크
                if(!(agreement1.isChecked() && agreement2.isChecked())){
                    Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.joinActivity_agreeClause, Toast.LENGTH_SHORT).show();
                    return ;
                }

                //멤버 가입
                member.setJoin_route("phyctogram");
                Log.d("-진우-", member.toString());
                Log.d("-진우-", Utility.member2json(member));
                registerMember(member);

                //redirectMainActivity(memberActivity);
            }
        });


        //이용약관 동의 및 개인정보취급방침 동의
        allAgreement =(CheckBox) findViewById(com.knowledge_seek.phyctogram.R.id.allAgreement);
        agreement1 = (CheckBox) findViewById(com.knowledge_seek.phyctogram.R.id.agreement1);
        agreement2 = (CheckBox) findViewById(com.knowledge_seek.phyctogram.R.id.agreement2);
        agreement1_view = (TextView)findViewById(com.knowledge_seek.phyctogram.R.id.agreement1_view);
        agreement2_view = (TextView)findViewById(com.knowledge_seek.phyctogram.R.id.agreement2_view);

        allAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                agreement1.setChecked(true);
                agreement2.setChecked(true);
                }
                else{
                    agreement1.setChecked(false);
                    agreement2.setChecked(false);
                }
            }
        });
        agreement1_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                View layout;
                layout = inflater.inflate(com.knowledge_seek.phyctogram.R.layout.popup_agree, (ViewGroup) findViewById(com.knowledge_seek.phyctogram.R.id.agreeText));
                AlertDialog.Builder aDialog = new AlertDialog.Builder(JoinActivity.this);

                aDialog.setTitle(com.knowledge_seek.phyctogram.R.string.joinActivity_clause);
                aDialog.setView(layout);

                aDialog.setNegativeButton(com.knowledge_seek.phyctogram.R.string.commonActivity_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog ad = aDialog.create();
                ad.show();
            }
        });
        agreement2_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext2 = getApplicationContext();
                LayoutInflater inflater2 = (LayoutInflater) mContext2.getSystemService(LAYOUT_INFLATER_SERVICE);

                View layout;
                layout = inflater2.inflate(com.knowledge_seek.phyctogram.R.layout.popup_agree2, (ViewGroup) findViewById(com.knowledge_seek.phyctogram.R.id.agreeText));
                AlertDialog.Builder aDialog2 = new AlertDialog.Builder(JoinActivity.this);

                aDialog2.setTitle(com.knowledge_seek.phyctogram.R.string.joinActivity_individualInfo);
                aDialog2.setView(layout);

                aDialog2.setNegativeButton(com.knowledge_seek.phyctogram.R.string.commonActivity_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog ad2 = aDialog2.create();
                ad2.show();
            }
        });

  /*      agreement1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(allAgreement.isChecked()){
                    return;
                }
                if (isChecked) {
                    Context mContext = getApplicationContext();
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                    View layout;
                    layout = inflater.inflate(R.layout.popup_agree, (ViewGroup) findViewById(R.id.agreeText));
                    AlertDialog.Builder aDialog = new AlertDialog.Builder(JoinActivity.this);

                    aDialog.setTitle(R.string.joinActivity_clause);
                    aDialog.setView(layout);

                    aDialog.setNegativeButton(R.string.commonActivity_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog ad = aDialog.create();
                    ad.show();
                }
            }
        });
        agreement2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(allAgreement.isChecked()){
                    return;
                }
                if (isChecked) {
                    Context mContext2 = getApplicationContext();
                    LayoutInflater inflater2 = (LayoutInflater) mContext2.getSystemService(LAYOUT_INFLATER_SERVICE);

                    View layout;
                    layout = inflater2.inflate(R.layout.popup_agree2, (ViewGroup) findViewById(R.id.agreeText));
                    AlertDialog.Builder aDialog2 = new AlertDialog.Builder(JoinActivity.this);

                    aDialog2.setTitle(R.string.joinActivity_individualInfo);
                    aDialog2.setView(layout);

                    aDialog2.setNegativeButton(R.string.commonActivity_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog ad2 = aDialog2.create();
                    ad2.show();
                }
            }
        });*/
    }//onCreate

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (et_pw.getText().toString().length()>0) {
            if (et_pw.getText().toString().equals(et_pw1.getText().toString())) {
                textViewPw.setTextColor(Color.parseColor("#01DF3A"));
                textViewPw.setText(com.knowledge_seek.phyctogram.R.string.joinActivity_agreePW);
            } else {
                textViewPw.setTextColor(Color.parseColor("#ff0000"));
                textViewPw.setText(com.knowledge_seek.phyctogram.R.string.joinActivity_discordPW);
            }
        }
        return super.onTouchEvent(event);
    }

    //패스워드 체크
    private boolean checkpw(String word1, String word2){
        if(word1.length() <= 0 || word2.length() <= 0 || !word1.equals(word2)){
            Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.joinActivity_checkPW, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //멤버 내용 체크
    private boolean checkMember(Member member){
        if(member.getName().length() <= 0 || member.getEmail().length() <= 0 || member.getPassword().length() <= 0){
            Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.joinActivity_checkContents, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (member.getEmail().contains("@")&&member.getEmail().contains(".")){
            return true;
        }else{
            Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.joinActivity_checkEmail, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //멤버저장
    private void registerMember(Member member){

        RegisterMemberTask task = new RegisterMemberTask();
        task.execute(member);

    }

    private void redirectMainActivity(Member member) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("member", member);
        startActivity(intent);
        finish();
    }

    //멤버 읽어오기
    private class RegisterMemberTask extends AsyncTask<Object, Void, Member> {

        private ProgressDialog dialog = new ProgressDialog(JoinActivity.this);
        private Member memberTask;

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
        protected Member doInBackground(Object... params) {
            Member member = null;
            memberTask = (Member)params[0];
            Log.d("-진우-", "멤버 읽어오기 : " + memberTask.toString());
            MemberAPI service = ServiceGenerator.createService(MemberAPI.class, "Member");
            Call<Member> call = service.registerMember(memberTask);
            try {
                member = call.execute().body();
            } catch (IOException e){
                e.printStackTrace();
            }
            return member;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Member member) {
            if(member != null) {
                Log.d("-진우-", "픽토그램 가입 성공 결과1 : " + member.toString());
                Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.joinActivity_successJoinAlert, Toast.LENGTH_SHORT).show();
                memberActivity = member;
                //가입완료후 로그인유지를 위해 preference를 사용한다.
                SaveSharedPreference.setMemberSeq(getApplicationContext(), String.valueOf(memberActivity.getMember_seq()));
                redirectMainActivity(memberActivity);
            } else {
                Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.joinActivity_alreadyJoinAlert, Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
            super.onPostExecute(member);
        }
    }

}
