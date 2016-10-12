package com.knowledge_seek.phyctogram.domain;

/**
 * Created by sjw on 2015-12-17.
 */
public class Comment {

    private int comment_seq;
    private String content;
    private String writng_de;
    private int member_seq;
    private String member_name;
    private int commnty_seq;

    public Comment() {

    }

    public Comment(int comment_seq, String content, String writng_de, int member_seq, String member_name, int commnty_seq) {
        this.comment_seq = comment_seq;
        this.content = content;
        this.writng_de = writng_de;
        this.member_seq = member_seq;
        this.member_name = member_name;
        this.commnty_seq = commnty_seq;
    }

    public int getComment_seq() {
        return comment_seq;
    }

    public void setComment_seq(int comment_seq) {
        this.comment_seq = comment_seq;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWritng_de() {
        return writng_de;
    }

    public void setWritng_de(String writng_de) {
        this.writng_de = writng_de;
    }

    public int getMember_seq() {
        return member_seq;
    }

    public void setMember_seq(int member_seq) {
        this.member_seq = member_seq;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public int getCommnty_seq() {
        return commnty_seq;
    }

    public void setCommnty_seq(int commnty_seq) {
        this.commnty_seq = commnty_seq;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment_seq=" + comment_seq +
                ", content='" + content + '\'' +
                ", writng_de='" + writng_de + '\'' +
                ", member_seq=" + member_seq +
                ", member_name='" + member_name + '\'' +
                ", commnty_seq=" + commnty_seq +
                '}';
    }
}
