package com.cose.easywu.home.bean;

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

    }
}
