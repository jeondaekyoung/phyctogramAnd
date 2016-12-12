package com.knowledge_seek.phyctogram.guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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
import com.knowledge_seek.phyctogram.UsersAddActivity;

/**
 * Created by dkfka on 2016-11-16.
 */
public class page_4 extends android.support.v4.app.Fragment {

    RelativeLayout relativeLayout;
    RadioButton radioButton4;
    ImageView guide_img,page4_chk;
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

        relativeLayout =(RelativeLayout)inflater.inflate(R.layout.include_guide,container,false);

        page4_chk = (ImageView) relativeLayout.findViewById(R.id.page4_chk);
        page4_chk.setVisibility(View.VISIBLE);

        page_num=(TextView) relativeLayout.findViewById(R.id.page_num);
        relativeLayout.removeView(page_num);
        //코드로 textView 생성

        /*<TextView
        android:id="@+id/page_num"
        android:layout_width="240dp"
        android:layout_height="wrap_content"
        android:text="text"
        android:textColor="#FFFFFF"
        android:textSize="24dp"
        android:layout_centerVertical="true"
        android:layout_centerHorizontal="true" />
*/
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
        new_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        new_tv.setText(" 아이를 등록하면 모든\n준비과정이 완료됩니다!");
        new_tv.setId(R.id.page_num);
        relativeLayout.addView(new_tv,lay);

        guide_img = (ImageView) relativeLayout.findViewById(R.id.guide_img);
        guide_img.setVisibility(View.GONE);

        guide_lv = (ListView) relativeLayout.findViewById(R.id.guide_lv);
        guide_lv.setVisibility(View.GONE);

        radioButton4 = (RadioButton) relativeLayout.findViewById(R.id.radioButton4);
        radioButton4.setChecked(true);

        rg_group = (RadioGroup) relativeLayout.findViewById(R.id.rg_group);
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
        //동적으로 버튼 변경 android:layout_below="@+id/guide_lv"
        size = Math.round(240 * dm.density);
        int h_size=Math.round(60 * dm.density);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(size,h_size);
        param.addRule(RelativeLayout.BELOW, R.id.page_num);

        size = Math.round(110 * dm.density);
        param.topMargin = size;
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);

        btn_searchWifi = (Button) relativeLayout.findViewById(R.id.btn_searchWifi);
        btn_searchWifi.setBackground(getResources().getDrawable(R.drawable.border_radius_white));
        btn_searchWifi.setText("시작하기");
        btn_searchWifi.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        btn_searchWifi.setTextColor(Color.GRAY);
        btn_searchWifi.setLayoutParams(param);
        btn_searchWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("preferences",getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor =preferences.edit();
                editor.putBoolean("guideNeed",false);
                editor.commit();
                Intent intent=new Intent(getContext(),UsersAddActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return relativeLayout;
    }

}