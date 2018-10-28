package com.cose.easywu.db;

import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {

    private String u_id;
    private String u_email;
    private String u_nick;
    private String u_photo;
    private int u_sex;
    private double u_gain;
    private int u_state;

    public User() {
    }

    public User(String u_id, String u_email, String u_nick, String u_photo, int u_sex, double u_gain, int u_state) {
        this.u_id = u_id;
        this.u_email = u_email;
        this.u_nick = u_nick;
        this.u_photo = u_photo;
        this.u_sex = u_sex;
        this.u_gain = u_gain;
        this.u_state = u_state;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getU_email() {
        return u_email;
    }

    public void setU_email(String u_email) {
        this.u_email = u_email;
    }

    public String getU_nick() {
        return u_nick;
    }

    public void setU_nick(String u_nick) {
        this.u_nick = u_nick;
    }

    public String getU_photo() {
        return u_photo;
    }

    public void setU_photo(String u_photo) {
        this.u_photo = u_photo;
    }

    public int getU_sex() {
        return u_sex;
    }

    public void setU_sex(int u_sex) {
        this.u_sex = u_sex;
    }

    public double getU_gain() {
        return u_gain;
    }

    public void setU_gain(double u_gain) {
        this.u_gain = u_gain;
    }

    public int getU_state() {
        return u_state;
    }

    public void setU_state(int u_state) {
        this.u_state = u_state;
    }

}
