package com.knowledge_seek.phyctogram;

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

import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.knowledge_seek.phyctogram.domain.Notice;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.listAdapter.NoticeListAdapter;
import com.knowledge_seek.phyctogram.retrofitapi.NoticeAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import retrofit.Call;

/**
 * Created by dkfka on 2016-02-11.
 */
public class NoticeListActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;        //슬라이드 내 이름

    //레이아웃정의
    private ListView lv_noticeList;
    private NoticeListAdapter noticeListAdapter;

    //데이터정의
    private List<Notice> noticeList = new ArrayList<>();
    //private
    private boolean lastListViewVisible = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
    private int pageCnt = 0;                            //리스트뷰의 목록 페이지 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.ic_screen);
        LayoutInflater.from(this).inflate(com.knowledge_seek.phyctogram.R.layout.include_notice_list, ic_screen, true);

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
                /*nowUsers = (Users)usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' 아이를 선택하였습니다", Toast.LENGTH_LONG).show();*/

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

        //공지 글 목록
        lv_noticeList = (ListView)findViewById(com.knowledge_seek.phyctogram.R.id.lv_noticeList);
        noticeListAdapter = new NoticeListAdapter(this, noticeList, com.knowledge_seek.phyctogram.R.layout.list_notice);
        lv_noticeList.setAdapter(noticeListAdapter);
        lv_noticeList.setOnScrollListener(scrollListener);

        //리스트뷰 클릭 -> 글 보기로 이동
        lv_noticeList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notice notice = (Notice)noticeListAdapter.getItem(position);
                Log.d("-진우-", "선택한 공지사항 : " + notice.toString());
                Intent intent = new Intent(getApplicationContext(), NoticeViewActivity.class);
                intent.putExtra("notice_seq", notice.getNotice_seq());
                intent.putExtra("title", notice.getTitle());
                intent.putExtra("writing_de", notice.getWritng_de());
                intent.putExtra("notice", notice.getNotice());
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
        int height = getListViewHeight(lv_usersList);
        lv_usersList.getLayoutParams().height = height;
        usersListSlideAdapter.notifyDataSetChanged();

        Log.d("-진우-", "CommunityListActivity.onResume() : " + member.toString());

        //새로읽어오기
        noticeList.clear();
        pageCnt = 0;
        NoticeListTask task = new NoticeListTask(pageCnt);
        task.execute();
        /*btn_commntyLatest.setBackgroundResource(R.drawable.btn_on);
        btn_commntyPlpular.setBackgroundResource(R.drawable.btn_off);
        FindCommntyLatestTask task2 = new FindCommntyLatestTask(pageCnt);
        task2.execute();*/

        Log.d("-진우-", "CommunityListActivity.onResume() 끝");
    }

    //공지사항 목록 불러오기
    private class NoticeListTask extends AsyncTask<Void, Void, List<Notice>>{

        private ProgressDialog dialog = new ProgressDialog(NoticeListActivity.this);
        private int pageCntTask;
        private List<Notice> noticeTask;

        public NoticeListTask(int pageCntTask) {
            this.pageCntTask = pageCntTask;
        }

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
        protected List<Notice> doInBackground(Void... params) {
            //공지사항 목록
            NoticeAPI service = ServiceGenerator.createService(NoticeAPI.class, "Notice");
            Call<List<Notice>> call = service.findnoticeList(pageCntTask);
            try {
                noticeTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "공지사항 목록조회 실패");
            }
            return noticeTask;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(List<Notice> notices) {
            if(notices != null && notices.size() > 0){
                Log.d("-진우-", "읽어온 목록은 " + notices.size() + " 개 있습니다");
                noticeList.addAll(notices);
                Log.d("-진우-", "총 목록은 " + noticeList.size() + " 개 있습니다");

                noticeListAdapter.setNotices(noticeList);
                pageCnt = pageCntTask+1;
           } else {
                Log.d("-진우-", "성공했으나 목록이 없습니다");
            }

            noticeListAdapter.notifyDataSetChanged();

            dialog.dismiss();
            super.onPostExecute(notices);
        }
    }

    //더보기 - 스크롤리스너
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을 때 발생되는 스크롤상태입니다.
            //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastListViewVisible){
                //화면에 바닦에 닿았고, 스크롤이 멈추었다.
                Log.d("-진우-", "추가데이터 불러오기");
                NoticeListTask task = new NoticeListTask(pageCnt);
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
