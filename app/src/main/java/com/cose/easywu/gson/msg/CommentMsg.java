package com.cose.easywu.gson.msg;

import java.util.Date;

public class CommentMsg extends BaseMsg {

    private int id;
    private Date time;

    public CommentMsg(int id, Date time) {
        this.id = id;
        this.time = time;
    }

    public CommentMsg(String code, String msg, int id, Date time) {
        super(code, msg);
        this.id = id;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
