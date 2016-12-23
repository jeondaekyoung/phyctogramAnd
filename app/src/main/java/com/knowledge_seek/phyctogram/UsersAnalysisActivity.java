
package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.knowledge_seek.phyctogram.domain.Height;
import com.knowledge_seek.phyctogram.domain.Users;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.retrofitapi.UsersAPI;
import com.knowledge_seek.phyctogram.util.Utility;
import com.pkmmte.view.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Call;

public class UsersAnalysisActivity extends BaseActivity {

    //레이아웃 - 슬라이드 메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //리포트 공유 팝업
    private PopupWindow popup;
    private Button btn_share;

    private LinearLayout ll_capture;                   //캡쳐화면
    private Bitmap captureView;                         //캡처화면 저장할 비트맵
    private TextView tv_users_name;               //아이 이름 출력
    private ImageView iv_my_animal;                        //캐릭터
    private TextView tv_height;                         //최종신장
    private TextView tv_grow;                           //성장 값
    private TextView tv_rank;                           //상위
    private TextView tv_analysis_height50;          //분석
    private ImageView iv_analysis_height50_diff;
    private TextView tv_analysis_height50_diff;
    private TextView tv_analysis_height;
    private ImageView iv_analysis_height_diff;
    private TextView tv_analysis_height_diff;


    //그래프 레이아웃
    private CombinedChart mChart;
    private YAxis rightAxis;
    private YAxis leftAxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("-진우-", "UsersAnalysisActivity.onCreate() 실행");

        //화면페이지
        ic_screen = (LinearLayout)findViewById(R.id.ic_screen);
        LayoutInflater.from(this).inflate(R.layout.include_analysis, ic_screen, true);

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
                nowUsers = (Users)usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' "+getString(R.string.characterActivity_choiceChild), Toast.LENGTH_LONG).show();

                tv_users_name.setText(nowUsers.getName());

                //선택 아이로 인한 순서 변경
                Utility.seqChange(usersList, nowUsers.getUser_seq());
                //내 아이 목록 셋팅
                usersListSlideAdapter.setUsersList(usersList);
                usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
                usersListSlideAdapter.notifyDataSetChanged();

                //내 아이 메인(분석) 정보 계산하기
                FindUsersAnalysisInfoTask task = new FindUsersAnalysisInfoTask();
                task.execute();

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

        ll_capture = (LinearLayout)findViewById(R.id.ll_capture);
        //리포트 공유 팝업
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //뷰가 업데이트 될 때마다 그 때의 뷰 이미지를 Drawing cache에 저장할지 여부를 결정합니다.
                ll_capture.setDrawingCacheEnabled(true);
                //뷰 이미지를 Drawing cache에 저장합니다. Drawing cache enabled 속성이 활성화되어 있다면 이 메서드를 호출하지 않아도 자동으로 Drawing cache에 뷰의 이미지가 저장됩니다.
                //ll_capture.buildDrawingCache();
                //Drawing cache에 저장된 뷰의 이미지를 Bitmap 형태로 반환합니다.
                Bitmap cache = ll_capture.getDrawingCache();

                View popupView = getLayoutInflater().inflate(R.layout.activity_report_share, null);
                //레이아웃 정의
                Button btn_close = (Button)popupView.findViewById(R.id.btn_close);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });
                ImageView iv_capture= (ImageView)popupView.findViewById(R.id.iv_capture);
                captureView = Bitmap.createBitmap(cache);
                iv_capture.setImageBitmap(captureView);

                //실제로 올릴 이미지 캡쳐
                final LinearLayout ll_capture_2 = (LinearLayout) popupView.findViewById(R.id.ll_capture_2);

                ImageButton imBtn_fb = (ImageButton)popupView.findViewById(R.id.imBtn_fb);
                imBtn_fb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getApplicationContext(), "페이스북에 공유하기", Toast.LENGTH_SHORT).show();

                        //카카오스토리 설치여부 확인
                        PackageManager pm = getPackageManager();
                        try {
                            String appPackage = "com.facebook.katana";
                            PackageInfo pi = pm.getPackageInfo(appPackage, PackageManager.GET_ACTIVITIES);
                        } catch (PackageManager.NameNotFoundException e) {
                            Toast.makeText(getApplicationContext(), R.string.usersAnalysisActivity_initFacebook, Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        //실제로 올릴 이미지 캡쳐-2
                        ll_capture_2.setDrawingCacheEnabled(true);
                        Bitmap cache_2 = ll_capture_2.getDrawingCache();
                        Bitmap captureView_2 = Bitmap.createBitmap(cache_2);
                        Uri uri = Uri.fromFile(Utility.saveBitmaptoJpeg(captureView_2));

                        /*FacebookShare fb = new FacebookShare(UsersAnalysisActivity.this);
                        fb.setImage(captureView_2);     //캡쳐이미지 셋팅
                        fb.onClickPostPhoto();*/
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        //msg.addCategory(Intent.CATEGORY_DEFAULT);
                        //intent.putExtra(Intent.EXTRA_SUBJECT, "픽토그램");
                        //intent.putExtra(Intent.EXTRA_TEXT, "내 아이 성장");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.setType("image/jpg");
                        intent.setPackage("com.facebook.katana");
                        //startActivity(Intent.createChooser(intent, "내 아이 성장을 공유합니다"));
                        startActivity(intent);
                    }
                });
                ImageButton imBtn_ks = (ImageButton)popupView.findViewById(R.id.imBtn_ks);
                imBtn_ks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getApplicationContext(), "카카오스토리에 공유하기", Toast.LENGTH_SHORT).show();

                        //카카오스토리 설치여부 확인
                        PackageManager pm = getPackageManager();
                        try {
                            String appPackage = "com.kakao.story";
                            PackageInfo pi = pm.getPackageInfo(appPackage, PackageManager.GET_ACTIVITIES);
                        } catch (PackageManager.NameNotFoundException e) {
                            Toast.makeText(getApplicationContext(), R.string.usersAnalysisActivity_initkakaoStroy, Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        //실제로 올릴 이미지 캡쳐-2
                        ll_capture_2.setDrawingCacheEnabled(true);
                        Bitmap cache_2 = ll_capture_2.getDrawingCache();
                        Bitmap captureView_2 = Bitmap.createBitmap(cache_2);
                        Uri uri = Uri.fromFile(Utility.saveBitmaptoJpeg(captureView_2));

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        //msg.addCategory(Intent.CATEGORY_DEFAULT);
                        //intent.putExtra(Intent.EXTRA_SUBJECT, "픽토그램");
                        //intent.putExtra(Intent.EXTRA_TEXT, "내 아이 성장");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.setType("image/*");
                        intent.setPackage("com.kakao.story");
                        //startActivity(Intent.createChooser(intent, "내 아이 성장을 공유합니다"));
                        startActivity(intent);

                    }
                });
                ImageButton imBtn_kt = (ImageButton)popupView.findViewById(R.id.imBtn_kt);
                imBtn_kt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getApplicationContext(), "카카오톡에 공유하기", Toast.LENGTH_SHORT).show();

                        //카카오톡 설치여부 확인
                        PackageManager pm = getPackageManager();
                        try {
                            String appPackage = "com.kakao.talk";
                            PackageInfo pi = pm.getPackageInfo(appPackage, PackageManager.GET_ACTIVITIES);
                        } catch (PackageManager.NameNotFoundException e) {
                            Toast.makeText(getApplicationContext(), R.string.usersAnalysisActivity_initkakao, Toast.LENGTH_SHORT).show();
                            /*Uri uri = Uri.parse("market://search?q=pname:com.kakao.talk");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);*/
                            return ;
                        }

                        //실제로 올릴 이미지 캡쳐-2
                        ll_capture_2.setDrawingCacheEnabled(true);
                        Bitmap cache_2 = ll_capture_2.getDrawingCache();
                        Bitmap captureView_2 = Bitmap.createBitmap(cache_2);
                        Uri uri = Uri.fromFile(Utility.saveBitmaptoJpeg(captureView_2));

                        //이미지공유
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.setType("image");

                        //텍스트(URL) 공유
                        /*Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "URL 테스트");
                        intent.putExtra(Intent.EXTRA_TEXT, "http://www.knowledge-seek.com");*/


                        intent.setPackage("com.kakao.talk");
                        //startActivity(Intent.createChooser(intent, "내 아이 성장을 공유합니다"));
                        startActivity(intent);

                    }
                });

                popup = new PopupWindow(popupView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                popup.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)
                popup.showAtLocation(popupView, Gravity.CENTER, 0, -100);

                ll_capture.setDrawingCacheEnabled(false);
            }
        });

        //그래프 셋팅
        mChart = (CombinedChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });
        /*mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });*/

        rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setStartAtZero(false);
        rightAxis.setEnabled(false);

        leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setStartAtZero(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //내 아이 메인(분석) 정보 등
        iv_my_animal = (ImageView)findViewById(R.id.iv_my_animal);
        iv_my_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
                //intent.putExtra("member", member);
                startActivity(intent);
                //finish();
            }
        });
        tv_height = (TextView)findViewById(R.id.tv_height);
        tv_grow = (TextView)findViewById(R.id.tv_grow);
        tv_rank = (TextView)findViewById(R.id.tv_rank);
        tv_analysis_height50 = (TextView)findViewById(R.id.tv_analysis_height50);
        iv_analysis_height50_diff = (ImageView)findViewById(R.id.iv_analysis_height50_diff);
        tv_analysis_height50_diff = (TextView)findViewById(R.id.tv_analysis_height50_diff);
        tv_analysis_height = (TextView)findViewById(R.id.tv_analysis_height);
        iv_analysis_height_diff = (ImageView)findViewById(R.id.iv_analysis_height_diff);
        tv_analysis_height_diff = (TextView)findViewById(R.id.tv_analysis_height_diff);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "UsersAnalysisActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        lv_usersList.getLayoutParams().height = getListViewHeight(lv_usersList);
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지, 내 아이 메인(분석)정보)
        /*UsersAnalysisTask task = new UsersAnalysisTask();
        task.execute(img_profile);*/

        //내 아이 메인(분석) 정보 계산하기
        FindUsersAnalysisInfoTask task = new FindUsersAnalysisInfoTask();
        task.execute();

        //Log.d("-진우-", "UsersAnalysisActivity 에 onResume() : " + member.toString());

        Log.d("-진우-", "UsersAnalysisActivity.onResume() 끝");
    }

    //그래프에 데이터 셋팅
    private void drawGraph(List<Height> heights){

        Collections.reverse(heights);                                      //역순정렬

        List<String> xAxis = new ArrayList<String>();                        //x축
        ArrayList<BarEntry> height50 = new ArrayList<BarEntry>();      //평균
        ArrayList<Entry> heightMy = new ArrayList<Entry>();              //내키

        for (int index = 0 ; index < heights.size(); index++){
            //Log.d("-진우-", "키 데이터 : " + heights.get(index).toString());
            if (index==0){
                if((float) heights.get(index).getHeight_50()<(float) heights.get(index).getHeight()){
                    leftAxis.setAxisMinValue((float) heights.get(index).getHeight_50());
                }else{
                    leftAxis.setAxisMinValue((float) heights.get(index).getHeight());
                }

            }
            String str = heights.get(index).getMesure_date().substring(2);//날짜에서 년도 '20' 삭제
            Log.d("mesure_date","drawGraph: "+str);
            //xAxis.add(String.valueOf(new StringBuilder().append(str.substring(5, 7)).append("/").append(str.substring(8, 10))));
            xAxis.add(String.valueOf(new StringBuilder().append(str.replaceAll("-","/"))));
            height50.add(new BarEntry((float) heights.get(index).getHeight_50(), index));
            heightMy.add(new Entry( (float)heights.get(index).getHeight(), index));
            Log.d("-진우-", "소수점 확인 : " + Float.parseFloat(String.format("%.1f", heights.get(index).getHeight_50())) + ", " + heights.get(index).getHeight_50());
        }

        CombinedData data = new CombinedData(xAxis);
        data.setData(generateLineData(heightMy));
        data.setData(generateBarData(height50));
        mChart.setData(data);
        mChart.invalidate();

    }



    /**
     * 내 아이 성장곡선
     * @return LineData
     */
    private LineData generateLineData(ArrayList<Entry> my) {

        LineData d = new LineData();

        LineDataSet set = new LineDataSet(my, getString(R.string.usersAnalysisActivity_childChart));
        set.setColor(Color.rgb(151, 118, 197));
        set.setLineWidth(2.0f);


        set.setCircleColor(Color.rgb(151, 118, 197));
        set.setCircleSize(5f);
        set.setFillColor(Color.rgb(151, 118, 197));
        set.setDrawCubic(true);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(151, 118, 197));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);
        return d;
    }

    /**
     * 평균 성장 그래프
     * @return BarData
     */
    private BarData generateBarData(ArrayList<BarEntry> ave) {

        BarData d = new BarData();

        BarDataSet set = new BarDataSet(ave, getString(R.string.usersAnalysisActivity_avgChart));
        set.setColor(Color.rgb(255, 240, 205));
        set.setValueTextColor(Color.rgb(255, 200, 90));
        set.setValueTextSize(10f);

        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    //분석페이지 초기 데이터조회(슬라이드 내 아이 목록, 내 이미지)
    private class UsersAnalysisTask extends AsyncTask<Object, Void, Bitmap> {

        private ProgressDialog dialog = new ProgressDialog(UsersAnalysisActivity.this);
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
                usersList = usersTask;

                usersListSlideAdapter.setUsersList(usersList);
                if (nowUsers == null) {
                    nowUsers = usersTask.get(0);
                }
                Log.d("-진우-", "메인 유저는 " + nowUsers.toString());
                tv_users_name.setText(nowUsers.getName());
                //내 아이 메인(분석) 정보 계산하기
                FindUsersAnalysisInfoTask task = new FindUsersAnalysisInfoTask();
                task.execute();
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

    //내 아이 메인(분석) 정보 계산하기
    //
    private class FindUsersAnalysisInfoTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog = new ProgressDialog(UsersAnalysisActivity.this);
        private List<Height> heightTask = new ArrayList<Height>();
        private List<Height> analysisTask = new ArrayList<Height>();

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.commonActivity_wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //내 아이 메인(분석)정보 계산하기
            UsersAPI service = ServiceGenerator.createService(UsersAPI.class, "Height");
            Call<List<Height>> call = service.findUsersMainInfoByUserSeq(nowUsers.getUser_seq());
            try {
                heightTask = call.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 메인(분석) 정보 실패");
            }

            //내 아이 키성장 분석
            UsersAPI service1 = ServiceGenerator.createService(UsersAPI.class, "Height");
            Call<List<Height>> call1 = service.findUsersAnalysisByUserSeq(nowUsers.getUser_seq());
            try {
                analysisTask = call1.execute().body();
            } catch (IOException e) {
                Log.d("-진우-", "내 아이 키성장 분석 실패");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("-진우-", heightTask.size() + " 개 조회(메인분석)");
            /*for (Height h : heightTask) {
                Log.d("-진우-", "최근신장 : " + h.toString());
            }*/
            //성장키 계산
            for (int i = 0; i < heightTask.size()-1; i++) {
                heightTask.get(i).setGrow(String.format("%.1f", (heightTask.get(i).getHeight() - heightTask.get(i + 1).getHeight()) ));
            }

            if(heightTask.size() == 0){
                //기록이 없으면 끝
                iv_my_animal.setImageResource(R.drawable.sample);
                tv_height.setText("-");
                tv_grow.setText("-");
                tv_rank.setText("-");
                tv_analysis_height50.setText("-");
                tv_analysis_height50_diff.setText("-");
                tv_analysis_height.setText("-");
                tv_analysis_height_diff.setText("-");
                mChart.clear();
                dialog.dismiss();
                super.onPostExecute(aVoid);
                return;
            }

            //내 아이 이미지
            String imgName = "@drawable/" + heightTask.get(0).getAnimal_img().substring(0,12);
            String packName = self.getPackageName();
            Log.d("-진우-", "확인 : " + imgName + ", " + packName);
            iv_my_animal.setImageResource(getResources().getIdentifier(imgName, "drawable", packName));
            //최종신장
            tv_height.setText(String.format("%.1f", heightTask.get(0).getHeight()));
            //성장키
            if(heightTask.size() == 2) {
                if (Double.valueOf(heightTask.get(0).getGrow()) >= 0) {
                    tv_grow.setText("+" + heightTask.get(0).getGrow());
                }
            }
            //상위
            tv_rank.setText(String.valueOf(heightTask.get(0).getRank()));

            //키 성장 분석 합니다....
            int analysisSize = analysisTask.size();
            if(analysisSize <= 0){
                //키성장 분석할 키 데이터가 없다
                tv_analysis_height50.setText("-");
                tv_analysis_height50_diff.setText("-");
                tv_analysis_height.setText("-");
                tv_analysis_height_diff.setText("-");
                mChart.clear();
                dialog.dismiss();
                super.onPostExecute(aVoid);
                return;
            }


            Log.d("-진우-", analysisSize + " 개 조회(키성장분석)");
            for(Height h : analysisTask) {
                Log.d("-진우-", "키성장분석 : " + h.toString());
            }
            StringBuilder analysis_height50 = new StringBuilder();
            /*analysis_height50.append(getString(R.string.usersAnalysisActivity_avg)+" ").append(analysisTask.get(0).getHeight_50()).append("cm 보다");*/
            analysis_height50.append(getString(R.string.usersAnalysisActivity_avg));
            double analysis_height050_diff = Double.parseDouble(String.format("%.1f", analysisTask.get(0).getHeight() - analysisTask.get(0).getHeight_50()));
            //Log.d("-진우-", "분석 결과 : " + analysis_height50 + ", " + analysis_height050_diff);
            tv_analysis_height50.setText(analysis_height50);
            if(analysis_height050_diff > 0){
                iv_analysis_height50_diff.setImageResource(R.drawable.icon_report_up);
            } else {
                iv_analysis_height50_diff.setImageResource(R.drawable.icon_report_down);
            }
            tv_analysis_height50_diff.setText(String.valueOf(Math.abs(analysis_height050_diff)));

            //키 데이터가 1개일 경우
            if(analysisSize == 1){
                tv_analysis_height.setText("-");
                tv_analysis_height_diff.setText("-");
                //그래프그리기
                drawGraph(analysisTask);
                dialog.dismiss();
                super.onPostExecute(aVoid);
                return;
            }

            //기간
            String dateFrom = analysisTask.get(analysisSize-1).getMesure_date();
            String dateTo = analysisTask.get(0).getMesure_date();
            //Log.d("-진우-", "기간1 : " + dateFrom + ", " + dateTo);
            int sYear = Integer.parseInt(dateFrom.substring(0,4));
            int sMonth = Integer.parseInt(dateFrom.substring(5, 7));
            int eYear = Integer.parseInt(dateTo.substring(0, 4));
            int eMonth = Integer.parseInt(dateTo.substring(5,7));
            Log.d("-진우-", "기간2 : " + sYear + "-" + sMonth+ ", " + eYear + "-" + eMonth);
            int month_diff = (eYear - sYear)* 12 + (eMonth - sMonth);
            //double analysis_height50_diff =  Double.parseDouble(String.format("%.1f", analysisTask.get(0).getHeight_50() - analysisTask.get(analysisSize-1).getHeight_50()));
            double analysis_height50_diff =   analysisTask.get(0).getHeight_50() - analysisTask.get(analysisSize-1).getHeight_50();
            double analysis_height_diff =   analysisTask.get(0).getHeight() - analysisTask.get(analysisSize-1).getHeight();
            StringBuilder analysis_height = new StringBuilder();
            /*analysis_height.append(month_diff).append(getString(R.string.usersAnalysisActivity_month)+" ").append(analysis_height50_diff).append("cm "+getString(R.string.usersAnalysisActivity_grow));*/
            analysis_height.append(month_diff).append(getString(R.string.usersAnalysisActivity_month));
            double diff = Double.parseDouble(String.format("%.1f", analysis_height_diff - analysis_height50_diff));
            Log.d("-진우-", "분석 결과2 : " + analysis_height + ", " + analysis_height_diff + ", " + analysis_height50_diff + " = "
                    + (analysis_height_diff - analysis_height50_diff) + ", " + diff);
            tv_analysis_height.setText(analysis_height);
            if(diff > 0){
                iv_analysis_height_diff.setImageResource(R.drawable.icon_report_up);
            } else {
                iv_analysis_height_diff.setImageResource(R.drawable.icon_report_down);
            }
            tv_analysis_height_diff.setText(String.valueOf(Math.abs(diff)));

            //그래프그리기
            drawGraph(analysisTask);

            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
