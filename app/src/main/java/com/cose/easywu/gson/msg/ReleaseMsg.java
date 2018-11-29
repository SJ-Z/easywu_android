package com.cose.easywu.gson.msg;

public class ReleaseMsg extends BaseMsg {
    private String g_id;
    private long g_updateTime;

    public ReleaseMsg() {
    }

    public ReleaseMsg(String code, String msg, String g_id, long g_updateTime) {
        super(code, msg);
        this.g_id = g_id;
        this.g_updateTime = g_updateTime;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public long getG_updateTime() {
        return g_updateTime;
    }

    public void setG_updateTime(long g_updateTime) {
        this.g_updateTime = g_updateTime;
    }

    @Override
    public String toString() {
        return "ReleaseMsg{" +
                "g_id='" + g_id + '\'' +
                ", g_updateTime=" + g_updateTime +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
