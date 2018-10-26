package com.cose.easywu.gson.msg;

public class LoginMsg extends BaseMsg {
    private String u_id; // 用户id

    public LoginMsg() {
    }

    public LoginMsg(String code, String msg, String u_id) {
        this.code = code;
        this.msg = msg;
        this.u_id = u_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    @Override
    public String toString() {
        return "LoginMsg{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", u_id='" + u_id + '\'' +
                '}';
    }
}
