package com.knowledge_seek.phyctogram.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.knowledge_seek.phyctogram.domain.Users;

/**
 * Created by sjw on 2015-12-08.
 */
public class UsersListManageAdapter extends BaseAdapter {
    private Context context;
    TextView tv;

    //리스트
    private List<Users> usersList = new ArrayList<Users>();

    public UsersListManageAdapter(Context context) {
        this.context = context;
    }

    public void setUsersList(List<Users> list){
        usersList = list;
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Object getItem(int position) {
        return usersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            tv = (TextView) LayoutInflater.from(context).inflate(
                    android.R.layout.simple_expandable_list_item_1, parent, false);
            tv.setText(usersList.get(position).getName());

        } else {
            tv = (TextView)convertView;
            tv.setText(usersList.get(position).getName());
        }
        return tv;
    }
}
