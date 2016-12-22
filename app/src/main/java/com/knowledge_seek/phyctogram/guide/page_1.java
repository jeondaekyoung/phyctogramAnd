package com.knowledge_seek.phyctogram.guide;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowledge_seek.phyctogram.GuideActivity;
import com.knowledge_seek.phyctogram.Guide_reqActivity;
import com.knowledge_seek.phyctogram.Guide_wifiActivity;
import com.knowledge_seek.phyctogram.R;

/**
 * Created by jdk on 2016-11-16..
 */
public class page_1 extends android.support.v4.app.Fragment {

    RelativeLayout relativeLayout;
    ImageView guide_close;
    Button guide_btn1;
    ImageView guide_img;
    TextView guide_Title_tv,guide_subTitle_tv;
    ListView guide_lv;
    Button btn_searchWifi;
    AlertDialog dialog_skip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final View dialogView_skip= getActivity().getLayoutInflater().inflate(R.layout.guide_skip, null);
        dialog_skip = new AlertDialog.Builder(getActivity()).setView(dialogView_skip).show();
        // AlertDialog 에서 위치 크기 수정
        dialog_skip.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_skip.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog_skip.getWindow().getAttributes();
        params.gravity= Gravity.TOP | Gravity.RIGHT;
        dialog_skip.getWindow().setAttributes(params);

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        relativeLayout =(RelativeLayout)inflater.inflate(R.layout.include_guide,container,false);

        //guide_close.getLocationOnScreen(location);
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
        guide_Title_tv.setText(R.string.includeGuide_page1tvTitle);

        guide_subTitle_tv  =(TextView) relativeLayout.findViewById(R.id.guide_subTitle_tv);
        guide_subTitle_tv.setVisibility(View.VISIBLE);
        guide_subTitle_tv.setText(R.string.includeGuide_page1tvsubTitle);

        guide_img = (ImageView) relativeLayout.findViewById(R.id.guide_img);

        guide_lv = (ListView) relativeLayout.findViewById(R.id.guide_lv);
        guide_lv.setVisibility(View.GONE);

        guide_btn1 = (Button) relativeLayout.findViewById(R.id.guide_btn1);
        guide_btn1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_radius_white));
        btn_searchWifi = (Button) relativeLayout.findViewById(R.id.btn_searchWifi);
        btn_searchWifi.setVisibility(View.GONE);


        return relativeLayout;

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}