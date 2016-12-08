package com.knowledge_seek.phyctogram;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Comment;
import com.knowledge_seek.phyctogram.domain.Commnty;
import com.knowledge_seek.phyctogram.domain.SqlCommntyListView;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.listAdapter.CommentListAdapter;
import com.knowledge_seek.phyctogram.retrofitapi.CommentAPI;
import com.knowledge_seek.phyctogram.retrofitapi.CommntyAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;

/**
 * Created by dkfka on 2015-12-04.
 */
public class CommunityViewActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드 메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃 정의
    private TextView tv_title;              //제목
    private TextView tv_name;            //이름
    private TextView tv_writng_de;     //작성날짜
    private TextView tv_hits_co;         //조회수
    private TextView tv_cnt;               //댓글수
    private TextView tv_contents;       //내용
    private ListView lv_comments;        //댓글 리스트
    private CommentListAdapter commentListAdapter;
    private EditText et_comment;        //댓글
    private Button btn_comment_register;        //댓글 등록

    //데이터정의
    private SqlCommntyListView sqlCommntyListView = new SqlCommntyListView(); //커뮤니티 리스트 객체
    private Commnty commnty = new Commnty(); //커뮤니티 내용 객체
    private List<Comment> CommentList = new ArrayList<Comment>();
    private Comment comment = new Comment();        //댓글객체


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_community_view, ic_screen, true);

        //데이터셋팅
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            sqlCommntyListView = (SqlCommntyListView)bundle.getSerializable("sqlCommntyListView");
            Log.d("-진우-", "CommunityViewActivity 에서 " + sqlCommntyListView.toString());
        } else {
            Log.d("-진우-", "CommunityViewActivity 에 sqlCommntyListView가 없다.");
        }

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

        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(sqlCommntyListView.getTitle());
        tv_name = (TextView)findViewById(R.id.tv_name);
        tv_name.setText(sqlCommntyListView.getName());
        tv_writng_de = (TextView)findViewById(R.id.tv_writng_de);
        tv_writng_de.setText(sqlCommntyListView.getWritng_de());
        tv_hits_co = (TextView)findViewById(R.id.tv_hits_co);
        tv_hits_co.setText(String.valueOf(sqlCommntyListView.getHits_co()));
        tv_cnt = (TextView)findViewById(R.id.tv_cnt);
        tv_cnt.setText(new StringBuilder().append(getString(R.string.communityViewActivity_comment)).append(sqlCommntyListView.getCnt()).append(" "+getString(R.string.communityViewActivity_number)).toString());
        tv_contents = (TextView)findViewById(R.id.tv_contents);

        //댓글 목록
        lv_comments = (ListView)findViewById(R.id.lv_comments);
        commentListAdapter = new CommentListAdapter(this, CommentList, R.layout.list_comment);
        lv_comments.setAdapter(commentListAdapter);

        //댓글
        et_comment = (EditText)findViewById(R.id.et_comment);
        btn_comment_register = (Button)findViewById(R.id.btn_comment_register);
        btn_comment_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //내용체크
                comment.setContent(et_comment.getText().toString());
                comment.setMember_seq(member.getMember_seq());
                comment.setCommnty_seq(sqlCommntyListView.getCommnty_seq());

                if(!checkComment(comment)){
                    return ;
                }

                //Log.d("-진우-", "댓글 저장하기 : " + comment.toString());
                //Log.d("-진우-", "json : " + Utility.comment2json(comment));
                //댓글 저장하기
                RegisterCommentTask task = new RegisterCommentTask(comment);
                task.execute();
            }
        });
        /*et_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), CommunityCommentActivity.class);
                //intent.putExtra("member", member);
                //intent.putExtra("sqlCommntyListView", sqlCommntyListView);
                //startActivity(intent);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "CommunityViewActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지)
        //CommunityViewTask task = new CommunityViewTask();
        //task.execute(img_profile);

        Log.d("-진우-", "CommunityViewActivity 에 onResume() : " + member.toString());

        //수다방 내용 읽어오기
        FindCommntyAndCommentTask task1 = new FindCommntyAndCommentTask(sqlCommntyListView.getCommnty_seq());
        task1.execute();

        Log.d("-진우-", "CommunityViewActivity.onResume() 끝");
    }

    //댓글 내용 체크
    private boolean checkComment(Comment comment){
        if(comment.getContent().length() <= 0){
            Toast.makeText(getApplicationContext(), R.string.communityViewActivity_writeComment, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //댓글 쓰기
    private class RegisterCommentTask extends AsyncTask<Void, Void, String>{
        private Comment comment;
        //private ProgressDialog dialog = new ProgressDialog(CommunityViewActivity.this);

        public RegisterCommentTask(Comment comment){
            this.comment = comment;
        }

        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            //dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //dialog.setMessage("잠시만 기다려주세요");
            //dialog.show();
            super.onPreExecute();
        }

        //Background 작업을 진행 한다.
        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            CommentAPI service = ServiceGenerator.createService(CommentAPI.class, "Comment");
            Call<String> call = service.registerComment(comment);
            try {
                result = call.execute().body();
            } catch (IOException e){
                Log.d("-진우-", "댓글저장 실패");
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

            //수다방 내용 읽어오기
            FindCommntyAndCommentTask task1 = new FindCommntyAndCommentTask(sqlCommntyListView.getCommnty_seq());
            task1.execute();

            //dialog.dismiss();
            super.onPostExecute(result);
        }
    }


    //수다방 내용 읽어오기
    private class FindCommntyAndCommentTask extends AsyncTask<Void, Void, Void>{

        private int commnty_seqTask;
        private Commnty commntyTask;
        private List<Comment> commentListTask = new ArrayList<Comment>();
        private ProgressDialog dialog = new ProgressDialog(CommunityViewActivity.this);

        public FindCommntyAndCommentTask(int commnty_seqTask) {
            this.commnty_seqTask = commnty_seqTask;
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
        protected Void doInBackground(Void... params) {
            CommntyAPI service = ServiceGenerator.createService(CommntyAPI.class, "Commnty");
            Call<Commnty> call = service.findCommntyByCommntySeq(commnty_seqTask);
            try {
                commntyTask = call.execute().body();
            } catch (IOException e){
                Log.d("-진우-", "수다방 조회 실패");
            }

            CommentAPI service1 = ServiceGenerator.createService(CommentAPI.class, "Comment");
            Call<List<Comment>> call1 = service1.findCommentByCommntySeq(commnty_seqTask);
            try {
                commentListTask = call1.execute().body();
            } catch (IOException e){
                Log.d("-진우-", "수다방 댓글 조회 실패");
            }
            return null;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Void aVoid) {
            if(commntyTask != null){
                Log.d("-진우-", "읽어온 커뮤니티 : " + commntyTask.toString());
                commnty = commntyTask;
                tv_contents.setText(commnty.getContents());
            } else {
                Log.d("-진우-", "읽어온 커뮤니티가 없습니다");
            }
            if(commentListTask != null && commentListTask.size() > 0){
                for(Comment c : commentListTask){
                    Log.d("-진우-", "목록 : " + c.toString());
                }
                Log.d("-진우-", "읽어온 목록은 " + commentListTask.size() + " 개 있습니다");

                CommentList = commentListTask;
                commentListAdapter.setComments(commentListTask);
            }

            int height = getListViewHeight(lv_comments);
            lv_comments.getLayoutParams().height = height;

            commentListAdapter.notifyDataSetChanged();

            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}