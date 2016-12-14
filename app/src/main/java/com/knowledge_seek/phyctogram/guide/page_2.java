package com.knowledge_seek.phyctogram.guide;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.R;

import java.util.ArrayList;

/**
 * Created by dkfka on 2016-11-16.
 */
public class page_2 extends android.support.v4.app.Fragment {

    final String TAG = page_2.class.getName();

    RelativeLayout relativeLayout;
    ImageView guide_close;
    Button guide_btn2;
    ImageView guide_img;
    TextView page_num;
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

        relativeLayout =(RelativeLayout)inflater.inflate(R.layout.include_guide,container,false);

        guide_close =(ImageView) relativeLayout.findViewById(R.id.guide_close);

        page_num=(TextView) relativeLayout.findViewById(R.id.page_num);
        page_num.setText("2.\nWi-Fi에 연결하세요");

        guide_img = (ImageView) relativeLayout.findViewById(R.id.guide_img);
        guide_img.setVisibility(View.GONE);

        guide_btn2 = (Button) relativeLayout.findViewById(R.id.guide_btn2);
        guide_btn2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_radius_white));

         guide_lv = (ListView) relativeLayout.findViewById(R.id.guide_lv);

        btn_searchWifi = (Button) relativeLayout.findViewById(R.id.btn_searchWifi);
        return relativeLayout;
    }

}