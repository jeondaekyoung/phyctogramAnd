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

import java.util.ArrayList;

/**
 * Created by dkfka on 2016-11-16.
 */
public class page_2 extends android.support.v4.app.Fragment {

    final String TAG = page_2.class.getName();

    RelativeLayout RelativeLayout;
    RadioButton radioButton2;
    ImageView guide_img;
    TextView page_num;
    RadioGroup rg_group;
    public static  ListView guide_lv;
    public static Button btn_searchWifi;


    private ArrayList<String> E_response;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout=(RelativeLayout)inflater.inflate(R.layout.include_guide,container,false);
        page_num=(TextView)RelativeLayout.findViewById(R.id.page_num);
        page_num.setText("2.\nWi-Fi에 연결하세요");

        guide_img = (ImageView)RelativeLayout.findViewById(R.id.guide_img);
        guide_img.setVisibility(View.GONE);

        radioButton2 = (RadioButton)RelativeLayout.findViewById(R.id.radioButton2);
        radioButton2.setChecked(true);

        guide_lv = (ListView)RelativeLayout.findViewById(R.id.guide_lv);


        rg_group = (RadioGroup) RelativeLayout.findViewById(R.id.rg_group);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.radioButton:
                        GuideActivity.viewPager.setCurrentItem(0);
                        radioButton2.setChecked(true);
                        break;
                    case R.id.radioButton3:
                        GuideActivity.viewPager.setCurrentItem(2);
                        radioButton2.setChecked(true);
                        break;
                    case R.id.radioButton4:
                        GuideActivity.viewPager.setCurrentItem(3);
                        radioButton2.setChecked(true);
                        break;
                }
            }
        });

       // lv_wifiList=new ListView(getActivity());
        //lv_wifiList.setAdapter(wifiListAdapter);

        //lv_wifiList.setLayoutParams(RelativeLayout.getLayoutParams());
        btn_searchWifi = (Button)RelativeLayout.findViewById(R.id.btn_searchWifi);
        return RelativeLayout;
    }

}