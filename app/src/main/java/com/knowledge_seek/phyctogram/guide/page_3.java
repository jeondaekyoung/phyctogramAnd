package com.knowledge_seek.phyctogram.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class page_3 extends android.support.v4.app.Fragment {

    RelativeLayout RelativeLayout;
    RadioButton radioButton3;
    ImageView guide_img;
    TextView page_num;
    RadioGroup rg_group;
    ListView guide_lv;
    Button btn_searchWifi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout=(RelativeLayout)inflater.inflate(R.layout.include_guide,container,false);
        page_num=(TextView)RelativeLayout.findViewById(R.id.page_num);
        page_num.setText("3.\n바닥까지의 높이를\n측정 합니다");

        guide_img = (ImageView)RelativeLayout.findViewById(R.id.guide_img);

        guide_lv = (ListView)RelativeLayout.findViewById(R.id.guide_lv);
        guide_lv.setVisibility(View.GONE);

        radioButton3 = (RadioButton)RelativeLayout.findViewById(R.id.radioButton3);
        radioButton3.setChecked(true);

        rg_group = (RadioGroup) RelativeLayout.findViewById(R.id.rg_group);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.radioButton:
                        GuideActivity.viewPager.setCurrentItem(0);
                        radioButton3.setChecked(true);
                        break;
                    case R.id.radioButton2:
                        GuideActivity.viewPager.setCurrentItem(1);
                        radioButton3.setChecked(true);
                        break;
                    case R.id.radioButton4:
                        GuideActivity.viewPager.setCurrentItem(3);
                        radioButton3.setChecked(true);
                        break;
                }
            }
        });

        btn_searchWifi = (Button)RelativeLayout.findViewById(R.id.btn_searchWifi);
        btn_searchWifi.setVisibility(View.GONE);

        return RelativeLayout;
    }
}