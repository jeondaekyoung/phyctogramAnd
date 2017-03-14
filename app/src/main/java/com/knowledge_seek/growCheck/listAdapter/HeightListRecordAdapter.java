package com.knowledge_seek.growCheck.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.knowledge_seek.growCheck.R;
import com.knowledge_seek.growCheck.domain.Height;

import java.util.List;

/**
 * Created by sjw on 2015-12-11.
 */
public class HeightListRecordAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Height> heights;
    private int layout;

    public HeightListRecordAdapter(Context context, List<Height> data, int layout) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.heights = data;
        this.layout = layout;
    }

    public void setHeights(List<Height> list){
        heights = list;
    }

    @Override
    public int getCount() {
        return heights.size();
    }

    @Override
    public Object getItem(int position) {
        return heights.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        Height height = heights.get(position);
        TextView recordDate = (TextView)convertView.findViewById(R.id.recordDate);
        TextView recordHeight = (TextView)convertView.findViewById(R.id.recordHeight);
        TextView recordGrow = (TextView)convertView.findViewById(R.id.record_grow);
        ImageView recordSa = (ImageView) convertView.findViewById(R.id.record_sa);
        TextView recordRank = (TextView)convertView.findViewById(R.id.record_rank);
        recordDate.setText(String.valueOf(height.getMesure_date()));
        recordHeight.setText(String.valueOf(height.getHeight()));
        if(Double.valueOf(height.getGrow()) >= 0){
            recordSa.setImageResource(R.drawable.record_up);
            recordGrow.setText(String.valueOf(height.getGrow()));
        } else {
            recordSa.setImageResource(R.drawable.record_down);
            recordGrow.setText(String.valueOf(height.getGrow()).substring(1));
        }

        recordRank.setText(height.getRank() + "%");

        return convertView;
    }
}
