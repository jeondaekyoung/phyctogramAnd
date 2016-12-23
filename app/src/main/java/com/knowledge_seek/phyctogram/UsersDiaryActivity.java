package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Diary;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.listAdapter.CalendarMonthAdapter;
import com.knowledge_seek.phyctogram.listAdapter.MonthItem;
import com.knowledge_seek.phyctogram.retrofitapi.DiaryAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.util.Utility;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;

/**
 * Created by sjw on 2015-12-28.
 */
public class UsersDiaryActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃 정의
    private TextView tv_users_name;     //아이 이름 출력

    private GridView gv_monthView;
    private CalendarMonthAdapter calendarMonthAdapter;
    private TextView tv_monthText;          //년월 출력
    private Button btn_monthPrevious;       //이전달가기
    private Button btn_monthNext;           //다음달가기
    private ImageButton imBtn_diary_write;  //일기쓰기

    //데이터 정의
    int curYear, curMonth;
    String curYearStr, curMonthStr;
    int curPosition;
    List<Diary> diaryList = new ArrayList<Diary>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("-진우-", "UsersDiaryActivity.onCreate() 실행");

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_calendar, ic_screen, true);

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

                //달력페이지에 출력할 일기 불러오기
                if(nowUsers != null){
                    ReUserDiaryTask task = new ReUserDiaryTask();
                    task.execute();
                }

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

        //그리드뷰 객체 참조 및 설정
        gv_monthView = (GridView) findViewById(R.id.gv_monthView);
        calendarMonthAdapter = new CalendarMonthAdapter(this);
        gv_monthView.setAdapter(calendarMonthAdapter);
        //그리드뷰(달력) 리스너 설정
        gv_monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MonthItem curItem = (MonthItem) calendarMonthAdapter.getItem(position);
                int day = curItem.getDay();
                //Log.d("-진우-", day + " 일입니다");
                for (final Diary d : diaryList) {
                    if (Integer.parseInt(d.getWritng_de()) == day) {
                        //일기 보기
                        Intent intent = new Intent(getApplicationContext(), DiaryViewActivity.class);
                        intent.putExtra("diary", d);
                        startActivity(intent);
                        /*AlertDialog.Builder dialog = new AlertDialog.Builder(UsersDiaryActivity.this);
                        dialog.setTitle("일기 보기");
                        dialog.setMessage(day + "일 일기를 보시겠습니까?");
                        dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getApplicationContext(), "일기보기", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), DiaryViewActivity.class);
                                //intent.putExtra("member", member);
                                intent.putExtra("diary", d);
                                startActivity(intent);
                                //finish();
                            }
                        });
                        dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();*/
                    }
                }
            }
        });

        //년월출력
        tv_monthText = (TextView) findViewById(R.id.tv_monthText);
        setMonthText();

        //이전달가기, 다음달가기
        btn_monthPrevious = (Button) findViewById(R.id.btn_monthPrevious);
        btn_monthPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarMonthAdapter.setPreviousMonth();
                setMonthText();

                //일기 불러오기
                if(nowUsers != null) {
                    ReUserDiaryTask task = new ReUserDiaryTask();
                    task.execute();
                }
            }
        });
        btn_monthNext = (Button) findViewById(R.id.btn_monthNext);
        btn_monthNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarMonthAdapter.setNextMonth();
                setMonthText();

                //일기 불러오기
                if(nowUsers != null) {
                    ReUserDiaryTask task = new ReUserDiaryTask();
                    task.execute();
                }
            }
        });

        //일기쓰기
        imBtn_diary_write = (ImageButton) findViewById(R.id.imBtn_diary_write);
        imBtn_diary_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "일기 쓰기 클릭", Toast.LENGTH_SHORT).show();
                if(nowUsers != null) {
                    Intent intent = new Intent(getApplicationContext(), DiaryWriteActivity.class);
                    //intent.putExtra("member", member);
                    startActivity(intent);
                    //finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.diaryWriteActivity_registerChild, Toast.LENGTH_SHORT).show();
                }
            }
        });
        curPosition = -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "UsersDiaryActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

        //달력페이지에 출력할 일기 불러오기
        if(nowUsers != null){
            ReUserDiaryTask task = new ReUserDiaryTask();
            task.execute();
        }
       // Log.d("-진우-", "UsersDiaryActivity.onResume() : " + member.toString());

        Log.d("-진우-", "UsersDiaryActivity.onResume() 끝");
    }


    //년월 출력
    private void setMonthText() {
        curYear = calendarMonthAdapter.getCurYear();
        curMonth = calendarMonthAdapter.getCurMonth();
        tv_monthText.setText(curYear + getString(R.string.usersDiaryActivity_year)+" " + (curMonth + 1) + getString(R.string.usersDiaryActivity_month));

        curYearStr = String.valueOf(curYear);
        curMonthStr = String.valueOf(curMonth + 1);
        if (curMonthStr.length() == 1) {
            curMonthStr = "0".concat(curMonthStr);
        }
    }

    //일기 목록 다시 읽어오기
    private class ReUserDiaryTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog = new ProgressDialog(UsersDiaryActivity.this);
        private List<Diary> diarysTask;

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
        protected Void doInBackground(Void... params) {
            //일기 목록 읽어오기
            DiaryAPI service = ServiceGenerator.createService(DiaryAPI.class, "Diary");
            Call<List<Diary>> call = service.findDiaryByUserSeqYearMt(nowUsers.getUser_seq(), curYearStr, curMonthStr);
            try {
                diarysTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "일기 목록 읽어오기 실패");
            }
            return null;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Void aVoid) {
            //일기
            diaryList.clear();
            if (diarysTask != null && diarysTask.size() > 0) {
                diaryList.addAll(diarysTask);
                Log.d("-진우-", "일기 갯수 : " + diaryList.size());
                for (Diary d : diaryList) {
                    Log.d("-진우-", "일기 : " + d.toString());
                }
            }
            //일기를 달력에 뿌려주기
            calendarMonthAdapter.setDiaryList(diaryList);
            calendarMonthAdapter.notifyDataSetChanged();

            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
