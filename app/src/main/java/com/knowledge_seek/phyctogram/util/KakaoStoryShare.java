package com.knowledge_seek.phyctogram.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.network.ErrorResult;
import com.kakao.util.KakaoParameterException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.knowledge_seek.phyctogram.R;

/**
 * Created by sjw on 2016-01-27.
 */
public class KakaoStoryShare {

    private final String photoContent = "픽토그램 내 아이 성장";
    private final String execParam = "place=1111";
    private final String marketParam = "referrer=kakaostory";

    private Activity mActivity;
    private Bitmap image;
    private List<File> fileList = new ArrayList<File>();

    public KakaoStoryShare(Activity activity) {
        this.mActivity = activity;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void onClickPostPhoto() {

        final File uploadFile = Utility.saveBitmaptoJpeg(image);
        fileList.add(uploadFile);

        try {
            KakaoStoryService.requestPostPhoto(new KakaoStoryResponseCallback<MyStoryInfo>() {
                @Override
                public void onSuccess(MyStoryInfo result) {
                    Log.d("-진우-", "성공 : " + result.toString());
                    //handleStoryPostResult(KakaoStoryService.StoryType.PHOTO, result);
                    Toast.makeText(mActivity, R.string.kakaoStoryShare_successUpload, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDidEnd() {
                    if(uploadFile != null) {
                        uploadFile.delete();
                    }
                }
            }, fileList, photoContent, PostRequest.StoryPermission.PUBLIC, true, execParam, execParam, marketParam, marketParam);
        } catch (KakaoParameterException e) {
            Log.d("-진우-", "카카오스토리 사진올리기 실패");
        }
    }

    //API 요청은 KakaoStoryService를 이용하여 요청합니다.
    //파라미터로 API 요청 결과에 따른 콜백 StoryResponseCallback을 넘깁니다. StoryResponseCallback 구현합니다.
    private abstract class KakaoStoryResponseCallback<T> extends StoryResponseCallback<T> {

        @Override
        public void onNotKakaoStoryUser() {
            Log.d("카스", "카카오스토리 유저가 아닙니다.");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Log.d("-진우-", "카카오스토리 실패" + errorResult);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            Log.d("-진우-", "카카오스토리 에러");
            //redirectLoginActivity();
        }

        @Override
        public void onNotSignedUp() {
            Log.d("-진우-", "카카오스토리 로그인필요");
            //redirectSignupActivity();
        }
    }
}
