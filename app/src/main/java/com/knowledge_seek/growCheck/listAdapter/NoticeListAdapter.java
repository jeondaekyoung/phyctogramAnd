package com.knowledge_seek.growCheck.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.knowledge_seek.growCheck.R;
import com.knowledge_seek.growCheck.domain.Notice;

/**
 * Created by dkfka on 2016-02-11.
 */
public class NoticeListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Notice> notices;
    private int layout;

    public NoticeListAdapter(Context context, List<Notice> data, int layout) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.notices = data;
        this.layout = layout;
    }

    public void setNotices(List<Notice> list) {
        notices = list;
    }

    @Override
    public int getCount() {
        return notices.size();
    }

    @Override
    public Object getItem(int position) {
        return notices.get(position);
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
        Notice notice = notices.get(position);
        TextView tv_title = (TextView)convertView.findViewById(R.id.tv_title);
        TextView tv_writng_de = (TextView)convertView.findViewById(R.id.tv_writng_de);
        tv_title.setText(notices.get(position).getTitle());
        tv_writng_de.setText(notices.get(position).getWritng_de());

        /*final int pos = position;
        final Context context = parent.getContext();

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if (convertView == null) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_notice, parent, false);

            // TextView에 현재 position의 문자열 추가
            TextView txt_title = (TextView) convertView.findViewById(R.id.notice_title);
            txt_title.setText(m_List.get(position));

            TextView txt_date = (TextView) convertView.findViewById(R.id.notice_date);
            txt_date.setText(m_List.get(position));
        }*/
        return convertView;
    }
}