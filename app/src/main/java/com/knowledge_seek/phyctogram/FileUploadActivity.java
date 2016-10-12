package com.knowledge_seek.phyctogram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;

import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;
import com.knowledge_seek.phyctogram.retrofitapi.FileUploadService;
import com.knowledge_seek.phyctogram.retrofitapi.ServiceGenerator;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dkfka on 2015-12-08.
 * 파일업로드 test activity
 */
public class FileUploadActivity extends BaseActivity implements View.OnClickListener {

    //레이아웃정의 - 슬라이드 메뉴
    private LinearLayout ic_screen;
    private ImageButton btn_left;
    private CircularImageView img_profile;      //슬라이드 내 이미지
    private TextView tv_member_name;            //슬라이드 내 이름

    //레이아웃정의
    private TextView messageText;
    private Button uploadButton, btnselectpic;
    private ImageView imageview;

    //데이터
    private ProgressDialog dialog = null;

    private String imagepath=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //화면 페이지
        ic_screen = (LinearLayout) findViewById(com.knowledge_seek.phyctogram.R.id.ic_screen);
        LayoutInflater.from(this).inflate(com.knowledge_seek.phyctogram.R.layout.include_fileupload, ic_screen, true);

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

        uploadButton = (Button)findViewById(com.knowledge_seek.phyctogram.R.id.uploadButton);
        messageText  = (TextView)findViewById(com.knowledge_seek.phyctogram.R.id.messageText);
        btnselectpic = (Button)findViewById(com.knowledge_seek.phyctogram.R.id.button_selectpic);
        imageview = (ImageView)findViewById(com.knowledge_seek.phyctogram.R.id.imageView_pic);

        btnselectpic.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("-진우-", "CharacterActivity.onResume() 실행");

        //슬라이드메뉴 셋팅
        initSildeMenu();

        //슬라이드메뉴 내 아이 목록 셋팅
        usersListSlideAdapter.setUsersList(usersList);
        usersListSlideAdapter.setSelectUsers(nowUsers.getUser_seq());
        int height = getListViewHeight(lv_usersList);
        lv_usersList.getLayoutParams().height = height;
        usersListSlideAdapter.notifyDataSetChanged();

        //슬라이드메뉴 셋팅(내 아이 목록, 계정이미지, 개월수, 캐릭터이미지)
        /*CharacterTask task = new CharacterTask();
        task.execute(img_profile);*/

        //아이의 개월수와 캐릭터 이미지 가져오기
        //FindMonthNumAnimalTask task = new FindMonthNumAnimalTask();
        //task.execute();

        Log.d("-진우-", "CharacterActivity 에 onResume() : " + member.toString());

        Log.d("-진우-", "CharacterActivity.onResume() 끝");
    }



    //Uri에서 파일명을 추출하는 로직
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        Log.d("-진우-", "갤러리 : " + column_index + ", path : " + imgPath + ", imgName : " + imgName);

        return imgName;
    }

    //파일 업로드
    private void uploadFile(String sourceFileUri){

        Log.d("-진우-", "파일업로드 시작 : " + sourceFileUri);

        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        File file = new File(sourceFileUri);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        Call<String> call = service.upload1(requestBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                Log.d("-진우-", "파일 업로드 성공 : " + "코드 " + response.code() + ", 스트링 " + response.toString() + ", 바디 " + response.body() + ", 메시지 " + response.message());
                dialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("-진우-", "파일 업로드 실패 : " + t.getMessage());
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View arg0) {
        if(arg0==btnselectpic)
        {
            /*Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);*/
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
        else if (arg0==uploadButton) {

            dialog = ProgressDialog.show(FileUploadActivity.this, "", getString(com.knowledge_seek.phyctogram.R.string.fileUploadActivity_upLoadingAlert), true);
            messageText.setText(com.knowledge_seek.phyctogram.R.string.fileUploadActivity_startUpLoading);
            new Thread(new Runnable() {
                public void run() {

                    uploadFile(imagepath);

                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("-진우-", "onActivityResutl : " + requestCode + ", " + resultCode);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap src = BitmapFactory.decodeFile(imagepath, options);
            Bitmap resized = Bitmap.createScaledBitmap(src, 300, 300, true);
            imageview.setImageBitmap(resized);

            //Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            //imageview.setImageBitmap(bitmap);
            messageText.setText(getString(com.knowledge_seek.phyctogram.R.string.fileUploadActivity_fileInfo)+ " " + imagepath);
        }

    }

    public String getPath(Uri uri){
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        Log.d("-진우-", "column_index : " + column_index);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        if(imgPath == null){
            Log.d("-진우-", "선택한 이미지가 없어?");
        }
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        Log.d("-진우-", "갤러리 : " + column_index + ", path : " + imgPath + ", imgName : " + imgName);
        Log.d("-진우-", "getPath() : " + cursor.getString(column_index));
        return cursor.getString(column_index);
    }
}
