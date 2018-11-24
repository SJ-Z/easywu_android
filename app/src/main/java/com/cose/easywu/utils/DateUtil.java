package com.cose.easywu.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    // 计算时间差
    public static String getDatePoor(Date startDate, Date endDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        if (day > 31) { // 直接显示日期yyyy-MM-dd HH:mm:ss
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(startDate);
        } else if (day > 0) {
//            return day + "天" + hour + "小时" + min + "分钟";
            return day + "天前";
        } else if (hour > 0) {
//            return hour + "小时" + min + "分钟";
            return hour + "小时前";
        } else if (min > 0) {
            return min + "分钟前";
        } else {
            return diff / 1000 + "秒前";
        }
    }
}
