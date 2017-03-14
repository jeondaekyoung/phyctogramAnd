package com.knowledge_seek.growCheck.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.knowledge_seek.growCheck.R;
import com.knowledge_seek.growCheck.domain.SqlCommntyListView;

/**
 * Created by sjw on 2015-12-15.
 */
public class SqlCommntyListViewListAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();

    private LayoutInflater inflater;
    private List<SqlCommntyListView> sqlCommntyListViews;
    private int layout;

    public SqlCommntyListViewListAdapter(Context context, List<SqlCommntyListView> data, int layout) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sqlCommntyListViews = data;
        this.layout = layout;
    }

    public void setSqlCommntyListViews(List<SqlCommntyListView> list){
        sqlCommntyListViews = list;
    }

    @Override
    public int getCount() {
        return sqlCommntyListViews.size();
    }

    @Override
    public Object getItem(int position) {
        return sqlCommntyListViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(layout, parent, false);
        }
        SqlCommntyListView sqlCommntyListView = sqlCommntyListViews.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.community_title);
        TextView name = (TextView) convertView.findViewById(R.id.community_userid);
        TextView writng_de = (TextView) convertView.findViewById(R.id.community_date);
        TextView hits_co = (TextView) convertView.findViewById(R.id.community_read);
        TextView cnt = (TextView) convertView.findViewById(R.id.community_reply);
        title.setText(sqlCommntyListView.getTitle());
        name.setText(sqlCommntyListView.getName());
        writng_de.setText(sqlCommntyListView.getWritng_de());
        hits_co.setText(String.valueOf(sqlCommntyListView.getHits_co()));
        cnt.setText(String.valueOf(sqlCommntyListView.getCnt()));

        return convertView;
    }
}
