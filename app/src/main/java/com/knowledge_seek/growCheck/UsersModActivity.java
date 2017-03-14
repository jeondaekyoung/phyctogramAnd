package com.knowledge_seek.growCheck;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.growCheck.domain.Users;
import com.knowledge_seek.growCheck.kakao.common.BaseActivity;
import com.knowledge_seek.growCheck.retrofitapi.ServiceGenerator;
import com.knowledge_seek.growCheck.retrofitapi.UsersAPI;
import com.knowledge_seek.growCheck.util.Utility;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dkfka on 2015-11-25.
 */
public class UsersModActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름


    //레이아웃 정의
    private EditText et_name;
    private EditText et_initials;
    private DatePicker dp_lifedate;
    private RadioButton rb_female;
    private RadioButton rb_male;
    private Button btn_usersmod;

    //데이터
    private Users users = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_users_mod, ic_screen, true);


        //데이터셋팅
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            users = (Users) bundle.getSerializable("users");
            Log.d("-진우-", "UsersModActivity 에서 " + users.toString());
        } else {
            Log.d("-진우-", "UsersModActivity 에 users가 없다");
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
                nowUsers = (Users) usersListSlideAdapter.getItem(position);
                users = nowUsers;
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' "+getString(R.string.characterActivity_choiceChild), Toast.LENGTH_LONG).show();

                //선택 아이로 인한 순서 변경
                Utility.seqChange(usersList, nowUsers.getUser_seq());
                //내 아이 목록 셋팅
                usersListSlideAdapter.setUsersList(usersList);
                usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
                usersListSlideAdapter.notifyDataSetChanged();

                init();
            }
        });

        //사이드 메뉴 버튼
        btn_left = (ImageButton) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });

        //레이아웃 정의
        et_name = (EditText)findViewById(R.id.et_name);
        et_initials = (EditText)findViewById(R.id.et_initials);
        dp_lifedate = (DatePicker)findViewById(R.id.dp_lifedate);
        rb_female = (RadioButton)findViewById(R.id.rb_female);
        rb_male = (RadioButton)findViewById(R.id.rb_male);
        btn_usersmod = (Button)findViewById(R.id.btn_usersmod);
        btn_usersmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.setName(et_name.getText().toString());
                users.setInitials(et_initials.getText().toString());
                users.setLifyea(String.valueOf(dp_lifedate.getYear()));
                users.setMt(Utility.dateFormat(dp_lifedate.getMonth() + 1));
                users.setDe(Utility.dateFormat(dp_lifedate.getDayOfMonth()));
                if (rb_female.isChecked()) {
                    users.setSexdstn("female");
                } else if (rb_male.isChecked()) {
                    users.setSexdstn("male");
                }

                if (!checkUsers(users)) {
                    return;
                }

                Log.d("-진우-", "내 아이 수정하기 : " + users.toString());
                Log.d("-진우-", "json : " + Utility.users2json(users));
                UsersAPI service = ServiceGenerator.createService(UsersAPI.class);
                Call<String> call = service.modUsersByUsers(users);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        Log.d("-진우-", "내 아이 수정 성공 결과 : " + response.body());
                        if (response.body().equals("success")) {
                            Toast.makeText(UsersModActivity.this, R.string.diaryViewActivity_modifyAlert, Toast.LENGTH_LONG).show();
                        }
                        FindUsersTask task = new FindUsersTask();
                        task.execute();

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("-진우-", "내 아이 수정 실패 " + t.getMessage() + ", " + t.getCause() + ", " + t.getStackTrace());
                    }
                });
            }
        });

        init();
    }

    //화면에 데이터 셋팅
    private void init() {
        if(users != null){
            et_name.setText(users.getName());
            et_initials.setText(users.getInitials());
            dp_lifedate.updateDate(Integer.valueOf(users.getLifyea()), Integer.valueOf(users.getMt()) - 1, Integer.valueOf(users.getDe()));
            if(users.getSexdstn().equals("male")){
                rb_male.setChecked(true);
                rb_female.setChecked(false);
            } else {
                rb_female.setChecked(true);
                rb_male.setChecked(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "UsersModActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();


       // Log.d("-진우-", "UsersModActivity.onResume() : " + member.toString());

        Log.d("-진우-", "UsersModActivity.onResume() 끝");
    }

    //users의 내용 체크
    private boolean checkUsers(Users users){
        //Log.d("-진우-", users.toString());
        if(users.getName().length() <= 0){
            Toast.makeText(getApplicationContext(), R.string.usersAddActivity_checkName, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(users.getInitials().length() <= 0 || users.getInitials().length() >= 4) {
            Toast.makeText(getApplicationContext(), R.string.usersAddActivity_checkInitials, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            //Toast.makeText(getApplicationContext(), users.getInitials() + " : " + users.getInitials().matches("^[A-Z0-9]*$"), Toast.LENGTH_SHORT).show();
            if(!users.getInitials().matches("^[A-Z0-9]*$")){
                Toast.makeText(getApplicationContext(), R.string.usersAddActivity_checkInitials, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //내 아이 목록 읽어오기
    private class FindUsersTask extends AsyncTask<Void, Void, Void>{

        private ProgressDialog dialog = new ProgressDialog(UsersModActivity.this);
        private List<Users> usersTask;

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //내 아이 목록
            UsersAPI service = ServiceGenerator.createService(UsersAPI.class, "Users");
            Call<List<Users>> call = service.findUsersByMember(String.valueOf(member.getMember_seq()));
            try {
                usersTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 목록 가져오기 실패");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(usersTask != null && usersTask.size() > 0){
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());

                //수정한 내 아이로 교체
                for(Users ut : usersTask) {
                    if(ut.getUser_seq() == users.getUser_seq()) {
                        for(int i = 0; i<usersList.size(); i++) {
                            if(usersList.get(i).getUser_seq() == users.getUser_seq()) {
                                usersList.get(i).setMember_seq(ut.getMember_seq());
                                usersList.get(i).setSexdstn(ut.getSexdstn());
                                usersList.get(i).setLifyea(ut.getLifyea());
                                usersList.get(i).setMt(ut.getMt());
                                usersList.get(i).setDe(ut.getDe());
                                usersList.get(i).setInitials(ut.getInitials());
                                usersList.get(i).setName(ut.getName());
                                break;
                            }
                        }
                        break;
                    }
                }

                //nowUsers를 수정한 아이라면 수정한 후의 데이터로 교체
                for(Users ul : usersList){
                    if(ul.getUser_seq() == nowUsers.getUser_seq()){
                        nowUsers = ul;
                    }
                }

                //usersListSlideAdapter.setUsersList(usersList);

                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다.");
            }
            /*int height = getListViewHeight(lv_usersList);
            lv_usersList.getLayoutParams().height = height;
            usersListSlideAdapter.notifyDataSetChanged();*/

            onBackPressed();

            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
