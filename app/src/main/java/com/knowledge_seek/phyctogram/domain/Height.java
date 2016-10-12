package com.knowledge_seek.phyctogram.domain;

/**
 * Created by sjw on 2015-12-11.
 */
public class Height {

    private String height_seq;
    private double height;
    private String mesure_date;
    private int user_seq;
    private String input_date;
    private int rank;               //상위
    private String grow;        //전기록과의 차이
    private double height_50;   //평균키
    private String animal_img;      //내 아이 캐릭터 이미지

    public Height() {

    }

    public Height(String height_seq, double height, String mesure_date, int user_seq, String input_date, int rank, double height_50, String animal_img) {
        this.height_seq = height_seq;
        this.height = height;
        this.mesure_date = mesure_date;
        this.user_seq = user_seq;
        this.input_date = input_date;
        this.rank = rank;
        this.height_50 = height_50;
        this.animal_img = animal_img;
    }

    public String getHeight_seq() {
        return height_seq;
    }

    public void setHeight_seq(String height_seq) {
        this.height_seq = height_seq;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getMesure_date() {
        return mesure_date;
    }

    public void setMesure_date(String mesure_date) {
        this.mesure_date = mesure_date;
    }

    public int getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(int user_seq) {
        this.user_seq = user_seq;
    }

    public String getInput_date() {
        return input_date;
    }

    public void setInput_date(String input_date) {
        this.input_date = input_date;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getGrow() {
        return grow;
    }

    public void setGrow(String grow) {
        this.grow = grow;
    }

    public double getHeight_50() {
        return height_50;
    }

    public void setHeight_50(double height_50) {
        this.height_50 = height_50;
    }

    public String getAnimal_img() {
        return animal_img;
    }

    public void setAnimal_img(String animal_img) {
        this.animal_img = animal_img;
    }

    @Override
    public String toString() {
        return "Height{" +
                "height_seq='" + height_seq + '\'' +
                ", height=" + height +
                ", mesure_date='" + mesure_date + '\'' +
                ", user_seq=" + user_seq +
                ", input_date='" + input_date + '\'' +
                ", rank=" + rank +
                ", grow='" + grow + '\'' +
                ", height_50=" + height_50 +
                ", animal_img='" + animal_img + '\'' +
                '}';
    }
}
