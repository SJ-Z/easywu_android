package com.cose.easywu.db;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class LikeFindGoods extends LitePalSupport {

    private String fg_id;
    private String fg_name;
    private String fg_desc;
    private String fg_pic1;
    private String fg_pic2;
    private String fg_pic3;
    private int fg_state;
    private int fg_like;
    private Date fg_updateTime;
    private String fg_ft_id;
    private String fg_u_id;
    private String fg_u_nick;
    private String fg_u_photo;
    private String fg_u_sex;

    public LikeFindGoods() {
    }

    public LikeFindGoods(String fg_id, String fg_name, String fg_desc, String fg_pic1, String fg_pic2, String fg_pic3, int fg_state, int fg_like, Date fg_updateTime, String fg_ft_id, String fg_u_id, String fg_u_nick, String fg_u_photo, String fg_u_sex) {
        this.fg_id = fg_id;
        this.fg_name = fg_name;
        this.fg_desc = fg_desc;
        this.fg_pic1 = fg_pic1;
        this.fg_pic2 = fg_pic2;
        this.fg_pic3 = fg_pic3;
        this.fg_state = fg_state;
        this.fg_like = fg_like;
        this.fg_updateTime = fg_updateTime;
        this.fg_ft_id = fg_ft_id;
        this.fg_u_id = fg_u_id;
        this.fg_u_nick = fg_u_nick;
        this.fg_u_photo = fg_u_photo;
        this.fg_u_sex = fg_u_sex;
    }

    public String getFg_id() {
        return fg_id;
    }

    public void setFg_id(String fg_id) {
        this.fg_id = fg_id;
    }

    public String getFg_name() {
        return fg_name;
    }

    public void setFg_name(String fg_name) {
        this.fg_name = fg_name;
    }

    public String getFg_desc() {
        return fg_desc;
    }

    public void setFg_desc(String fg_desc) {
        this.fg_desc = fg_desc;
    }

    public String getFg_pic1() {
        return fg_pic1;
    }

    public void setFg_pic1(String fg_pic1) {
        this.fg_pic1 = fg_pic1;
    }

    public String getFg_pic2() {
        return fg_pic2;
    }

    public void setFg_pic2(String fg_pic2) {
        this.fg_pic2 = fg_pic2;
    }

    public String getFg_pic3() {
        return fg_pic3;
    }

    public void setFg_pic3(String fg_pic3) {
        this.fg_pic3 = fg_pic3;
    }

    public int getFg_state() {
        return fg_state;
    }

    public void setFg_state(int fg_state) {
        this.fg_state = fg_state;
    }

    public int getFg_like() {
        return fg_like;
    }

    public void setFg_like(int fg_like) {
        this.fg_like = fg_like;
    }

    public Date getFg_updateTime() {
        return fg_updateTime;
    }

    public void setFg_updateTime(Date fg_updateTime) {
        this.fg_updateTime = fg_updateTime;
    }

    public String getFg_ft_id() {
        return fg_ft_id;
    }

    public void setFg_ft_id(String fg_ft_id) {
        this.fg_ft_id = fg_ft_id;
    }

    public String getFg_u_id() {
        return fg_u_id;
    }

    public void setFg_u_id(String fg_u_id) {
        this.fg_u_id = fg_u_id;
    }

    public String getFg_u_nick() {
        return fg_u_nick;
    }

    public void setFg_u_nick(String fg_u_nick) {
        this.fg_u_nick = fg_u_nick;
    }

    public String getFg_u_photo() {
        return fg_u_photo;
    }

    public void setFg_u_photo(String fg_u_photo) {
        this.fg_u_photo = fg_u_photo;
    }

    public String getFg_u_sex() {
        return fg_u_sex;
    }

    public void setFg_u_sex(String fg_u_sex) {
        this.fg_u_sex = fg_u_sex;
    }

    @Override
    public String toString() {
        return "LikeFindGoods{" +
                "fg_id='" + fg_id + '\'' +
                ", fg_name='" + fg_name + '\'' +
                ", fg_desc='" + fg_desc + '\'' +
                ", fg_pic1='" + fg_pic1 + '\'' +
                ", fg_pic2='" + fg_pic2 + '\'' +
                ", fg_pic3='" + fg_pic3 + '\'' +
                ", fg_state=" + fg_state +
                ", fg_like=" + fg_like +
                ", fg_updateTime=" + fg_updateTime +
                ", fg_ft_id='" + fg_ft_id + '\'' +
                ", fg_u_id='" + fg_u_id + '\'' +
                ", fg_u_nick='" + fg_u_nick + '\'' +
                ", fg_u_photo='" + fg_u_photo + '\'' +
                ", fg_u_sex='" + fg_u_sex + '\'' +
                '}';
    }
}
