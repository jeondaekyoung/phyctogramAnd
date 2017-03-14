package com.knowledge_seek.growCheck.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.knowledge_seek.growCheck.R;
import com.knowledge_seek.growCheck.domain.Users;

/**
 * Created by sjw on 2015-12-08.
 */
public class UsersListSlideAdapter extends BaseAdapter {
    private Context context;
    private int selectUsers;

    //리스트
    private List<Users> usersList = new ArrayList<Users>();

    public UsersListSlideAdapter(Context context) {
        this.context = context;
    }

    public void setUsersList(List<Users> list){
        usersList.clear();
        usersList.addAll(list);
    }

    public void setSelectUsers(int selectUsers) {
        this.selectUsers = selectUsers;
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
        /*if(convertView == null){
            tv = (TextView) LayoutInflater.from(context).inflate(
                    android.R.layout.simple_list_item_1, parent, false);
            tv.setText(usersList.get(position).getName());
            Log.d("-내아이목록 리스트 null-", "번호 " + position);
        } else {
            tv = (TextView)convertView;
            tv.setText(usersList.get(position).getName());
            Log.d("-내아이목록 리스트-", "번호 " + position);
        }*/
        /*tv = (TextView) LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_1, parent, false);
        tv.setText(usersList.get(position).getName());*/

        //Log.d("내아이목록리스트", position + usersList.get(position).toString());
        TextView tv = (TextView) LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_1, parent, false);
        tv.setText(usersList.get(position).getName());
        if(selectUsers == usersList.get(position).getUser_seq()) {
            tv.setBackgroundResource(R.drawable.lv_selector);
        }
        return tv;
    }
}
