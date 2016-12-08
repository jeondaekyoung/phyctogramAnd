package com.knowledge_seek.phyctogram;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.listAdapter.UsersListManageAdapter;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.retrofitapi.UsersAPI;
import com.knowledge_seek.phyctogram.util.Utility;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dkfka on 2015-12-07.
 */
public class UsersManageActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;        //슬라이드 내 이름


    //레이아웃정의
    private Button btn_usersadd;
    private ListView lv_usersList_manage;
    private UsersListManageAdapter usersListManageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_users_manage, ic_screen, true);

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

        //슬라이드 메뉴 버튼
        btn_left = (ImageButton) findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLeftSlideAnimationToggle();
            }
        });

        //레이아웃정의
        btn_usersadd = (Button) findViewById(R.id.btn_usersadd);
        btn_usersadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(UsersManageActivity.this, "내아이 추가하러 가자", Toast.LENGTH_SHORT).show();
                //menuLeftSlideAnimationToggle();
                Intent intent = new Intent(getApplicationContext(), UsersAddActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        lv_usersList_manage = (ListView) findViewById(R.id.lv_usersList_manage);
        usersListManageAdapter = new UsersListManageAdapter(this);
        lv_usersList_manage.setAdapter(usersListManageAdapter);

        //리스트뷰 클릭 -> 내 아이 수정으로 이동
        lv_usersList_manage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Users users = (Users) usersListManageAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), UsersModActivity.class);
                intent.putExtra("users", users);
                startActivity(intent);
            }
        });

        //리스트뷰 롱클릭 -> 내 아이 삭제됨
        lv_usersList_manage.setLongClickable(true);
        lv_usersList_manage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Users users = (Users) usersListManageAdapter.getItem(position);
                //Log.d("-진우-", "삭제 : " + users.getName());

                AlertDialog.Builder dialog = new AlertDialog.Builder(UsersManageActivity.this);
                dialog.setTitle("'" + users.getName() + "' 삭제");
                dialog.setMessage(R.string.usersManageActivity_deleteChildAsk);
                dialog.setPositiveButton(R.string.commonActivity_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UsersAPI service = ServiceGenerator.createService(UsersAPI.class);
                        Call<String> call = service.delUsersByUserSeq(String.valueOf(users.getUser_seq()));
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Response<String> response, Retrofit retrofit) {
                                Log.d("-진우-", "내아이 삭제 성공 결과 : " + response.body());
                                /*Intent intent = new Intent(getApplicationContext(), UsersManageActivity.class);
                                intent.putExtra("member", member);
                                startActivity(intent);
                                finish();*/
                                Toast.makeText(getApplicationContext(), R.string.usersManageActivity_successDeleteChild, Toast.LENGTH_LONG).show();

                                if (nowUsers.getUser_seq() == users.getUser_seq()) {
                                    nowUsers = new Users();
                                    //nowUsers = null;
                                }

                                FindUsersTask task = new FindUsersTask();
                                task.execute();

                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("-진우-", R.string.usersManageActivity_failDeleteChild + t.getMessage() + ", " + t.getCause() + ", " + t.getStackTrace());
                            }
                        });
                    }
                });
                dialog.setNegativeButton(R.string.commonActivity_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

                return true;
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "UsersManageActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //내 아이 불러오기
        FindUsersTask task = new FindUsersTask();
        task.execute();

        //슬라이드메뉴 내 아이 목록 셋팅
        /*usersListSlideAdapter.setUsersList(usersList);
        int height = getListViewHeight(lv_usersList);
        lv_usersList.getLayoutParams().height = height;
        usersListSlideAdapter.notifyDataSetChanged();*/

        //내 아이 목록
        /*usersListManageAdapter.setUsersList(usersList);
        height = getListViewHeight(lv_usersList_manage);
        lv_usersList_manage.getLayoutParams().height = height;
        usersListManageAdapter.notifyDataSetChanged();*/


        //슬라이드메뉴 셋팅(내 아이목록, 계정이미지)
        //UsersManageTask task = new UsersManageTask();
        //task.execute(img_profile);

        Log.d("-진우-", "UsersManageActivity.onResume() : " + member.toString());

        Log.d("-진우-", "UsersManageActivity.onResume() 끝");
    }


    //내아이관리페이지 초기 데이터조회(슬라이드 내 아이목록, 계정이미지)
    private class UsersManageTask extends AsyncTask<Object, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(UsersManageActivity.this);
        private List<Users> usersTask;
        private CircularImageView img_profileTask;

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

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

            /*String image_url = null;
            if (member.getJoin_route().equals("kakao")) {
                image_url = member.getKakao_thumbnailimagepath();
                //이미지 불러오기
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

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*if(bitmap != null) {
                Log.d("-진우-", "이미지읽어옴");
                img_profileTask.setImageBitmap(bitmap);
            }

            if (usersTask != null && usersTask.size() > 0) {
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());
                for (Users u : usersTask) {
                    Log.d("-진우-", "내 아이 : " + u.toString());
                }
                usersList = usersTask;
                usersListSlideAdapter.setUsersList(usersList);
                if (nowUsers == null) {
                    nowUsers = usersTask.get(0);
                }
                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
                usersListManageAdapter.setUsersList(usersTask);
            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다.");
            }

            int height = getListViewHeight(lv_usersList);
            lv_usersList.getLayoutParams().height = height;
            usersListSlideAdapter.notifyDataSetChanged();
            height = getListViewHeight(lv_usersList_manage);
            lv_usersList_manage.getLayoutParams().height = height;
            usersListManageAdapter.notifyDataSetChanged();*/

            dialog.dismiss();
            super.onPostExecute(bitmap);
        }

    }

    //내 아이 목록 읽어오기
    private class FindUsersTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog = new ProgressDialog(UsersManageActivity.this);
        private List<Users> usersTask;

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

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(Void aVoid) {

            if (usersTask != null && usersTask.size() > 0) {
                Log.d("-진우-", "내 아이는 몇명? " + usersTask.size());
                Utility.compareList(usersList, usersTask);

                if (nowUsers.getUser_seq() == 0) {
                    nowUsers = usersList.get(0);
                }
                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
                //현재 선택된 내 아이를 맨 뒤로 이동
                Utility.seqChange(usersList, nowUsers.getUser_seq());
            } else {
                Log.d("-진우-", "성공했으나 등록된 내아이가 없습니다.");
                usersList.clear();
            }

            usersListSlideAdapter.setUsersList(usersList);
            usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
            lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
            usersListSlideAdapter.notifyDataSetChanged();

            usersListManageAdapter.setUsersList(usersList);
            lv_usersList_manage.getLayoutParams().height = getListViewHeight(lv_usersList_manage);
            usersListManageAdapter.notifyDataSetChanged();

            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}