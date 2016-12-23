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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.domain.SqlCommntyListView;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.listAdapter.SqlCommntyListViewListAdapter;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.retrofitapi.SqlCommntyListViewAPI;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;

/**
 * Created by dkfka on 2015-12-03.
 */
public class CommunityListActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;        //슬라이드 내 이름

    //레이아웃정의
    private ImageButton imBtn_community_write;              //글 쓰기
    private ListView lv_sqlcommntyList_communityList;
    private SqlCommntyListViewListAdapter sqlCommntyListViewListAdapter;
    private Button btn_commntyLatest;       //최신
    private Button btn_commntyPlpular;      //인기

    //데이터정의
    private List<SqlCommntyListView> sqlCommntyListViewList = new ArrayList<SqlCommntyListView>();
    private String mCurrentListViewState;        //현재 리스트뷰 상태(최신-latest, 인기-popular)
    private boolean lastListViewVisible = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
    private int pageCnt = 0;                            //리스트뷰의 목록 페이지 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_community_list, ic_screen, true);

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
                /*nowUsers = (Users)usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' 아이를 선택하였습니다", Toast.LENGTH_LONG).show();*/
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
        //수다방 글쓰기
        imBtn_community_write = (ImageButton)findViewById(R.id.imBtn_community_write);
        imBtn_community_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommunityWriteActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        //최신 글 목록 읽어오기
        btn_commntyLatest = (Button)findViewById(R.id.btn_commntyLatest);
        btn_commntyLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_commntyLatest.setBackgroundResource(R.drawable.btn_on);
                btn_commntyPlpular.setBackgroundResource(R.drawable.btn_off);
                pageCnt = 0;
                sqlCommntyListViewList.clear();
                FindCommntyLatestTask task = new FindCommntyLatestTask(pageCnt);
                task.execute();
            }
        });
        //인기 글 목록 읽어오기
        btn_commntyPlpular = (Button)findViewById(R.id.btn_commntyPopular);
        btn_commntyPlpular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_commntyPlpular.setBackgroundResource(R.drawable.btn_on);
                btn_commntyLatest.setBackgroundResource(R.drawable.btn_off);
                pageCnt = 0;
                sqlCommntyListViewList.clear();
                FindCommntyPopularTask task = new FindCommntyPopularTask(pageCnt);
                task.execute();
            }
        });


        //커뮤니티 글 목록
        lv_sqlcommntyList_communityList = (ListView)findViewById(R.id.lv_sqlcommntyList_communityList);
        //set adapter
        sqlCommntyListViewListAdapter = new SqlCommntyListViewListAdapter(this, sqlCommntyListViewList, R.layout.list_community );
        lv_sqlcommntyList_communityList.setAdapter(sqlCommntyListViewListAdapter);
        //set scrollListener
        lv_sqlcommntyList_communityList.setOnScrollListener(scrollListenerLatest);
        //리스트뷰 클릭 -> 글 보기로 이동
        lv_sqlcommntyList_communityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SqlCommntyListView sqlCommntyListView = (SqlCommntyListView)sqlCommntyListViewListAdapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), CommunityViewActivity.class);
                //intent.putExtra("member", member);
                intent.putExtra("sqlCommntyListView", sqlCommntyListView);
                startActivity(intent);
                //finish();

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
        //리스트뷰의 높이를 셋팅 (리스트뷰의 높이를 구함)
        lv_usersList.getLayoutParams().height  = getListViewHeight(lv_usersList);
        //data 갱신
        usersListSlideAdapter.notifyDataSetChanged();

       // Log.d("-진우-", "CommunityListActivity.onResume() : " + member.toString());

        //새로읽어오기
        pageCnt = 0;
        btn_commntyLatest.setBackgroundResource(R.drawable.btn_on);
        btn_commntyPlpular.setBackgroundResource(R.drawable.btn_off);
        //최신 글 목록 읽어오기
        FindCommntyLatestTask task2 = new FindCommntyLatestTask(pageCnt);
        task2.execute();

        Log.d("-진우-", "CommunityListActivity.onResume() 끝");
    }

    //최신 글 목록 읽어오기
    private class FindCommntyLatestTask extends AsyncTask<Void, Void, List<SqlCommntyListView>>{
        private int pageCntTask; //페이지 번호
        private List<SqlCommntyListView> sqlCommntyListViewTask; //커뮤니티 리스트 객체
        private ProgressDialog dialog = new ProgressDialog(CommunityListActivity.this); //로딩 팝업

        //생성자 페이지 번호 셋팅
        public FindCommntyLatestTask(int pageCntTask) {
            this.pageCntTask = pageCntTask;
        }

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(R.string.commonActivity_wait+"");
            dialog.show();
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected List<SqlCommntyListView> doInBackground(Void... params) {
            //통신에 필요한 객체 생성
            SqlCommntyListViewAPI service = ServiceGenerator.createService(SqlCommntyListViewAPI.class, "SqlCommntyListView");
            Call<List<SqlCommntyListView>> call = service.findCommntyLatest(pageCntTask);
            try {
                //통신 시작 후 결과를 sqlCommntyListViewTask에 저장
                sqlCommntyListViewTask = call.execute().body();
            } catch (IOException e){
                Log.d("-진우-", "수다방 목록 조회 실패");
            }
            return sqlCommntyListViewTask;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(List<SqlCommntyListView> sqlCommntyListViews) {
            if(sqlCommntyListViews != null && sqlCommntyListViews.size() > 0){
                for(SqlCommntyListView s : sqlCommntyListViews){
                    Log.d("-진우-", "목록 : " + s.toString());
                }
                Log.d("-진우-", "읽어온 목록은 " + sqlCommntyListViews.size() + " 개 있습니다");

                if(pageCntTask == 0){
                    sqlCommntyListViewList.clear();
                }
                //sqlCommntyListViewList = sqlCommntyListViews;
                sqlCommntyListViewList.addAll(sqlCommntyListViews);
                Log.d("-진우-", "총 목록은 " + sqlCommntyListViewList.size() + " 개 입니다");
                sqlCommntyListViewListAdapter.setSqlCommntyListViews(sqlCommntyListViewList);
                pageCnt = pageCntTask+1;

            } else {
                Log.d("-진우-", "성공했으나 목록이 없습니다");
            }

            sqlCommntyListViewListAdapter.notifyDataSetChanged();
            mCurrentListViewState = "latest";

            dialog.dismiss();
            super.onPostExecute(sqlCommntyListViews);
        }
    }

    //인기 글 목록 읽어오기
    private class FindCommntyPopularTask extends AsyncTask<Void, Void, List<SqlCommntyListView>>{
        private int pageCntTask;
        private List<SqlCommntyListView> sqlCommntyListViewTask;
        private ProgressDialog dialog = new ProgressDialog(CommunityListActivity.this);

        //생성자 페이지 번호 셋팅
        public FindCommntyPopularTask(int pageCntTask) {
            this.pageCntTask = pageCntTask;
        }

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(R.string.commonActivity_wait+"");
            dialog.show();
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected List<SqlCommntyListView> doInBackground(Void... params) {
            SqlCommntyListViewAPI service = ServiceGenerator.createService(SqlCommntyListViewAPI.class, "SqlCommntyListView");
            Call<List<SqlCommntyListView>> call = service.findCommntyPopular(pageCnt);
            try {
                sqlCommntyListViewTask = call.execute().body();
            } catch (IOException e){
                Log.d("-진우-", "수다방 목록 조회 실패");
            }
            return sqlCommntyListViewTask;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(List<SqlCommntyListView> sqlCommntyListViews) {
            if(sqlCommntyListViews != null && sqlCommntyListViews.size() > 0){
                for(SqlCommntyListView s : sqlCommntyListViews){
                    Log.d("-진우-", "목록 : " + s.toString());
                }
                Log.d("-진우-", "읽어온 목록은 " + sqlCommntyListViews.size() + " 개 있습니다");

                sqlCommntyListViewList.addAll(sqlCommntyListViews);
                Log.d("-진우-", "총 목록은 " + sqlCommntyListViewList.size() + " 개 입니다");
                sqlCommntyListViewListAdapter.setSqlCommntyListViews(sqlCommntyListViewList);
                pageCnt = pageCntTask+1;
            } else {
                Log.d("-진우-", "성공했으나 목록이 없습니다");
            }

            sqlCommntyListViewListAdapter.notifyDataSetChanged();
            mCurrentListViewState = "popular";
            dialog.dismiss();

            super.onPostExecute(sqlCommntyListViews);
        }
    }

    //더보기 - 스크롤리스너
    private AbsListView.OnScrollListener scrollListenerLatest = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
            //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastListViewVisible){
                //화면에 바닦에 닿았고, 스크롤이 멈추었다.
                Log.d("-진우-", "추가데이터 불러오기");
                if("latest".equals(mCurrentListViewState)){
                    FindCommntyLatestTask task = new FindCommntyLatestTask(pageCnt);
                    task.execute();
                } else if("popular".equals(mCurrentListViewState)){
                    FindCommntyPopularTask task = new FindCommntyPopularTask(pageCnt);
                    task.execute();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가
            //리스트 전제의 갯수(totalItemCount) - 1 보다 크거나 같을 때
            lastListViewVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            /*if(lastListViewVisible){
                Log.d("-진우-", "끝이다.");
            }*/
        }
    };
}