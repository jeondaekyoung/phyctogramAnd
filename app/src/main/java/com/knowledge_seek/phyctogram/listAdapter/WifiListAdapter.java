package com.knowledge_seek.phyctogram.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.knowledge_seek.phyctogram.R;
import com.knowledge_seek.phyctogram.domain.Wifi;

/**
 * Created by shj on 2016-02-23.
 */
public class WifiListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Wifi> wifis;
    private int layout;

    public WifiListAdapter(Context context, List<Wifi> wifis, int layout) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.wifis = wifis;
        this.layout = layout;
    }

    public void setWifis(List<Wifi> wifis) {
        this.wifis = wifis;
    }

    @Override
    public int getCount() {
        return wifis.size();
    }

    @Override
    public Object getItem(int position) {
        return wifis.get(position);
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
        Wifi wifi = wifis.get(position);
        TextView tv_ssid = (TextView)convertView.findViewById(R.id.tv_ssid);
        //TextView tv_capabilities = (TextView)convertView.findViewById(R.id.tv_capabilities);
        tv_ssid.setText(wifis.get(position).getSsid());
        //tv_capabilities.setText(wifis.get(position).getCapabilities());
        return convertView;
    }
}
