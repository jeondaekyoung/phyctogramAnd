package com.knowledge_seek.growCheck.domain;

import java.io.Serializable;

/**
 * Created by sjw on 2015-12-15.
 */
public class SqlCommntyListView implements Serializable {

    private int commnty_seq;        //커뮤니티(수다방)_seq
    private String title;                   //제목
    private String name;                //작성자 이름
    private String writng_de;         //작성일자
    private int hits_co;                //조회수
    private int cnt;                    //댓글 수

    public SqlCommntyListView() {

    }

    public SqlCommntyListView(int commnty_seq, String title, String name, String writng_de, int hits_co, int cnt) {
        this.commnty_seq = commnty_seq;
        this.title = title;
        this.name = name;
        this.writng_de = writng_de;
        this.hits_co = hits_co;
        this.cnt = cnt;
    }

    public int getCommnty_seq() {
        return commnty_seq;
    }

    public void setCommnty_seq(int commnty_seq) {
        this.commnty_seq = commnty_seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWritng_de() {
        return writng_de;
    }

    public void setWritng_de(String writng_de) {
        this.writng_de = writng_de;
    }

    public int getHits_co() {
        return hits_co;
    }

    public void setHits_co(int hits_co) {
        this.hits_co = hits_co;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    @Override
    public String toString() {
        return "SqlCommntyListView{" +
                "commnty_seq=" + commnty_seq +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", writng_de='" + writng_de + '\'' +
                ", hits_co=" + hits_co +
                ", cnt=" + cnt +
                '}';
    }
}
