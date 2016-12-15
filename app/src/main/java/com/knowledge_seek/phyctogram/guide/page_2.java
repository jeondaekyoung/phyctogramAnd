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

import com.knowledge_seek.phyctogram.GuideActivity;
import com.knowledge_seek.phyctogram.R;

/**
 * Created by dkfka on 2016-11-16.
 */
public class page_2 extends android.support.v4.app.Fragment {

    final String TAG = page_2.class.getName();

    RelativeLayout relativeLayout;
    ImageView guide_close;
    Button guide_btn2;
    ImageView guide_img;
    TextView guide_Title_tv;
    public static  ListView guide_lv;
    public static TextView guide_lvEmpty;
    public static Button btn_searchWifi;



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

        guide_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideActivity.dialog_close.show();
            }
        });


        guide_Title_tv =(TextView) relativeLayout.findViewById(R.id.guide_Title_tv);
        guide_Title_tv.setText(R.string.includeGuide_page2tvTitle);

        guide_img = (ImageView) relativeLayout.findViewById(R.id.guide_img);
        guide_img.setVisibility(View.GONE);

        guide_btn2 = (Button) relativeLayout.findViewById(R.id.guide_btn2);
        guide_btn2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_radius_white));

         guide_lv = (ListView) relativeLayout.findViewById(R.id.guide_lv);

        guide_lvEmpty = (TextView)relativeLayout.findViewById(R.id.guide_lvEmpty);

        btn_searchWifi = (Button) relativeLayout.findViewById(R.id.btn_searchWifi);
        return relativeLayout;
    }

}