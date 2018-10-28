package com.cose.easywu.gson;

public class User {
    private String u_id;
    private String u_email;
    private String u_pwd;
    private String u_nick;
    private String u_photo;
    private int u_sex;
    private String u_code;
    private double u_gain;
    private int u_state;

    public User() {
    }

    public User(String u_email, String u_pwd) {
        this.u_email = u_email;
        this.u_pwd = u_pwd;
    }

    public User(String u_email, String u_pwd, String u_nick) {
        this.u_email = u_email;
        this.u_pwd = u_pwd;
        this.u_nick = u_nick;
    }

    public String getU_email() {
        return u_email;
    }

    public void setU_email(String u_email) {
        this.u_email = u_email;
    }

    public String getU_pwd() {
        return u_pwd;
    }

    public void setU_pwd(String u_pwd) {
        this.u_pwd = u_pwd;
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

    public String getU_code() {
        return u_code;
    }

    public void setU_code(String u_code) {
        this.u_code = u_code;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
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

    @Override
    public String toString() {
        return "User{" +
                "u_id='" + u_id + '\'' +
                ", u_email='" + u_email + '\'' +
                ", u_pwd='" + u_pwd + '\'' +
                ", u_nick='" + u_nick + '\'' +
                ", u_photo='" + u_photo + '\'' +
                ", u_sex=" + u_sex +
                ", u_code='" + u_code + '\'' +
                ", u_gain=" + u_gain +
                ", u_state=" + u_state +
                '}';
    }
}
