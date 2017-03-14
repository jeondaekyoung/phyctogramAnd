package com.knowledge_seek.growCheck.listAdapter;

/**
 * Created by sjw on 2015-12-29.
 */
public class MonthItem {

    //날짜
    private int dayValue;

    public MonthItem() {

    }

    public MonthItem(int day) {
        dayValue = day;
    }

    public int getDay() {
        return dayValue;
    }

    public void setDay(int day) {
        this.dayValue = day;
    }

    @Override
    public String toString() {
        return "MonthItem{" +
                "dayValue=" + dayValue +
                '}';
    }
}
