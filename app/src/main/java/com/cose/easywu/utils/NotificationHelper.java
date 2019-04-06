package com.cose.easywu.utils;

public class NotificationHelper {

    // 通知的类型
    public static int TYPE_GOODS_COMMENT = 0;
    public static int TYPE_GOODS_REPLY = 1;
    public static int TYPE_NEW_ORDER_GOODS = 2;
    public static int TYPE_CONFIRM_ORDER_GOODS = 3;
    public static int TYPE_REFUSE_ORDER_GOODS = 4;
    public static int TYPE_FIND_GOODS_COMMENT = 5;
    public static int TYPE_FIND_GOODS_REPLY = 6;
    public static int TYPE_FIND_PEOPLE_COMMENT = 7;
    public static int TYPE_FIND_PEOPLE_REPLY = 8;
    public static int TYPE_NOTIFICATION_TZSC_ADMIN = 9;
    public static int TYPE_NOTIFICATION_SWZL_ADMIN = 10;
    public static int TYPE_NOTIFICATION_SUPER_ADMIN = 11;

    // 通知的标题
    public static String GOODS = "跳蚤市场";
    public static String FIND_GOODS = "寻物启示";
    public static String FIND_PEOPLE = "失物招领";
    public static String SUPER_ADMIN = "超级管理员";

    // 通知的状态
    public static int STATE_RECEIVE = 0; // 收到
    public static int STATE_READ = 1; // 已读

}
