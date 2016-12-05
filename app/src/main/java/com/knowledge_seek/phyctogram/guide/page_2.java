package com.knowledge_seek.phyctogram.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.R;

/**
 * Created by dkfka on 2016-11-16.
 */
public class page_2 extends android.support.v4.app.Fragment {
    RelativeLayout RelativeLayout;
    RadioButton radioButton,radioButton2,radioButton3,radioButton4;
    ImageView guide_iV;
    TextView page_num;
    ListView lv_wifiList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout=(RelativeLayout)inflater.inflate(R.layout.include_guide,container,false);
        page_num=(TextView)RelativeLayout.findViewById(R.id.page_num);
        guide_iV= (ImageView)RelativeLayout.findViewById(R.id.guide_iV);
        radioButton = (RadioButton)RelativeLayout.findViewById(R.id.radioButton);
        radioButton2 = (RadioButton)RelativeLayout.findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton)RelativeLayout.findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton)RelativeLayout.findViewById(R.id.radioButton4);

        page_num.setText("2.Wi-Fi에 연결하세요");
        guide_iV.setVisibility(View.GONE);

        lv_wifiList=new ListView(getActivity());
        //lv_wifiList.setAdapter();

        lv_wifiList.setLayoutParams(RelativeLayout.getLayoutParams());

        radioButton.setChecked(false);
        radioButton2.setChecked(true);
        radioButton3.setChecked(false);
        radioButton4.setChecked(false);

        return RelativeLayout;
    }
}