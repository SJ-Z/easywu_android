package com.hyphenate.easeui.model;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;

public class GoodsMessageHelper {

    public static String CHATTYPE = "GoodsChatType";
    public static String NewGoodsOrderType = "NewGoodsOrderType";
    public static String ConfirmGoodsOrderType = "ConfirmGoodsOrderType";
    public static String RefuseGoodsOrderType = "RefuseGoodsOrderType";
    public static String NotificationType = "NotificationType";
    public static String GOODS_ID = "goods_id";
    public static String GOODS_NAME = "goods_name";
    public static String GOODS_PRICE = "goods_price";
    public static String GOODS_PIC = "goods_pic";
    public static String GOODS_BUYER_ID = "goods_buyer_id";
    public static String MESSAGE_TIME = "time";
    public static String MESSAGE_TYPE = "type";

    public static boolean isGoodsChatType(EMMessage emMessage) {
        String TYPE = emMessage.getStringAttribute("CHATTYPE", null);
        String PRICE = emMessage.getStringAttribute(GOODS_PRICE, null);
        if (TYPE == null){
            return false;
        }
        if (TYPE.equals(CHATTYPE)){
            return true;
        }

        return false;
    }

    public static boolean isFindGoods(EMMessage emMessage) {
        String PRICE = emMessage.getStringAttribute(GOODS_PRICE, "");
        return TextUtils.isEmpty(PRICE);
    }
}
