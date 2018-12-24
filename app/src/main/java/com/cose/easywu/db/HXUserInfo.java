package com.cose.easywu.db;

import org.litepal.crud.LitePalSupport;

public class HXUserInfo extends LitePalSupport {

    private String uid;
    private String nick;
    private String photo;

    public HXUserInfo() {
    }

    public HXUserInfo(String uid, String nick, String photo) {
        this.uid = uid;
        this.nick = nick;
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
