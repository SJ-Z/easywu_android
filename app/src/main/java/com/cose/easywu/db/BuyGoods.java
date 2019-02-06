package com.cose.easywu.db;

import java.util.Date;

public class BuyGoods extends ReleaseGoods {

    private String g_u_id;
    private String g_u_nick;
    private String g_u_photo;
    private int g_u_sex;

    public BuyGoods(String g_id, String g_name, String g_desc, double g_price, double g_originalPrice, String g_pic1, String g_pic2, String g_pic3, int g_state, int g_like, Date g_updateTime, String g_t_id, String g_u_id, String g_u_nick, String g_u_photo, int g_u_sex) {
        super(g_id, g_name, g_desc, g_price, g_originalPrice, g_pic1, g_pic2, g_pic3, g_state, g_like, g_updateTime, g_t_id);
        this.g_u_id = g_u_id;
        this.g_u_nick = g_u_nick;
        this.g_u_photo = g_u_photo;
        this.g_u_sex = g_u_sex;
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
}
