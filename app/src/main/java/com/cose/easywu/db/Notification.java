package com.cose.easywu.db;

import com.cose.easywu.utils.NotificationHelper;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class Notification extends LitePalSupport {

    private String title;
    private String content;
    private String g_id;
    private Date time;
    private int type; // 通知的类型见NotificationHelper
    private int state; // 通知的状态见NotificationHelper

    public Notification() {
    }

    public Notification(String title, String content, String g_id, long time, int type) {
        this.title = title;
        this.content = content;
        this.g_id = g_id;
        this.time = new Date(time);
        this.type = type;
        this.state = NotificationHelper.STATE_RECEIVE; // 新建通知时，通知的类型应为收到
    }

    public Notification(String title, String content, long time, int type) {
        this.title = title;
        this.content = content;
        this.time = new Date(time);
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
