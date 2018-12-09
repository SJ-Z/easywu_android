package com.cose.easywu.home.bean;

import java.util.Date;

public class ReplyDetailBean {
    private int id;
    private String nickName;
    private String userPhoto;
    private String commentId;
    private String content;
    private Date createTime;

    public ReplyDetailBean(String nickName, String content) {
        this.nickName = nickName;
        this.content = content;
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

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
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
                ", userPhoto='" + userPhoto + '\'' +
                ", commentId='" + commentId + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
