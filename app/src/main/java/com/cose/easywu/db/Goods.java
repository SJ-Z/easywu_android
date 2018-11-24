package com.cose.easywu.db;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class Goods extends LitePalSupport {

    private String g_id;
    private String g_name;
    private String g_desc;
    private double g_price;
    private double g_originalPrice;
    private String g_pic1;
    private String g_pic2;
    private String g_pic3;
    private int g_state;
    private int g_like;
    private Date g_updateTime;
    private String g_u_id;
    private String g_u_nick;
    private String g_u_photo;
    private int g_u_sex;

    public Goods() {
    }

    public Goods(String g_id, String g_name, String g_desc, double g_price, double g_originalPrice, String g_pic1, String g_pic2, String g_pic3, int g_state, int g_like, Date g_updateTime, String g_u_id, String g_u_nick, String g_u_photo, int g_u_sex) {
        this.g_id = g_id;
        this.g_name = g_name;
        this.g_desc = g_desc;
        this.g_price = g_price;
        this.g_originalPrice = g_originalPrice;
        this.g_pic1 = g_pic1;
        this.g_pic2 = g_pic2;
        this.g_pic3 = g_pic3;
        this.g_state = g_state;
        this.g_like = g_like;
        this.g_updateTime = g_updateTime;
        this.g_u_id = g_u_id;
        this.g_u_nick = g_u_nick;
        this.g_u_photo = g_u_photo;
        this.g_u_sex = g_u_sex;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getG_desc() {
        return g_desc;
    }

    public void setG_desc(String g_desc) {
        this.g_desc = g_desc;
    }

    public double getG_price() {
        return g_price;
    }

    public void setG_price(double g_price) {
        this.g_price = g_price;
    }

    public double getG_originalPrice() {
        return g_originalPrice;
    }

    public void setG_originalPrice(double g_originalPrice) {
        this.g_originalPrice = g_originalPrice;
    }

    public String getG_pic1() {
        return g_pic1;
    }

    public void setG_pic1(String g_pic1) {
        this.g_pic1 = g_pic1;
    }

    public String getG_pic2() {
        return g_pic2;
    }

    public void setG_pic2(String g_pic2) {
        this.g_pic2 = g_pic2;
    }

    public String getG_pic3() {
        return g_pic3;
    }

    public void setG_pic3(String g_pic3) {
        this.g_pic3 = g_pic3;
    }

    public int getG_state() {
        return g_state;
    }

    public void setG_state(int g_state) {
        this.g_state = g_state;
    }

    public int getG_like() {
        return g_like;
    }

    public void setG_like(int g_like) {
        this.g_like = g_like;
    }

    public Date getG_updateTime() {
        return g_updateTime;
    }

    public void setG_updateTime(Date g_updateTime) {
        this.g_updateTime = g_updateTime;
    }

    public String getG_u_id() {
        return g_u_id;
    }

    public void setG_u_id(String g_u_id) {
        this.g_u_id = g_u_id;
    }

    public String getG_u_nick() {
        return g_u_nick;
    }

    public void setG_u_nick(String g_u_nick) {
        this.g_u_nick = g_u_nick;
    }

    public String getG_u_photo() {
        return g_u_photo;
    }

    public void setG_u_photo(String g_u_photo) {
        this.g_u_photo = g_u_photo;
    }

    public int getG_u_sex() {
        return g_u_sex;
    }

    public void setG_u_sex(int g_u_sex) {
        this.g_u_sex = g_u_sex;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "g_id='" + g_id + '\'' +
                ", g_name='" + g_name + '\'' +
                ", g_desc='" + g_desc + '\'' +
                ", g_price=" + g_price +
                ", g_originalPrice=" + g_originalPrice +
                ", g_pic1='" + g_pic1 + '\'' +
                ", g_pic2='" + g_pic2 + '\'' +
                ", g_pic3='" + g_pic3 + '\'' +
                ", g_state=" + g_state +
                ", g_like=" + g_like +
                ", g_updateTime=" + g_updateTime +
                ", g_u_id='" + g_u_id + '\'' +
                ", g_u_nick='" + g_u_nick + '\'' +
                ", g_u_photo='" + g_u_photo + '\'' +
                ", g_u_sex=" + g_u_sex +
                '}';
    }
}
