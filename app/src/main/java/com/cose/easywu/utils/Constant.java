package com.cose.easywu.utils;

public class Constant {

    // 广播
    public static String RELEASE_CHOOSE_TYPE = "com.cose.easywu.release.chooseType";
    public static String RELEASE_NEW_RELEASE = "com.cose.easywu.release.newRelease";
    public static String RELEASE_NEW_RELEASE_FIND_PEOPLE = "com.cose.easywu.release.newReleaseFindPeople";
    public static String RELEASE_NEW_RELEASE_FIND_GOODS = "com.cose.easywu.release.newReleaseFindGoods";
//    public static String HX_USER_INFO = "com.cose.easywu.chat_userInfo"; // 在EaseConstant中声明
    public static String RECEIVE_NEW_MESSAGE = "com.cose.easywu.receive_new_message"; // 收到新消息

    // 服务器地址
//    public static String BASE_URL = "http://10.0.2.2:8080/easywu";
    public static String BASE_URL = "http://172.20.10.8:8080/easywu";
    public static String BASE_PHOTO_URL = BASE_URL + "/user_photo/";
    public static String BASE_PIC_URL = BASE_URL + "/goods_pic/";
    public static String BASE_FIND_PIC_URL = BASE_URL + "/find_goods_pic/";

    public static String REGIST_URL = BASE_URL + "/user/regist"; // 注册
    public static String LOGIN_URL = BASE_URL + "/user/login"; // 登录
    public static String CHECKVERIFYCODE_URL = BASE_URL + "/user/checkVerifyCode"; // 验证邮箱验证码
    public static String FINDPWD_URL = BASE_URL + "/user/findPwd"; // 找回密码
    public static String RESETPWD_URL = BASE_URL + "/user/resetPwd"; // 重置密码
    public static String PERSONAL_CENTER_URL = BASE_URL + "/user/personalCenter"; // 个人中心
    public static String EDITPWD_URL = BASE_URL + "/user/editPwd"; // 修改密码
    public static String EDITSEX_URL = BASE_URL + "/user/editSex"; // 修改性别
    public static String EDITPHOTO_URL = BASE_URL + "/user/editPhoto"; // 修改头像
    public static String HXUSER_INFO_URL = BASE_URL + "/user/hxInfo"; // 获取用户的环信信息

    public static String HOME_URL = BASE_URL + "/home/home"; // 主页
    public static String RELEASE_GOODS_URL = BASE_URL + "/goods/release_goods"; // 发布闲置
    public static String NEWEST_GOODS_URL = BASE_URL + "/goods/newestGoods"; // 最新发布的闲置商品
    public static String TYPE_GOODS_URL = BASE_URL + "/goods/typeGoods"; // 分页查询某一分类下的商品
    public static String SET_LIKE_GOODS_URL = BASE_URL + "/goods/setLikeGoods"; // 修改收藏的商品
    public static String POLISH_GOODS_URL = BASE_URL + "/goods/polishGoods"; // 擦亮商品
    public static String DELETE_GOODS_URL = BASE_URL + "/goods/deleteGoods"; // 删除商品（将商品从发布移到下架）
    public static String REMOVE_GOODS_URL = BASE_URL + "/goods/removeGoods"; // 移除商品（用户界面不再显示）
    public static String GET_GOODS_URL = BASE_URL + "/goods/getGoodsInfo"; // 查询商品信息
    public static String GOODS_COMMENT_URL = BASE_URL + "/goods/goodsComment"; // 获取商品评论
    public static String GOODS_ADD_COMMENT_URL = BASE_URL + "/goods/goodsAddComment"; // 添加商品评论
    public static String GOODS_ADD_REPLY_URL = BASE_URL + "/goods/goodsAddReply"; // 添加商品回复
    public static String SEARCH_GOODS_URL = BASE_URL + "/goods/searchGoods"; // 按关键字搜索商品
    public static String NEW_GOODS_ORDER_URL = BASE_URL + "/goods/newGoodsOrder"; // 下单商品
    public static String NEW_GOODS_ORDER_CONFIRM_URL = BASE_URL + "/goods/newGoodsOrderConfirm"; // 确认商品订单
    public static String NEW_GOODS_ORDER_REFUSE_URL = BASE_URL + "/goods/newGoodsOrderRefuse"; // 拒绝商品订单

    public static String HOME_FIND_URL = BASE_URL + "/home/find"; // 失物招领主页
    public static String RELEASE_FIND_URL = BASE_URL + "/goods/release_find"; // 发布失物招领信息
    public static String NEWEST_FIND_GOODS_URL = BASE_URL + "/goods/newestFindGoods"; // 最新发布的寻找失物
    public static String NEWEST_FIND_PEOPLE_URL = BASE_URL + "/goods/newestFindPeople"; // 最新发布的寻找失主
    public static String FIND_GOODS_COMMENT_URL = BASE_URL + "/goods/findGoodsComment"; // 获取失物招领的评论
    public static String SET_LIKE_FIND_GOODS_URL = BASE_URL + "/goods/setLikeFindGoods"; // 修改收藏的失物招领
    public static String FIND_GOODS_ADD_COMMENT_URL = BASE_URL + "/goods/findGoodsAddComment"; // 添加失物招领评论
    public static String FIND_GOODS_ADD_REPLY_URL = BASE_URL + "/goods/findGoodsAddReply"; // 添加失物招领回复
    public static String DELETE_FIND_GOODS_URL = BASE_URL + "/goods/deleteFindGoods"; // 删除失物招领信息（将信息从发布移到下架）
    public static String GET_FIND_GOODS_URL = BASE_URL + "/goods/getFindGoodsInfo"; // 查询失物招领信息
    public static String POLISH_FIND_GOODS_URL = BASE_URL + "/goods/polishFindGoods"; // 擦亮失物招领信息

}
