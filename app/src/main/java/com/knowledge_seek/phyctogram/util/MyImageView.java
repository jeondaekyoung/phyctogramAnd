package com.knowledge_seek.phyctogram.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;

/**
 * Created by shj on 2016-02-22.
 */
public class MyImageView extends ImageView{
    int startX, endX;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int)ev.getX();
                Log.d("-진우-", "MotionEvent.ACTION_DOWN");
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("-진우-","MotionEvent.ACTION_MOVE");
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("-진우-","MotionEvent.ACTION_CANCEL");
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_UP:
                endX = (int)ev.getX();
                Log.d("-진우-","MotionEvent.ACTION_UP");
                if(slideCheck(startX, endX)) {
                    Log.d("-진우-", "ScrollView에슬라이드 실행");
                    ((BaseActivity) getContext()).menuLeftSlideAnimationToggle();
                    return false;
                } else {
                    Log.d("-진우-", "ScrollView에슬라이드 미실행");
                }
                super.onTouchEvent(ev);
                break;
        }

        return true;
    }

    //스와이프 결정
    public boolean slideCheck(int startX, int endX) {
        Log.d("-진우-", "scrollstartX:" + startX + "scrollendX" + endX);
        if (startX <= 50) {
            Log.d("-진우-", "scroll시작지점 OK");
        } else {
            Log.d("-진우-", "scroll시작지점으로 옳지않다");
            return false;
        }
        if ((endX - startX) >= 80) {
            Log.d("-진우-", "scroll이동 OK");
        } else {
            Log.d("-진우-", "scroll이동하기에 옳지않다");
            return false;
        }
        return true;
    }
}
