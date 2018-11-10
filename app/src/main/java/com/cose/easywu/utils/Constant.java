package com.cose.easywu.utils;

public class Constant {

//    public static String BASE_URL = "http://10.0.2.2:8080/easywu";
    public static String BASE_URL = "http://172.20.10.8:8080/easywu";
    public static String BASE_PHOTO_URL = BASE_URL + "/user_photo/";

    public static String REGIST_URL = BASE_URL + "/user/regist"; // 注册
    public static String LOGIN_URL = BASE_URL + "/user/login"; // 登录
    public static String CHECKVERIFYCODE_URL = BASE_URL + "/user/checkVerifyCode"; // 验证邮箱验证码
    public static String FINDPWD_URL = BASE_URL + "/user/findPwd"; // 找回密码
    public static String RESETPWD_URL = BASE_URL + "/user/resetPwd"; // 重置密码
    public static String PERSONAL_CENTER_URL = BASE_URL + "/user/personalCenter"; // 个人中心
    public static String EDITPWD_URL = BASE_URL + "/user/editPwd"; // 修改密码
    public static String EDITSEX_URL = BASE_URL + "/user/editSex"; // 修改性别
    public static String EDITNICK_URL = BASE_URL + "/user/editNick"; // 修改昵称
    public static String EDITPHOTO_URL = BASE_URL + "/user/editPhoto"; // 修改头像

    public static String HOME_URL = BASE_URL + "/home/home"; // 主页
    public static String RELEASE_GOODS_URL = BASE_URL + "/home/release_goods"; // 发布闲置

}
