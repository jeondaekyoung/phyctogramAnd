package com.knowledge_seek.phyctogram.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.knowledge_seek.phyctogram.R;
import com.knowledge_seek.phyctogram.domain.Comment;

/**
 * Created by sjw on 2015-12-17.
 */
public class CommentListAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();

    private LayoutInflater inflater;
    private List<Comment> comments;
    private int layout;

    public CommentListAdapter(Context context, List<Comment> data, int layout) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.comments = data;
        this.layout = layout;
    }

    public void setComments(List<Comment> list){
        this.comments = list;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
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
        Comment comment = comments.get(position);
        TextView tv_member_name = (TextView)convertView.findViewById(R.id.tv_member_name);
        TextView tv_content = (TextView)convertView.findViewById(R.id.tv_content);
        tv_member_name.setText(comment.getMember_name());
        tv_content.setText(comment.getContent());
        return convertView;
    }
}
