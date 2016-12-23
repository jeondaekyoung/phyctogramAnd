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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Comment;
import com.knowledge_seek.phyctogram.domain.SqlCommntyListView;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.CommentAPI;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.List;

import retrofit.Call;

/**
 * Created by dkfka on 2015-12-08.
 */
public class CommunityCommentActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private EditText et_content;
    private Button btn_comment_register;

    //데이터
    private SqlCommntyListView sqlCommntyListView;
    private Comment comment = new Comment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_community_comment, ic_screen, true);

        //데이터셋팅
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            sqlCommntyListView = (SqlCommntyListView)bundle.getSerializable("sqlCommntyListView");
            Log.d("-진우-", "CommunityCommentActivity 에서 " + sqlCommntyListView.toString());
        } else {
            Log.d("-진우-", "CommunityCommentActivity 에 sqlCommntyListView가 없다");
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
        btn_left = (ImageButton)findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });

        et_content = (EditText)findViewById(R.id.et_content);
        btn_comment_register = (Button)findViewById(R.id.btn_comment_register);
        btn_comment_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //내용체크
                comment.setContent(et_content.getText().toString());
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "CommunityCommentActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지)
        //CommunityCommentTask task = new CommunityCommentTask();
        //task.execute(img_profile);

        //Log.d("-진우-", "CommunityCommentActivity 에 onResume() : " + member.toString());

        Log.d("-진우-", "CommunityCommentActivity.onResume() 끝");
    }

    //comment의 내용 체크
    private boolean checkComment(Comment comment){
        if(comment.getContent().length() <= 0){
            Toast.makeText(getApplicationContext(), R.string.communityCommentActivity_writeContents, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //댓글쓰기페이지 초기 데이터조회(슬라이드 내 아이 목록, 내 이미지)
    private class CommunityCommentTask extends AsyncTask<Object, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(CommunityCommentActivity.this);
        private List<Users> usersTask;
        private CircularImageView img_profileTask;

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
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            img_profileTask = (CircularImageView) params[0];

            //슬라이드메뉴에 있는 내 아이 목록
            /*UsersAPI service = ServiceGenerator.createService(UsersAPI.class, "Users");
            Call<List<Users>> call = service.findUsersByMember(String.valueOf(member.getMember_seq()));
            try {
                usersTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 목록 가져오기 실패");
            }*/

            //이미지 불러오기
            /*String image_url = null;
            if (member.getJoin_route().equals("kakao")) {
                image_url = member.getKakao_thumbnailimagepath();
                InputStream in = null;
                try {
                    Log.d("-진우-", "이미지 주소 : " + image_url);
                    in = new URL(image_url).openStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (member.getJoin_route().equals("facebook")) {
                image_url = "http://graph.facebook.com/" + member.getFacebook_id() + "/picture?type=large";
                //이미지 불러오기
                InputStream in = null;
                try {
                    //페이스북은 jpg파일이 링크 걸린 것이 아니다.
                    //http://graph.facebook.com/userid/picture?type=large
                    Log.d("-진우-", "이미지 주소 : " + image_url);

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(image_url)
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    in = response.body().byteStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
            return mBitmap;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*if (bitmap != null) {
                Log.d("-진우-", "이미지읽어옴");
                img_profileTask.setImageBitmap(bitmap);
            }

            if (usersTask != null && usersTask.size() > 0) {
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());
                for (Users u : usersTask) {
                    Log.d("-진우-", "내 아이 : " + u.toString());
                }

                if (nowUsers == null) {
                    nowUsers = usersTask.get(0);
                }
                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
                usersList = usersTask;
                //현재 선택된 내 아이를 맨 뒤로 이동
                usersList = Utility.seqChange(usersList, nowUsers.getUser_seq());
                usersListSlideAdapter.setUsersList(usersList);
            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다");
            }

            int height = getListViewHeight(lv_usersList);
            lv_usersList.getLayoutParams().height = height;
            usersListSlideAdapter.notifyDataSetChanged();*/

            dialog.dismiss();
            super.onPostExecute(bitmap);
        }
    }

    //댓글 쓰기
    private class RegisterCommentTask extends AsyncTask<Void, Void, String>{

        private Comment comment;
        private ProgressDialog dialog = new ProgressDialog(CommunityCommentActivity.this);

        public RegisterCommentTask(Comment comment){
            this.comment = comment;
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

            dialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
