package com.knowledge_seek.phyctogram.domain;

/**
 * Created by sjw on 2015-12-04.
 */
public class Commnty {

    private int commnty_seq;
    private String title;       //제목
    private String contents;    //내용
    private String image_nm;
    private String image_server_nm;

    //private Timestamp writng_de;
    private String writng_de;
    private int hits_co;
    private int member_seq;

    public Commnty() {

    }

    public Commnty(int commnty_seq, String title, String contents, String writng_de, int hits_co, int member_seq) {
        this.commnty_seq = commnty_seq;
        this.title = title;
        this.contents = contents;
        this.writng_de = writng_de;
        this.hits_co = hits_co;
        this.member_seq = member_seq;
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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
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

    public int getMember_seq() {
        return member_seq;
    }

    public void setMember_seq(int member_seq) {
        this.member_seq = member_seq;
    }

    @Override
    public String toString() {
        return "Commnty{" +
                "commnty_seq=" + commnty_seq +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", image_nm='" + image_nm + '\'' +
                ", image_server_nm='" + image_server_nm + '\'' +
                ", writng_de='" + writng_de + '\'' +
                ", hits_co=" + hits_co +
                ", member_seq=" + member_seq +
                '}';
    }
}
