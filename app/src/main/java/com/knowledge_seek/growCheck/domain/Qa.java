package com.knowledge_seek.growCheck.domain;

/**
 * Created by sjw on 2016-02-16.
 */
public class Qa {

    private int qa_seq;
    private String title;
    private String contents;
    private String writng_de;
    private String answer;
    private int member_seq;
    private String state;

    public Qa() {

    }

    public Qa(int qa_seq, String title, String contents, String writng_de, String answer, int member_seq, String state) {
        this.qa_seq = qa_seq;
        this.title = title;
        this.contents = contents;
        this.writng_de = writng_de;
        this.answer = answer;
        this.member_seq = member_seq;
        this.state = state;
    }

    public int getQa_seq() {
        return qa_seq;
    }

    public void setQa_seq(int qa_seq) {
        this.qa_seq = qa_seq;
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

    public String getWritng_de() {
        return writng_de;
    }

    public void setWritng_de(String writng_de) {
        this.writng_de = writng_de;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getMember_seq() {
        return member_seq;
    }

    public void setMember_seq(int member_seq) {
        this.member_seq = member_seq;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Qa{" +
                "qa_seq=" + qa_seq +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writng_de='" + writng_de + '\'' +
                ", answer='" + answer + '\'' +
                ", member_seq=" + member_seq +
                ", state='" + state + '\'' +
                '}';
    }
}
