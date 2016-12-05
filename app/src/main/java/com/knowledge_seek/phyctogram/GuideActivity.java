package com.knowledge_seek.phyctogram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.RadioButton;

import com.knowledge_seek.phyctogram.guide.page_1;
import com.knowledge_seek.phyctogram.guide.page_2;
import com.knowledge_seek.phyctogram.guide.page_3;
import com.knowledge_seek.phyctogram.guide.page_4;
import com.knowledge_seek.phyctogram.kakao.common.BaseActivity;

/**
 * Created by dkfka on 2016-11-16.
 */
public class GuideActivity extends FragmentActivity {

    final String TAG = EquipmentActivity.class.getName();
    int MAX_PAGE=4;
    Fragment cur_fragment=new Fragment();
    AlertDialog.Builder dialog;
    boolean dialogFlag=true;
    RadioButton radioButton,radioButton2,radioButton3,radioButton4;

    public static final int REQUEST_ACT = 112;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog=new AlertDialog.Builder(this).setMessage("기기가 삐뚤어 지진 않았나요?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialogFlag=false;
            }
        });
        radioButton = (RadioButton)findViewById(R.id.radioButton);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton)findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton)findViewById(R.id.radioButton4);

        setContentView(R.layout.activity_guide);
        ViewPager viewPager=(ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new adapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                              @Override
                                              public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                              }
                                              @Override
                                              public void onPageSelected(int position) {

                                              }
                                              @Override
                                              public void onPageScrollStateChanged(int state) {

                                                  if(state==1&&dialogFlag){
                                                      dialog.show();
                                                  }

                                              }
                                          }
        );
    }
    private class adapter extends FragmentStatePagerAdapter {
        public adapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if(position<0 || MAX_PAGE<=position)
                return null;
            switch (position){
                case 0:
                    cur_fragment=new page_1();

                    break;
                case 1:
                    cur_fragment=new page_2();
                    break;
                case 2:
                    cur_fragment=new page_3();
                    break;
                case 3:
                    cur_fragment=new page_4();
                    break;
            }
            return cur_fragment;
        }
        @Override
        public int getCount() {
            return MAX_PAGE;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseActivity.setStatusBarColor(this,R.color.purpledk);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: 실행");

        if (resultCode != EquipmentActivity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: 결과가 성공이 아님.");
            return;
        }

        if (requestCode == GuideActivity.REQUEST_ACT) {
            //popupActiviy에서 가져온 ap 정보
            String p_ssid = data.getStringExtra("p_ssid");
            String p_password = data.getStringExtra("p_password");
            String p_capabilities = data.getStringExtra("p_capabilities");
            Log.d(TAG, "onActivityResult: 결과:" +p_ssid+","+p_password+","+p_capabilities);
            //new Equipment_TCP_Client_Task(this,wm).execute("s "+p_ssid+" "+p_password);

        } else {
            Log.d(TAG, "onActivityResult: REQUEST_ACT가 아님.");
        }
    }
}