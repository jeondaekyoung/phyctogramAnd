package com.knowledge_seek.phyctogram.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.knowledge_seek.phyctogram.R;
import com.knowledge_seek.phyctogram.domain.Qa;

/**
 * Created by sjw on 2016-02-16.
 */
public class QaListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Qa> qas;
    private int layout;

    public QaListAdapter(Context context, List<Qa> data, int layout) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.qas = data;
        this.layout = layout;
    }

    public void setQas(List<Qa> list) {
        qas = list;
    }

    @Override
    public int getCount() {
        return qas.size();
    }

    @Override
    public Object getItem(int position) {
        return qas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        Qa qa = qas.get(position);
        TextView tv_title = (TextView)convertView.findViewById(R.id.tv_title);
        TextView tv_writng_de = (TextView)convertView.findViewById(R.id.tv_writng_de);
        TextView tv_state = (TextView)convertView.findViewById(R.id.tv_state);
        tv_title.setText(qas.get(position).getTitle());
        tv_writng_de.setText(qas.get(position).getWritng_de());
        tv_state.setText(qas.get(position).getState());
        return convertView;
    }
}
