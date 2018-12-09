package com.cose.easywu.home.bean;

import java.util.List;

public class CommentBean {

    private int id;
    private String g_id;
    private List<CommentDetailBean> list;

    public CommentBean() {
    }

    public CommentBean(int id, String g_id, List<CommentDetailBean> list) {
        this.id = id;
        this.g_id = g_id;
        this.list = list;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public List<CommentDetailBean> getList() {
        return list;
    }

    public void setList(List<CommentDetailBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "CommentBean{" +
                "id=" + id +
                ", g_id='" + g_id + '\'' +
                ", list=" + list +
                '}';
    }
}
