package com.knowledge_seek.phyctogram.guide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.GuideActivity;
import com.knowledge_seek.phyctogram.Guide_reqActivity;
import com.knowledge_seek.phyctogram.Guide_wifiActivity;
import com.knowledge_seek.phyctogram.R;
import com.knowledge_seek.phyctogram.UsersAddActivity;
import com.knowledge_seek.phyctogram.phyctogram.SaveSharedPreference;

/**
 * Created by dkfka on 2016-11-16..
 */
public class page_4 extends android.support.v4.app.Fragment {

    RelativeLayout relativeLayout;
    ImageView guide_close;
    Button guide_btn4;
    ImageView guide_img,page4_chk;
    TextView guide_Title_tv;
    ListView guide_lv;
    Button btn_searchWifi;


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


        page4_chk = (ImageView) relativeLayout.findViewById(R.id.page4_chk);
        page4_chk.setVisibility(View.VISIBLE);

        guide_Title_tv =(TextView) relativeLayout.findViewById(R.id.guide_Title_tv);
        relativeLayout.removeView(guide_Title_tv);

        // 아래와 같이 해줘야 기기에 맞는 DP가 나온다.
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(240 * dm.density);
        RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(
                size, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //dp값 변경
        size = Math.round(50 * dm.density);
        lay.topMargin = size;

        lay.addRule(RelativeLayout.CENTER_VERTICAL);
        lay.addRule(RelativeLayout.CENTER_HORIZONTAL);

        TextView new_tv=new TextView(getActivity());
        new_tv.setTextColor(Color.WHITE);
        new_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        new_tv.setText(R.string.includeGuide_page4tvTitle);
        new_tv.setId(R.id.guide_Title_tv);
        relativeLayout.addView(new_tv,lay);

        guide_img = (ImageView) relativeLayout.findViewById(R.id.guide_img);
        guide_img.setVisibility(View.GONE);

        guide_lv = (ListView) relativeLayout.findViewById(R.id.guide_lv);
        guide_lv.setVisibility(View.GONE);


        guide_btn4 = (Button) relativeLayout.findViewById(R.id.guide_btn4);
        guide_btn4.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_radius_white));


        //동적으로 버튼 변경 android:layout_below="@+id/guide_lv"
        size = Math.round(240 * dm.density);
        int h_size=Math.round(60 * dm.density);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(size,h_size);
        param.addRule(RelativeLayout.BELOW, R.id.guide_Title_tv);

        size = Math.round(110 * dm.density);
        param.topMargin = size;
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);

        btn_searchWifi = (Button) relativeLayout.findViewById(R.id.btn_searchWifi);
        btn_searchWifi.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_radius_white));
        btn_searchWifi.setText("시작하기");
        btn_searchWifi.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        btn_searchWifi.setTextColor(Color.GRAY);
        btn_searchWifi.setLayoutParams(param);
        btn_searchWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity();
                SaveSharedPreference.setGuideFlag(getContext(),false);
                Intent intent=new Intent(getContext(),UsersAddActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return relativeLayout;
    }

}