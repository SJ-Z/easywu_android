package com.cose.easywu.gson.msg;

public class HXMsg extends BaseMsg {
    private String nick;
    private String photo;

    public HXMsg(String code, String msg, String nick, String photo) {
        super(code, msg);
        this.nick = nick;
        this.photo = photo;
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
