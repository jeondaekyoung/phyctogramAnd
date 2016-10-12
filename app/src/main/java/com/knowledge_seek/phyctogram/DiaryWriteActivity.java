package com.knowledge_seek.phyctogram;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.knowledge_seek.phyctogram.domain.Diary;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.DiaryAPI;
import com.knowledge_seek.phyctogram.retrofitapi.FileUploadService;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import com.knowledge_seek.phyctogram.util.Utility;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by dkfka on 2015-12-08.
 */
public class DiaryWriteActivity extends BaseActivity {

    //레이아웃정의 - 슬라이드메뉴
    private ImageButton btn_left;
    private LinearLayout ic_screen;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃 정의
    private TextView tv_users_name;     //아이 이름 출력

    private TextView tv_diary_date;     //날짜
    private Button btn_diary_save;      //일기 저장
    private EditText et_title;
    private EditText et_contents;
    private Button btn_pic;                 //갤러리

    //데이터 정의
    int year, month, day;
    Diary diary;
    private static final int SELECT_IMAGE = 1;
    private File imageFile = null;                          //업로드파일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("-진우-", "DiaryWriteActivity.onCreate() 실행");

        //화면 페이지
        ic_screen = (LinearLayout)findViewById(com.knowledge_seek.phyctogram.R.id.ic_screen);
        LayoutInflater.from(this).inflate(com.knowledge_seek.phyctogram.R.layout.include_diary_write, ic_screen, true);

        //슬라이드 내 이미지, 셋팅
        img_profile = (CircularImageView)findViewById(com.knowledge_seek.phyctogram.R.id.img_profile);
        if (memberImg != null) {
            img_profile.setImageBitmap(memberImg);
        }

        //슬라이드 내 이름, 셋팅
        tv_member_name = (TextView)findViewById(com.knowledge_seek.phyctogram.R.id.tv_member_name);
        if (memberName != null) {
            tv_member_name.setText(memberName);
        }

        //메인페이지 내 아이 이름 출력
        tv_users_name = (TextView) findViewById(com.knowledge_seek.phyctogram.R.id.tv_users_name);
        if (nowUsers != null) {
            tv_users_name.setText(nowUsers.getName());
        }

        //슬라이드 내 아이 목록(ListView)에서 아이 선택시
        lv_usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*nowUsers = (Users) usersListSlideAdapter.getItem(position);
                Log.d("-진우-", "선택한 아이 : " + nowUsers.toString());
                Toast.makeText(getApplicationContext(), "'" + nowUsers.getName() + "' 아이를 선택하였습니다", Toast.LENGTH_LONG).show();

                if (tv_users_name != null) {
                    tv_users_name.setText(nowUsers.getName());
                }*/
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

        tv_diary_date = (TextView)findViewById(com.knowledge_seek.phyctogram.R.id.tv_diary_date);

        //초기디폴트 날짜 셋팅
        datepickSetting();

        //달력 대화상자 띄우기
        tv_diary_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diaryDate = tv_diary_date.getText().toString();
                new DatePickerDialog(DiaryWriteActivity.this, dateSetListener, Integer.valueOf(diaryDate.substring(0,4)),
                        Integer.valueOf(diaryDate.substring(5,7))-1, Integer.valueOf(diaryDate.substring(8))).show();
                setTheme(com.knowledge_seek.phyctogram.R.style.AppTheme);
            }
        });

        et_title = (EditText)findViewById(com.knowledge_seek.phyctogram.R.id.et_title);
        et_contents = (EditText)findViewById(com.knowledge_seek.phyctogram.R.id.et_contents);
        //일기 글 저장
        btn_diary_save = (Button)findViewById(com.knowledge_seek.phyctogram.R.id.btn_diary_save);
        btn_diary_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("-진우-", "저장하기");

                if(nowUsers.getUser_seq() == 0){
                    Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.diaryWriteActivity_registerChild, Toast.LENGTH_SHORT).show();
                    return;
                }

                diary = new Diary();
                diary.setTitle(et_title.getText().toString());
                diary.setContents(et_contents.getText().toString());
                diary.setUser_seq(nowUsers.getUser_seq());


                //Log.d("-진우-", "날짜 : " + et_diary_date.getText().toString().length());
                if(tv_diary_date.getText().toString().length() <= 0){
                    Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.diaryWriteActivity_registerDay, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    //Log.d("-진우-", "날짜 : " + et_diary_date.getText().toString());
                    String[] date = tv_diary_date.getText().toString().split("-");
                    //Log.d("-진우-", String.valueOf(date.length));
                    diary.setWritng_year(date[0]);
                    diary.setWritng_mt(date[1]);
                    diary.setWritng_de(date[2]);
                }

                if(!checkDiary(diary)){
                    return;
                }

                Log.d("-진우-", "저장 : " + Utility.diary2json(diary));
                RegisterDiaryTask task = new RegisterDiaryTask(diary, imageFile);
                task.execute();
                //onBackPressed();
            }
        });

        btn_pic = (Button)findViewById(com.knowledge_seek.phyctogram.R.id.btn_pic);
        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //롤리팝 버전 이상이라면 권한 체크 요청함
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
                    }catch (Exception e){
                        Log.d("-진우-","ParingActivity requestPermissions Exception : "+ e.getMessage());
                    }
                }else{
                    //파일업로드 테스트
                    //Intent intent = new Intent(getApplicationContext(), FileUploadActivity.class);
                    //startActivity(intent);

                    try {
                        //사진 가져오기
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECT_IMAGE);
                    }catch (Exception e){
                        Log.d("-진우-","ParingActivity requestPermissions Exception : "+ e.getMessage());
                    }
                }
            }
        });
    }

    //권한 체크 결과 받음
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 11) {
            //허용 0, 비허용 -1
            if (grantResults[0] == 0){
                //사진 가져오기
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE);
            }else{
                Log.d("-진우-", "fail");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "DiaryWriteActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        int height = getListViewHeight(lv_usersList);
        lv_usersList.getLayoutParams().height = height;
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이 목록, 계정이름, 계정이미지)
        //DiaryWriteTask task = new DiaryWriteTask();
        //task.execute(img_profile);

        Log.d("-진우-", "DiaryWriteActivity.onResume() : " + member.toString());

        Log.d("-진우-", "DiaryWriteActivity.onResume() 끝");
    }



    //갤러리 선택 결과 받는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImageUri = data.getData();
            String imagePath = getPath(selectedImageUri);

            //선택한 갤러리를 작게 보이도록 할 수 있다
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap src = BitmapFactory.decodeFile(imagepath, options);
            Bitmap resized = Bitmap.createScaledBitmap(src, 300, 300, true);
            imageview.setImageBitmap(resized);*/

            imageFile = new File(imagePath);
        }
    }

    //선택한 갤러리 파일 경로찾기
    public String getPath(Uri uri){
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //Log.d("-진우-", "column_index : " + column_index);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        //Log.d("-진우-", "갤러리 : " + column_index + ", path : " + imgPath + ", imgName : " + imgName);
        Log.d("-진우-", "선택한 갤러리 경로 : " + cursor.getString(column_index));
        return cursor.getString(column_index);
    }


    //초기디폴트 날짜 셋팅
    private void datepickSetting() {
        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String msg = String.valueOf(year).concat("-").concat(Utility.dateFormat(month + 1)).concat("-").concat(Utility.dateFormat(day));
        tv_diary_date.setText(msg);
    }

    //날짜 입력
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String msg = String.valueOf(year).concat("-").concat(Utility.dateFormat(monthOfYear + 1)).concat("-").concat(Utility.dateFormat(dayOfMonth));
            tv_diary_date.setText(msg);
        }
    };


    //Diary의 내용체크
    private boolean checkDiary(Diary diary){
        if(diary.getTitle().length() <= 0 || diary.getContents().length() <= 0){
            Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.communityWriteActivity_writeTitleContents, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //일기 저장
    private class RegisterDiaryTask extends AsyncTask<Void, Void, String>{
        private ProgressDialog dialog = new ProgressDialog(DiaryWriteActivity.this);
        private Diary diaryTask;
        private File imageFileTask;

        //생성자
        public RegisterDiaryTask(Diary diary, File imageFile) {
            this.diaryTask = diary;
            this.imageFileTask = imageFile;
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
        protected String doInBackground(Void... params) {
            String result = null;

            DiaryAPI service = ServiceGenerator.createService(DiaryAPI.class);
            Call<String> call = service.registerDiary(diaryTask);
            try {
                result = call.execute().body();
                Log.d("-진우-", "일기 저장 결과 : " + result);
            } catch (IOException e){
                Log.d("-진우-", "글 저장 실패");
            }

            //이미지파일이 있을 경우 업로드한다
            if(imageFileTask != null && !result.equals("exist") && !result.equals("fail")) {
                Log.d("-진우-", "파일 업로드 시작 :  " + imageFileTask.getName() + ", 일기 시퀀스 : " + result);
                FileUploadService service1 = ServiceGenerator.createService(FileUploadService.class);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFileTask);

                Call<String> call1 = service1.fileUploadSingle(Integer.valueOf(result), requestBody);
                try {
                    //result = call.execute().body();
                    Response res = call1.execute();
                    Log.d("-진우-", "코드 : " + res.code() + ", " + res.body());
                } catch (IOException e) {
                    Log.d("-진우-", "글 저장 실패");
                }
            }
            return result;
        }

        //Background 작업이 끝난 후 UI 작업을 진행 한다.
        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                if(result.equals("exist")){
                    Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.diaryWriteActivity_alreadyDiary, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), com.knowledge_seek.phyctogram.R.string.commonActivity_save, Toast.LENGTH_LONG).show();
                    //Log.d("-진우-", "일기 시퀀스 : " + result);
                    onBackPressed();
                }
            } else {
                Log.d("-진우-", "일기 저장에 실패하였습니다");
            }

            dialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
