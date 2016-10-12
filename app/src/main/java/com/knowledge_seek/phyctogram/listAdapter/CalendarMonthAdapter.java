package com.knowledge_seek.phyctogram.listAdapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.knowledge_seek.phyctogram.domain.Diary;

/**
 * Created by sjw on 2015-12-29.
 */
public class CalendarMonthAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();

    Context mContext;

    //public static int oddColor = Color.rgb(225, 225, 225);
    //public static int headColor = Color.rgb(12, 32, 158);

    private int selectedPosition = -1;

    //하루하루(일) 데이터
    private MonthItem[] items;

    private int countColumn = 7;

    int mStartDay;
    int startDay;
    int curYear;
    int curMonth;

    int firstDay;
    int lastDay;

    Calendar mCalendar;
    //boolean recreateItems = false;

    //일기 데이터
    List<Diary> diaryList;
    //HashMap<String, String> diaryHash;


    public CalendarMonthAdapter(Context context){
        super();
        mContext = context;
        init();
    }

    public CalendarMonthAdapter(Context context, AttributeSet attrs){
        super();
        mContext = context;
        init();
    }

    private void init(){
        items = new MonthItem[7*6];
        mCalendar = Calendar.getInstance();
        recalculate();
        resetDayNumbers();
        diaryList = new ArrayList<Diary>();
        //diaryHash = new HashMap<String, String>();
    }

    public void recalculate(){
        //Calendar.DAY_OF_MONTH -> 현재 월의 날짜
        //현재 월의 날짜를 1일로 셋팅
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        //Calendar.DAY_OF_WEEK -> 현재요일(일요일은1, 토요일은 7)
        int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        firstDay = getFirstDay(dayOfWeek);
        //월요일 1, 토요일 6, 일요일 0
        Log.d(TAG, "firstDay : " + firstDay);

        //getFirstDayOfWeek() -> 주의 첫째날은 무슨 요일, 일요일1, 월요일2, 토요일7
        mStartDay = mCalendar.getFirstDayOfWeek();
        curYear = mCalendar.get(Calendar.YEAR);
        curMonth = mCalendar.get(Calendar.MONTH);
        //현재 날짜의 달의 마지막 날짜는 몇일
        lastDay = getMonthLastDay(curYear, curMonth);
        Log.d(TAG, "현재 년 : " + curYear + ", 현재 달 : " + (curMonth+1) + ", 마지막 날짜 : " + lastDay);

        int diff = mStartDay - Calendar.SUNDAY -1;
        startDay = getFirstDayOfWeek();
        Log.d(TAG, "주의 시작 요일 : " + mStartDay + ", 주의 시작요일(토요일6, 월요일1, 일요일0) : " + startDay);
    }

    //이전 월로 이동시 일별 데이터 새로 계산
    public void setPreviousMonth(){
        mCalendar.add(Calendar.MONTH, -1);
        recalculate();
        resetDayNumbers();
        selectedPosition = -1;
    }

    //다음 월로 이동시 일별 데이터 새로 계산
    public void setNextMonth(){
        mCalendar.add(Calendar.MONTH, 1);
        recalculate();
        resetDayNumbers();
        selectedPosition = -1;
    }

    //지정한 월의 일별 데이터를 새로 계산하는 메소드 정의
    public void resetDayNumbers(){
        //42개(7*6)의 items 객체 설정
        for(int i=0; i<42; i++){
            int dayNumber = (i+1) - firstDay;
            if(dayNumber < 1 || dayNumber > lastDay){
                dayNumber = 0;
            }
            items[i] = new MonthItem(dayNumber);
        }
    }

    //요일별로 숫자로 리턴함
    private int getFirstDay(int dayOfWeek){
        int result = 0;
        if(dayOfWeek == Calendar.SUNDAY){
            result = 0;
        } else if(dayOfWeek == Calendar.MONDAY){
            result = 1;
        } else if(dayOfWeek == Calendar.TUESDAY){
            result = 2;
        } else if(dayOfWeek == Calendar.WEDNESDAY){
            result = 3;
        } else if(dayOfWeek == Calendar.THURSDAY){
            result = 4;
        } else if(dayOfWeek == Calendar.FRIDAY){
            result = 5;
        } else if(dayOfWeek == Calendar.SATURDAY){
            result = 6;
        }
        return result;
    }

    //현재 달의 마지막 날짜 리턴함
    private int getMonthLastDay(int year, int month){
        switch(month){
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return (31);

            case 3:
            case 5:
            case 8:
            case 10:
                return (30);

            default:
                if(((year%4==0) && (year%100!=0)) || (year%400==0)){
                    return (29);        //2월 윤달계산
                } else {
                    return (28);
                }
        }
    }

    //주의 첫째날을 android.text.format.Time로 리턴함
    public static int getFirstDayOfWeek(){
        int startDay = Calendar.getInstance().getFirstDayOfWeek();
        if(startDay == Calendar.SATURDAY){
            return Time.SATURDAY;       //6
        } else if(startDay == Calendar.MONDAY){
            return Time.MONDAY;         //1
        } else {
            return Time.SUNDAY;         //0
        }
    }

    public int getCurYear() {
        return curYear;
    }

    public int getCurMonth(){
        return curMonth;
    }

    public int getNumColumns(){
        return 7;
    }

    public int getCount(){
        return 7*6;
    }

    public Object getItem(int position){
        return items[position];
    }

    public long getItemId(int position){
        return position;
    }

    public void setDiaryList(List<Diary> diarys) {
        this.diaryList = diarys;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d(TAG, "현재 선택 : " + position);
        MonthItemView itemView;
        if(convertView == null){
            itemView = new MonthItemView(mContext);
        } else {
            itemView = (MonthItemView)convertView;
            itemView.setTitleClear();
        }

        //params 셋팅
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT, parent.getHeight()/6);
        //달력의 행과 열 계산, 인텍스값 계산
        int rowIndex = position / countColumn;
        int columnIndex = position % countColumn;
        //Log.d(TAG, "달력(그리드)에서 위치(row,column) : " + rowIndex + ", " + columnIndex);

        //현재 날짜의 MonthItem 객체 설정
        itemView.setItem(items[position]);
        itemView.setLayoutParams(params);
        itemView.setPadding(2, 2, 2, 2);
        itemView.setGravity(Gravity.LEFT);

        if(columnIndex == 0){
            itemView.setDayColor(Color.rgb(255,100,100));       //일요일은 빨강색 글씨
        } else if(columnIndex == 6){
            itemView.setDayColor(Color.rgb(110,155,255));       //토요일은 파란색 글씨
        } else {
            itemView.setDayColor(Color.rgb(180,180,180));
        }

        if(position == getSelectedPosition()){
            itemView.setBackgroundColor(Color.rgb(203,186,229));      //선택한 날짜 배경색입히기
        } else {
            itemView.setBackgroundColor(Color.WHITE);
        }
        //-> 현재 날짜의 MonthItem 객체 설정

        //일기 제목을 달력에 출력
        for(Diary d : diaryList){
            int diary_writng_de = Integer.parseInt(d.getWritng_de());
            int day = itemView.getItem().getDay();
            if(diary_writng_de == day){
                //Log.d("-진우-", "CalendarMonthAdapter.getView() : " + d.toString());
                //Log.d("-진우-", "위치 : " + position + ", (" + rowIndex +"," + columnIndex + ") ");
                //Log.d("-진우-", "diary_writng_de :" + diary_writng_de + ", MonthItem.day : " + day);
                itemView.setTitle(d.getTitle());
            }

        }

        return itemView;
    }

    //선택한 날짜 저장
    public void setSelectedPosition(int selectedPosition){
        this.selectedPosition = selectedPosition;
    }
    public int getSelectedPosition(){
        return selectedPosition;
    }

}
