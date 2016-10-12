package com.knowledge_seek.phyctogram.domain;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by sjw on 2015-12-29.
 */
public class Diary implements Serializable {

    private int diary_seq;
    private String title;
    private String contents;
    private String writng_year;
    private String writng_mt;
    private String writng_de;
    private String image_nm;
    private String image_server_nm;
    private int user_seq;

    public Diary() {

    }

    public Diary(int diary_seq, String title, String contents, String writng_year, String writng_mt, String writng_de, int user_seq) {
        this.diary_seq = diary_seq;
        this.title = title;
        this.contents = contents;
        this.writng_year = writng_year;
        this.writng_mt = writng_mt;
        this.writng_de = writng_de;
        this.user_seq = user_seq;
    }

    public int getDiary_seq() {
        return diary_seq;
    }

    public void setDiary_seq(int diary_seq) {
        this.diary_seq = diary_seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getWritng_year() {
        return writng_year;
    }

    public void setWritng_year(String writng_year) {
        this.writng_year = writng_year;
    }

    public String getWritng_mt() {
        return writng_mt;
    }

    public void setWritng_mt(String writng_mt) {
        this.writng_mt = writng_mt;
    }

    public String getWritng_de() {
        return writng_de;
    }

    public void setWritng_de(String writng_de) {
        this.writng_de = writng_de;
    }

    public String getImage_nm() {
        return image_nm;
    }

    public void setImage_nm(String image_nm) {
        this.image_nm = image_nm;
    }

    public String getImage_server_nm() {
        return image_server_nm;
    }

    public void setImage_server_nm(String image_server_nm) {
        this.image_server_nm = image_server_nm;
    }

    public int getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(int user_seq) {
        this.user_seq = user_seq;
    }

    @Override
    public String toString() {
        return "Diary{" +
                "diary_seq=" + diary_seq +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writng_year='" + writng_year + '\'' +
                ", writng_mt='" + writng_mt + '\'' +
                ", writng_de='" + writng_de + '\'' +
                ", image_nm='" + image_nm + '\'' +
                ", image_server_nm='" + image_server_nm + '\'' +
                ", user_seq=" + user_seq +
                '}';
    }

    //일기 시퀀스는 비교하지 않는다.
    public boolean equals(Diary d) {
        Log.d("-일기 비교 전-", this.toString());
        Log.d("-일기 비교 후-", d.toString());
        if(this.getTitle().equals(d.getTitle()) &&
                this.getContents().equals(d.getContents()) &&
                (this.getUser_seq() == d.getUser_seq()) &&
                this.getWritng_year().equals(d.getWritng_year()) &&
                this.getWritng_mt().equals(d.getWritng_mt()) &&
                this.getWritng_de().equals(d.getWritng_de()) ){
            return true;
        }
        return false;
    }
}
