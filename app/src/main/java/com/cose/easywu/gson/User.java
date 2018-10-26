package com.cose.easywu.gson;

public class User {
    private String u_email;
    private String u_pwd;
    private String u_nick;
    private String u_code;

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

    public User(String u_email, String u_pwd, String u_nick, String u_code) {
        this.u_email = u_email;
        this.u_pwd = u_pwd;
        this.u_nick = u_nick;
        this.u_code = u_code;
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

    public String getU_code() {
        return u_code;
    }

    public void setU_code(String u_code) {
        this.u_code = u_code;
    }

    @Override
    public String toString() {
        return "User{" +
                "u_email='" + u_email + '\'' +
                ", u_pwd='" + u_pwd + '\'' +
                ", u_nick='" + u_nick + '\'' +
                ", u_code='" + u_code + '\'' +
                '}';
    }
}
