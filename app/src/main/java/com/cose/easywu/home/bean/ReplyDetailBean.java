package com.cose.easywu.home.bean;

import java.util.Date;

public class ReplyDetailBean {
    private int id;
    private String nickName;
    private int commentId;
    private String content;
    private Date createTime;

    public ReplyDetailBean(int id, String nickName, int commentId, String content, Date createTime) {
        this.id = id;
        this.nickName = nickName;
        this.commentId = commentId;
        this.content = content;
        this.createTime = createTime;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = new Date(createTime);
    }

    @Override
    public String toString() {
        return "ReplyDetailBean{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", commentId=" + commentId +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
