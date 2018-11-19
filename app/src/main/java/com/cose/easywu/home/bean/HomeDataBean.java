package com.cose.easywu.home.bean;

import com.cose.easywu.gson.User;

import java.util.List;

public class HomeDataBean {

    /**
     * banner_info : [{"image":"/1478770583834.png","option":3,"type":0,"value":{"url":"/act20161111?cyc_app=1"}},{"image":"/1478770583835.png","option":2,"type":0,"value":{"url":"/act20161111?cyc_app=1"}},{"image":"/1478770583836.png","option":1,"type":0,"value":{"url":"/act20161111?cyc_app=1"}}]
     * type_info : [{"channel_name":"服饰","image":"/app/img/menu-cyc.png","option":2,"type":1,"value":{"channel_id":"8"}},{"channel_name":"游戏","image":"/app/img/menu-game.png","option":2,"type":1,"value":{"channel_id":"4"}},{"channel_name":"动漫","image":"/app/img/menu-carttoon.png","option":2,"type":1,"value":{"channel_id":"3"}},{"channel_name":"装扮","image":"/app/img/menu-cosplay.png","option":2,"type":1,"value":{"channel_id":"5"}},{"channel_name":"古风","image":"/app/img/menu-oldage.png","option":2,"type":1,"value":{"channel_id":"6"}},{"channel_name":"漫展票务","image":"/app/img/menu-collect.png","option":2,"type":1,"value":{"channel_id":"9"}},{"channel_name":"文具","image":"/app/img/menu-stationery.png","option":2,"type":1,"value":{"channel_id":"11"}},{"channel_name":"零食","image":"/app/img/menu-snack.png","option":2,"type":1,"value":{"channel_id":"10"}},{"channel_name":"首饰","image":"/app/img/menu-jewelry.png","option":2,"type":1,"value":{"channel_id":"12"}},{"channel_name":"更多","image":"/app/img/menu-more.png","option":6,"type":1,"value":{"channel_id":"13"}}]
     * newest_info : [{"cover_price":"138.00","figure":"/supplier/1478873740576.jpg","name":"【尚硅谷】日常 萌系小天使卫衣--白色款","product_id":"10659"},{"cover_price":"138.00","figure":"/supplier/1478873369497.jpg","name":"【尚硅谷】日常 萌系小恶魔卫衣--黑色款","product_id":"10658"},{"cover_price":"32.00","figure":"/supplier/1478867468462.jpg","name":"预售【漫友文化】全职高手6 天闻角川  流地徽章 全新典藏版 蝴蝶蓝 猫树绘 赠精美大海报+首刷限定赠2017年活页台历","product_id":"10657"},{"cover_price":"18.00","figure":"/1478860081305.jpg","name":"【幸运星】烫金雪纺JSK的配件小物：手 套、项链","product_id":"10656"},{"cover_price":"178.00","figure":"/1478850234799.jpg","name":"【尚硅谷】妖狐图腾 阴阳师同人元素卫衣","product_id":"10655"},{"cover_price":"138.00","figure":"/1478849792177.jpg","name":"【尚硅谷】学院风 日常百搭 宽松长袖衬衫","product_id":"10654"}]
     */

    private List<BannerInfoBean> banner_info;
    private List<TypeInfoBean> type_info;
    private List<NewestInfoBean> newest_info;

    public List<BannerInfoBean> getBanner_info() {
        return banner_info;
    }

    public void setBanner_info(List<BannerInfoBean> banner_info) {
        this.banner_info = banner_info;
    }

    public List<TypeInfoBean> getType_info() {
        return type_info;
    }

    public void setType_info(List<TypeInfoBean> type_info) {
        this.type_info = type_info;
    }

    public List<NewestInfoBean> getNewest_info() {
        return newest_info;
    }

    public void setNewest_info(List<NewestInfoBean> newest_info) {
        this.newest_info = newest_info;
    }

    public static class BannerInfoBean {
        /**
         * image : /1478770583834.png
         * option : 3
         * type : 0
         * value : {"url":"/act20161111?cyc_app=1"}
         */

        private String ban_id;
        private String ban_img;
        private int ban_index;

        public String getBan_id() {
            return ban_id;
        }

        public void setBan_id(String ban_id) {
            this.ban_id = ban_id;
        }

        public String getBan_img() {
            return ban_img;
        }

        public void setBan_img(String ban_img) {
            this.ban_img = ban_img;
        }

        public int getBan_index() {
            return ban_index;
        }

        public void setBan_index(int ban_index) {
            this.ban_index = ban_index;
        }

        @Override
        public String toString() {
            return "BannerInfoBean{" +
                    "ban_id='" + ban_id + '\'' +
                    ", ban_img='" + ban_img + '\'' +
                    ", ban_index=" + ban_index +
                    '}';
        }
    }

    public static class TypeInfoBean {

        private String t_id;
        private String t_name;
        private String t_pic;

        public String getT_id() {
            return t_id;
        }

        public void setT_id(String t_id) {
            this.t_id = t_id;
        }

        public String getT_name() {
            return t_name;
        }

        public void setT_name(String t_name) {
            this.t_name = t_name;
        }

        public String getT_pic() {
            return t_pic;
        }

        public void setT_pic(String t_pic) {
            this.t_pic = t_pic;
        }

        @Override
        public String toString() {
            return "TypeInfoBean{" +
                    "t_id='" + t_id + '\'' +
                    ", t_name='" + t_name + '\'' +
                    ", t_pic='" + t_pic + '\'' +
                    '}';
        }
    }

    public static class NewestInfoBean {
        private String g_id;
        private String g_name;
        private String g_desc;
        private double g_price;
        private double g_originalPrice;
        private String g_pic1;
        private String g_pic2;
        private String g_pic3;
        private int g_like;
        private String g_u_id;
        private String g_u_nick;
        private String g_u_photo;
        private int g_u_sex;

        public String getG_id() {
            return g_id;
        }

        public void setG_id(String g_id) {
            this.g_id = g_id;
        }

        public String getG_name() {
            return g_name;
        }

        public void setG_name(String g_name) {
            this.g_name = g_name;
        }

        public String getG_desc() {
            return g_desc;
        }

        public void setG_desc(String g_desc) {
            this.g_desc = g_desc;
        }

        public double getG_price() {
            return g_price;
        }

        public void setG_price(double g_price) {
            this.g_price = g_price;
        }

        public double getG_originalPrice() {
            return g_originalPrice;
        }

        public void setG_originalPrice(double g_originalPrice) {
            this.g_originalPrice = g_originalPrice;
        }

        public String getG_pic1() {
            return g_pic1;
        }

        public void setG_pic1(String g_pic1) {
            this.g_pic1 = g_pic1;
        }

        public String getG_pic2() {
            return g_pic2;
        }

        public void setG_pic2(String g_pic2) {
            this.g_pic2 = g_pic2;
        }

        public String getG_pic3() {
            return g_pic3;
        }

        public void setG_pic3(String g_pic3) {
            this.g_pic3 = g_pic3;
        }

        public int getG_like() {
            return g_like;
        }

        public void setG_like(int g_like) {
            this.g_like = g_like;
        }

        public String getG_u_id() {
            return g_u_id;
        }

        public void setG_u_id(String g_u_id) {
            this.g_u_id = g_u_id;
        }

        public String getG_u_nick() {
            return g_u_nick;
        }

        public void setG_u_nick(String g_u_nick) {
            this.g_u_nick = g_u_nick;
        }

        public String getG_u_photo() {
            return g_u_photo;
        }

        public void setG_u_photo(String g_u_photo) {
            this.g_u_photo = g_u_photo;
        }

        public int getG_u_sex() {
            return g_u_sex;
        }

        public void setG_u_sex(int g_u_sex) {
            this.g_u_sex = g_u_sex;
        }

        @Override
        public String toString() {
            return "NewestInfoBean{" +
                    "g_id='" + g_id + '\'' +
                    ", g_name='" + g_name + '\'' +
                    ", g_desc='" + g_desc + '\'' +
                    ", g_price=" + g_price +
                    ", g_originalPrice=" + g_originalPrice +
                    ", g_pic1='" + g_pic1 + '\'' +
                    ", g_pic2='" + g_pic2 + '\'' +
                    ", g_pic3='" + g_pic3 + '\'' +
                    ", g_like=" + g_like +
                    ", g_u_id='" + g_u_id + '\'' +
                    ", g_u_nick='" + g_u_nick + '\'' +
                    ", g_u_photo='" + g_u_photo + '\'' +
                    ", g_u_sex=" + g_u_sex +
                    '}';
        }
    }
}
