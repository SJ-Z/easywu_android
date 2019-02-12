package com.cose.easywu.gson.msg;

import com.cose.easywu.find.bean.FindDataBean;
import com.google.gson.annotations.SerializedName;

public class FindGoodsMsg extends BaseMsg {

    @SerializedName("findGoods")
    private FindDataBean.FindNewestInfo findGoods;

    public FindGoodsMsg(FindDataBean.FindNewestInfo findGoods) {
        this.findGoods = findGoods;
    }

    public FindGoodsMsg(String code, String msg, FindDataBean.FindNewestInfo findGoods) {
        super(code, msg);
        this.findGoods = findGoods;
    }

    public FindDataBean.FindNewestInfo getFindGoods() {
        return findGoods;
    }

    public void setFindGoods(FindDataBean.FindNewestInfo findGoods) {
        this.findGoods = findGoods;
    }

    @Override
    public String toString() {
        return "FindGoodsMsg{" +
                "findGoods=" + findGoods +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
