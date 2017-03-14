package com.knowledge_seek.growCheck;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import com.knowledge_seek.growCheck.kakao.common.BaseActivity;

/**
 * Created by jdk on 2016-02-11!
 */
public class NoticeViewActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;        //슬라이드 내 이름

    //WebView webview;
    private int notice_seq;
    private String title;
    private String writing_de;
    private String notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_notice_view, ic_screen, true);

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

        //데이터셋팅
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            notice_seq = (int)bundle.getSerializable("notice_seq");
            title = (String)bundle.getSerializable("title");
            writing_de = (String)bundle.getSerializable("writing_de");
            notice = (String)bundle.getSerializable("notice");

            Log.d("-진우-", "공지사항 번호 : " + notice_seq);
            Log.d("-진우-", "공지사항 제목 : " + title);
            Log.d("-진우-", "공지사항 날짜 : " + writing_de);
            Log.d("-진우-", "공지사항 내용 : " + notice);
        }

        //텍스트뷰 스크롤바 달기
        TextView tv_content = (TextView) findViewById(R.id.notice_content);
        tv_content.setMovementMethod(new ScrollingMovementMethod());
        tv_content.setText(notice);

        //공지사항 타이틀
        TextView notice_title = (TextView) findViewById(R.id.notice_title);
        notice_title.setText(title);

        //공지사항 날짜
        TextView notice_date = (TextView) findViewById(R.id.notice_date);
        notice_date.setText(writing_de);

        /*webview = (WebView) findViewById(R.id.wb_notice);
        webview.setWebViewClient(new WebClient()); // 응룡프로그램에서 직접 url 처리
        WebSettings set = webview.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(true);
        webview.loadUrl("http://www.phyctogram.com/notice/view.do?notice_seq=" + notice_seq);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "NoticeViewActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

     //   Log.d("-진우-", "NoticeViewActivity.onResume() : " + member.toString());

        Log.d("-진우-", "NoticeViewActivity.onResume() 끝");
    }

    /*class WebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }*/
}
