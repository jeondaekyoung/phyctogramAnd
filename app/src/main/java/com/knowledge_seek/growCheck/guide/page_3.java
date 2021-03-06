package com.knowledge_seek.growCheck.guide;

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

import com.knowledge_seek.growCheck.GuideActivity;
import com.knowledge_seek.growCheck.Guide_reqActivity;
import com.knowledge_seek.growCheck.Guide_wifiActivity;
import com.knowledge_seek.growCheck.R;

/**
 * Created by dkfka on 2016-11-16..
 */
public class page_3 extends android.support.v4.app.Fragment {

    RelativeLayout relativeLayout;
    ImageView guide_close;
    Button guide_btn3;
    ImageView guide_img;
    TextView guide_Title_tv,guide_subTitle_tv;
    ListView guide_lv;
    Button btn_searchWifi;
    public static Button btn_measure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        relativeLayout =(RelativeLayout)inflater.inflate(R.layout.include_guide,container,false);

        guide_close =(ImageView) relativeLayout.findViewById(R.id.guide_close);

        guide_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className =getActivity().getClass().getSimpleName();
                switch (className){
                    case "GuideActivity":
                        GuideActivity.dialog_close.show();
                        break;
                    case "Guide_reqActivity":
                        Guide_reqActivity.dialog_close.show();
                        break;
                    case "Guide_wifiActivity":
                        Guide_wifiActivity.dialog_close.show();
                        break;

                }

            }
        });

        guide_Title_tv =(TextView) relativeLayout.findViewById(R.id.guide_Title_tv);
        guide_Title_tv.setText(R.string.includeGuide_page3tvTitle);

        guide_subTitle_tv = (TextView) relativeLayout.findViewById(R.id.guide_subTitle_tv);
        guide_subTitle_tv.setText(R.string.includeGuide_page3tvsubTitle);
        guide_subTitle_tv.setVisibility(View.VISIBLE);

        guide_img = (ImageView) relativeLayout.findViewById(R.id.guide_img);
        guide_img.setImageResource(R.drawable.guideimg2);

        guide_lv = (ListView) relativeLayout.findViewById(R.id.guide_lv);
        guide_lv.setVisibility(View.GONE);

        guide_btn3= (Button) relativeLayout.findViewById(R.id.guide_btn3);
        guide_btn3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_radius_white));

        btn_searchWifi = (Button) relativeLayout.findViewById(R.id.btn_searchWifi);
        btn_searchWifi.setVisibility(View.GONE);

        btn_measure = (Button) relativeLayout.findViewById(R.id.btn_measure);
        btn_measure.setVisibility(View.VISIBLE);

        return relativeLayout;
    }
}