package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.pkmmte.view.CircularImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by dkfka on 2015-11-25.
 */
public class AdminActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private LinearLayout ic_screen;
    private ImageButton btn_left;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private EditText et_ref;
    private EditText et_adj;
    private EditText et_useq;
    private Button btn_ref, btn_adj, btn_useq, btn_end;

    //test 코드
    ADMINTCP_Task tc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_admin, ic_screen, true);

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
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' 아이를 선택하였습니다", Toast.LENGTH_LONG).show();
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

        et_ref = (EditText) findViewById(R.id.et_ref);
        et_adj = (EditText) findViewById(R.id.et_adj);
        et_useq = (EditText) findViewById(R.id.et_useq);

        btn_ref = (Button) findViewById(R.id.btn_ref);
        btn_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new EqAsyncTask().execute("192.168.4.1:80", "?REF", et_ref.getText() + "**");
                String command = et_ref.getText().toString();
                tc = new ADMINTCP_Task(getApplicationContext());
                tc.execute(command);

            }
        });
        btn_adj = (Button) findViewById(R.id.btn_adj);
        btn_adj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new EqAsyncTask().execute("192.168.4.1:80", "?ADJ", et_adj.getText() + "**");
                Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
                startActivity(intent);
                finish();

            }
        });
        btn_useq = (Button) findViewById(R.id.btn_useq);
        btn_useq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new EqAsyncTask().execute("192.168.4.1:80", "?USEQ", et_useq.getText() + "**");
                Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_end = (Button) findViewById(R.id.btn_end);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new EqAsyncTask().execute("192.168.4.1:80", "?END_SERVER", "END_SERVER");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "PwmodActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이목록, 계정이미지)
        //PwmodTask task = new PwmodTask();
        //task.execute(img_profile);

        Log.d("-진우-", "PwmodActivity 에서 onResume() : " + member.toString());

        Log.d("-진우-", "PwmodActivity.onResume() 끝");
    }

    private class ADMINTCP_Task extends AsyncTask<Object, Integer, ArrayList<String>> {

        protected String SERV_IP = "192.168.4.1"; //server ip
        protected int PORT = 80;

        private BufferedReader networkReader;
        private BufferedWriter networkWriter;
        ArrayList<String> response;
        private Context mContext;

        public ADMINTCP_Task(Context context) {
            mContext = context;


        }

        private ProgressDialog dialog;


        //Background 작업 시작전에 UI 작업을 진행 한다.
        @Override
        protected void onPreExecute() {
            dialog= new ProgressDialog(AdminActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(mContext.getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Object... params) {
            try {
                Log.d("TCP", "server connecting");
                String command = (String) params[0];


                Socket socket = new Socket(SERV_IP, PORT);
                networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ASCII"));
                WriteSocket(networkWriter, command);

                PrintWriter out = new PrintWriter(networkWriter, true);
                out.flush();

                String line;
                response = new ArrayList<String>();

                //디버깅을위한 list(0) command로
                response.add(command);
                if (command.equals("w")) {
                    int i = 0;
                    while (true) {
                        line = networkReader.readLine();
                        Log.d("1 line", line);
                        if (!line.contains("Signal:")) {//ssid 가 30비트 이상일 경우(길 경우) 개행이 되어 signal:이 포함되있지 않으면 read라인
                            if (!line.contains("number of available networks")) {//첫라인은 제외
                                line += networkReader.readLine();
                            }

                            Log.d("2 line", line);
                        }
                        Log.d("3 line", line);
                        response.add(line);
                        i++;
                        if (i == Integer.valueOf(response.get(1).substring(response.get(1).indexOf(":") + 1, response.get(1).length())) + 1) {//기기에서 보내준 wifi 갯수일때  break(첫라인은 info므로 +1)
                            break;
                        }
                    }
                    Log.w("-대경-", "while문 종료");

                } else {
                    line = networkReader.readLine();
                    Log.d("line", line);
                    response.add(line);
                }
                Log.d("response: ", response.toString());
                socket.close();
                networkWriter.close();
                networkReader.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(ArrayList<String> response) {
            super.onPostExecute(response);
            Toast.makeText(mContext, "명령어: " + response.get(0) + "response 1 Line:" + response.get(1), Toast.LENGTH_LONG).show();

            dialog.dismiss();

        }


        public void WriteSocket(BufferedWriter data, String s) throws IOException {
            //	data send
            data.write(s);
            data.flush();
            System.out.println("데이터를 송신 하였습니다.");
        }
    }
}