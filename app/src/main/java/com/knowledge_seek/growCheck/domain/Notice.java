package com.knowledge_seek.growCheck.domain;

/**
 * Created by sjw on 2016-02-15.
 */
public class Notice {

    private int notice_seq;
    private String title;
    private String notice;
    private String writng_de;

    public Notice() {

    }

    public Notice(int notice_seq, String title, String notice, String writng_de) {
        this.notice_seq = notice_seq;
        this.title = title;
        this.notice = notice;
        this.writng_de = writng_de;
    }

    public int getNotice_seq() {
        return notice_seq;
    }

    public void setNotice_seq(int notice_seq) {
        this.notice_seq = notice_seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getWritng_de() {
        return writng_de;
    }

    public void setWritng_de(String writng_de) {
        this.writng_de = writng_de;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "notice_seq=" + notice_seq +
                ", title='" + title + '\'' +
                ", notice='" + notice + '\'' +
                ", writng_de='" + writng_de + '\'' +
                '}';
    }
}
