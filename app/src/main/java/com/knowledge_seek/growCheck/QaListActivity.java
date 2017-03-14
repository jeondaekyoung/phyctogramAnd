package com.knowledge_seek.growCheck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.knowledge_seek.growCheck.domain.Qa;
import com.knowledge_seek.growCheck.kakao.common.BaseActivity;
import com.knowledge_seek.growCheck.listAdapter.QaListAdapter;
import com.knowledge_seek.growCheck.retrofitapi.QaAPI;
import com.knowledge_seek.growCheck.retrofitapi.ServiceGenerator;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;

/**
 * Created by sjw on 2016-02-16.
 */
public class QaListActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;        //슬라이드 내 이름

    //레이아웃정의
    private ImageButton imBtn_qa_write;     //글 쓰기
    private ListView lv_qalist;
    private QaListAdapter qaListAdapter;

    //데이터정의
    private List<Qa> qaList = new ArrayList<>();
    private boolean lastListViewVisible = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
    private int pageCnt = 0;                                //리스트의 목록 페이지 번호


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_qa_list, ic_screen, true);

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
        //문의하기 글쓰기
        imBtn_qa_write = (ImageButton)findViewById(R.id.imBtn_qa_write);
        imBtn_qa_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QaWriteActivity.class);
                startActivity(intent);
            }
        });

        //문의내용 글 목록
        lv_qalist = (ListView)findViewById(R.id.lv_qaList);
        qaListAdapter = new QaListAdapter(this, qaList, R.layout.list_qa);
        lv_qalist.setAdapter(qaListAdapter);
        lv_qalist.setOnScrollListener(scrollListener);

        //리스트뷰 클릭 -> 글 보기로 이동
        lv_qalist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Qa qa = (Qa)qaListAdapter.getItem(position);
                Log.d("-진우-", "선택한 문의내용 : " + qa.toString());
                Intent intent = new Intent(getApplicationContext(), QaViewActivity.class);
                intent.putExtra("qa_seq", qa.getQa_seq());
                intent.putExtra("title", qa.getTitle());
                intent.putExtra("writing_de", qa.getWritng_de());
                intent.putExtra("contents", qa.getContents());
                intent.putExtra("state", qa.getState());
                intent.putExtra("ansewr", qa.getAnswer());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "CommunityListActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());

        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

       // Log.d("-진우-", "QaListActivity.onResume() : " + member.toString());

        //새로읽어오기
        qaList.clear();
        pageCnt = 0;
        QaListTask task = new QaListTask(pageCnt);
        task.execute();

        Log.d("-진우-", "QaListActivity.onResume() 끝");
    }

    //문의내용 목록 불러오기
    private class QaListTask extends AsyncTask<Void, Void, List<Qa>>{

        private ProgressDialog dialog = new ProgressDialog(QaListActivity.this);
        private int pageCntTask;
        private List<Qa> qaTask;

        public QaListTask(int pageCntTask) {
            this.pageCntTask = pageCntTask;
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
        protected List<Qa> doInBackground(Void... params) {
            //문의내용 목록
            QaAPI service = ServiceGenerator.createService(QaAPI.class, "Qa");
            Call<List<Qa>> call = service.findqaByMemberSeq(member.getMember_seq(), pageCntTask);
            try {
                qaTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "문의내용 목록조회 실패");
            }
            return qaTask;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(List<Qa> qas) {
            if(qas != null && qas.size() > 0) {
                Log.d("-진우-", "읽어온 목록은 " + qas.size() + " 개 있습니다");
                qaList.addAll(qas);
                Log.d("-진우-", "총 목록은 " + qaList.size() + " 개 있습니다");

                qaListAdapter.setQas(qaList);
                pageCnt = pageCntTask+1;
            } else {
                Log.d("-진우-", "성공했으나 목록이 없습니다");
            }

            qaListAdapter.notifyDataSetChanged();

            dialog.dismiss();
            super.onPostExecute(qas);
        }
    }

    //더보기 - 스크롤리스너
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을 때 발생되는 스크롤상태입니다.
            //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastListViewVisible) {
                //화면에 바닦에 닿았고, 스크롤이 멈추었다.
                Log.d("-진우-", "추가데이터 불러오기");
                QaListTask task = new QaListTask(pageCnt);
                task.execute();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가
                //리스트 전체의 갯수(totalItemCount) - 1 보다 크거나 같을 때
                lastListViewVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
        }
    };
}
