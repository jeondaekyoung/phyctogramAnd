package com.knowledge_seek.growCheck.listAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowledge_seek.growCheck.R;

/**
 * Created by sjw on 2015-12-29.
 */
public class MonthItemView extends RelativeLayout {

    private Context mContext;
    private MonthItem item;

    private RelativeLayout rl_month_item_container;
    private TextView tv_month_item_day;
    private TextView tv_month_item_title;

    public MonthItemView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MonthItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_month_item, this, true);

        rl_month_item_container = (RelativeLayout)findViewById(R.id.rl_month_item_container);
        tv_month_item_day = (TextView)findViewById(R.id.tv_month_item_day);
        tv_month_item_title = (TextView)findViewById(R.id.tv_month_item_title);

        rl_month_item_container.setBackgroundColor(Color.WHITE);
    }

    public MonthItem getItem() {
        return item;
    }

    public void setItem(MonthItem item) {
        this.item = item;

        int day = item.getDay();
        if (day != 0) {
            tv_month_item_day.setText(String.valueOf(day));
        } else {
            tv_month_item_day.setText("");
        }

    }

    public void setDayColor(int color) {
        tv_month_item_day.setTextColor(color);
    }

    public void setTitle(String title) {
        tv_month_item_title.setText(title);
        tv_month_item_title.setBackgroundColor(Color.rgb(203,186,229));
        tv_month_item_title.setVisibility(View.VISIBLE);
    }

    public void setTitleClear() {
        tv_month_item_title.setText("");
        tv_month_item_title.setBackgroundColor(Color.WHITE);
        tv_month_item_title.setVisibility(View.GONE);
    }
}
