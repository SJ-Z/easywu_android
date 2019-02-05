package com.hyphenate.easeui.model;

import com.hyphenate.chat.EMMessage;

public class GoodsMessageHelper {

    public static String CHATTYPE = "GoodsChatType";
    public static String NewGoodsOrderType = "NewGoodsOrderType";
    public static String ConfirmGoodsOrderType = "ConfirmGoodsOrderType";
    public static String RefuseGoodsOrderType = "RefuseGoodsOrderType";
    public static String GOODS_ID = "goods_id";
    public static String GOODS_NAME = "goods_name";
    public static String GOODS_PRICE = "goods_price";
    public static String GOODS_PIC = "goods_pic";
    public static String GOODS_BUYER_ID = "goods_buyer_id";
    public static String MESSAGE_TIME = "time";
    public static String MESSAGE_TYPE = "type";

    public static boolean isGoodsChatType(EMMessage emMessage) {
        String TYPE = emMessage.getStringAttribute("CHATTYPE", null);
        if (TYPE == null){
            return false;
        }
        if (TYPE.equals(CHATTYPE)){
            return true;
        }

        return false;
    }
}
