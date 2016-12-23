package com.knowledge_seek.phyctogram;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.knowledge_seek.phyctogram.domain.Diary;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.DiaryAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.util.Utility;
import com.pkmmte.view.CircularImageView;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dkfka on 2015-12-08.
 */
public class DiaryViewActivity extends BaseActivity {

    //레이아웃정의
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃 정의
    private TextView tv_users_name;     //아이 이름 출력
    private EditText et_diary_date;
    private EditText et_title;
    private EditText et_contents;
    private Button btn_diary_delete;        //일기 삭제
    private Button btn_diary_modify;            //일기 수정

    //데이터
    private Diary diary = new Diary();
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("-진우-", "DiaryViewActivity.onCreate() 실행");

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_diary_view, ic_screen, true);

        //데이터셋팅
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            diary = (Diary)bundle.getSerializable("diary");
            Log.d("-진우-", "DiaryViewActivity 에서 " + diary.toString());
        } else {
            Log.d("-진우-", "DiaryViewActivity 에 diary가 없다");
        }

        //슬라이드 내 이미지, 셋팅
        img_profile = (CircularImageView)findViewById(R.id.img_profile);
        if (memberImg != null) {
            img_profile.setImageBitmap(memberImg);
        }

        //슬라이드 내 이름, 셋팅
        tv_member_name = (TextView)findViewById(R.id.tv_member_name);
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
                /*nowUsers = (Users) usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' 아이를 선택하였습니다", Toast.LENGTH_LONG).show();

                if (tv_users_name != null) {
                    tv_users_name.setText(nowUsers.getName());
                }

                //내용 지우기
                et_diary_date.setText("");
                et_title.setText("");
                et_contents.setText("");*/
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

        et_diary_date = (EditText)findViewById(R.id.et_diary_date);
        et_diary_date.setText(new StringBuilder(diary.getWritng_year()).append("-").append(diary.getWritng_mt()).append("-").append(diary.getWritng_de()));
        //달력 대화상자 띄우기
        /*GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        et_diary_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DiaryViewActivity.this, dateSetListener, year, month, day).show();
                setTheme(R.style.AppTheme);
            }
        });*/

        et_title = (EditText)findViewById(R.id.et_title);
        et_title.setText(diary.getTitle());
        et_contents = (EditText)findViewById(R.id.et_contents);
        et_contents.setText(diary.getContents());
        btn_diary_delete = (Button)findViewById(R.id.btn_diary_delete);
        btn_diary_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_diary_date.getText().toString().length() <= 0){
                    Toast.makeText(getApplicationContext(), R.string.diaryViewActivity_notDeleteDiary, Toast.LENGTH_LONG).show();
                    return;
                }
                String diary_date = et_diary_date.getText().toString();

                Log.d("-진우-", "삭제클릭 : " + Utility.diary2json(diary));
                //삭제 팝업
                AlertDialog.Builder dialog = new AlertDialog.Builder(DiaryViewActivity.this);
                dialog.setTitle(R.string.diaryViewActivity_deleteDiaryTitle)
                        .setMessage(nowUsers.getName() + getString(R.string.diaryViewActivity_respect)+" " + diary_date + " "+getString(R.string.diaryViewActivity_deleteAsk))
                        .setCancelable(false)
                        .setPositiveButton(R.string.diaryViewActivity_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("-진우-", "json : " + Utility.diary2json(diary));
                                DiaryAPI service = ServiceGenerator.createService(DiaryAPI.class);
                                Call<String> call = service.deleteDiaryByDiary(diary.getDiary_seq());
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Response<String> response, Retrofit retrofit) {
                                        Log.d("-진우-", "일기 삭제 결과 : " + response.body());
                                        if (response.body().equals("success")) {
                                            Toast.makeText(getApplicationContext(), R.string.diaryViewActivity_deleteAlert, Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                            //내용 지우기
                                            //et_diary_date.setText("");
                                            //et_title.setText("");
                                            //et_contents.setText("");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.commonActivity_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            }
        });

        btn_diary_modify = (Button)findViewById(R.id.btn_diary_modify);
        btn_diary_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Diary mod_diary = new Diary();
                mod_diary.setDiary_seq(diary.getDiary_seq());
                mod_diary.setTitle(et_title.getText().toString());
                mod_diary.setContents(et_contents.getText().toString());
                mod_diary.setUser_seq(nowUsers.getUser_seq());

                //Log.d("-진우-", "날짜 : " + et_diary_date.getText().toString().length());
                if(et_diary_date.getText().toString().length() <= 0){
                    Toast.makeText(getApplicationContext(), R.string.diaryViewActivity_notModifyAlert, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    //Log.d("-진우-", "날짜 : " + et_diary_date.getText().toString());
                    String[] date = et_diary_date.getText().toString().split("-");
                    //Log.d("-진우-", String.valueOf(date.length));
                    mod_diary.setWritng_year(date[0]);
                    mod_diary.setWritng_mt(date[1]);
                    mod_diary.setWritng_de(date[2]);
                }

                if(!checkDiary(mod_diary)){
                    return ;
                }

                if(diary.equals(mod_diary)){
                    Toast.makeText(getApplicationContext(), R.string.diaryViewActivity_notChangeAlert, Toast.LENGTH_SHORT).show();
                    return ;
                }

                Log.d("-진우-", "json : " + Utility.diary2json(mod_diary));
                DiaryAPI service = ServiceGenerator.createService(DiaryAPI.class);
                Call<String> call = service.modifyDiaryByDiary(mod_diary);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        Log.d("-진우-", "일기 수정 결과 : " + response.body());
                        if(response.body().equals("success")){
                            Toast.makeText(getApplicationContext(), R.string.diaryViewActivity_modifyAlert, Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "DiaryViewActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

      //  Log.d("-진우-", "DiaryViewActivity.onResume() : " + member.toString());

        Log.d("-진우-", "DiaryViewActivity.onResume() 끝");
    }

    //날짜 입력
    /*private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String msg = String.valueOf(year).concat("-").concat(dateFormat(monthOfYear + 1)).concat("-").concat(dateFormat(dayOfMonth));
            et_diary_date.setText(msg);
        }
    };*/

    //Diary의 내용체크
    private boolean checkDiary(Diary diary){
        if(diary.getTitle().length() <= 0 || diary.getContents().length() <= 0){
            Toast.makeText(getApplicationContext(), R.string.communityWriteActivity_writeTitleContents, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
