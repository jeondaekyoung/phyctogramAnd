package com.knowledge_seek.phyctogram.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.GuideActivity;
import com.knowledge_seek.phyctogram.R;

/**
 * Created by dkfka on 2016-11-16.
 */
public class page_4 extends android.support.v4.app.Fragment {

    RelativeLayout RelativeLayout;
    RadioButton radioButton4;
    ImageView guide_img;
    TextView page_num;
    RadioGroup rg_group;
    ListView guide_lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout=(RelativeLayout)inflater.inflate(R.layout.include_guide,container,false);
        page_num=(TextView)RelativeLayout.findViewById(R.id.page_num);
        page_num.setText("아이를 등록하면 모든\n준비과정이 완료됩니다!");

        guide_img = (ImageView)RelativeLayout.findViewById(R.id.guide_img);

        guide_lv = (ListView)RelativeLayout.findViewById(R.id.guide_lv);
        guide_lv.setVisibility(View.GONE);

        radioButton4 = (RadioButton)RelativeLayout.findViewById(R.id.radioButton4);
        radioButton4.setChecked(true);

        rg_group = (RadioGroup) RelativeLayout.findViewById(R.id.rg_group);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.radioButton:
                        GuideActivity.viewPager.setCurrentItem(0);
                        radioButton4.setChecked(true);
                        break;
                    case R.id.radioButton2:
                        GuideActivity.viewPager.setCurrentItem(1);
                        radioButton4.setChecked(true);
                        break;
                    case R.id.radioButton3:
                        GuideActivity.viewPager.setCurrentItem(2);
                        radioButton4.setChecked(true);
                        break;
                }
            }
        });

        return RelativeLayout;
    }

}