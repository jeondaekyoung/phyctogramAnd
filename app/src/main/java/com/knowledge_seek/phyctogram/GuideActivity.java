package com.knowledge_seek.phyctogram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
    int MAX_PAGE=3;
    Fragment cur_fragment=new Fragment();
    AlertDialog.Builder dialog;
    boolean dialogFlag=true;
    RadioButton radioButton,radioButton2,radioButton3,radioButton4;
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
}