package com.cose.easywu.home.bean;

import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.ReleaseGoods;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class HomeDataBean {

    private List<BannerInfoBean> banner_info;
    private List<TypeInfoBean> type_info;
    private List<NewestInfoBean> newest_info;
    private List<LikeGoods> goodsLikeList;
    private List<ReleaseGoods> releaseGoodsList;

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

    public List<LikeGoods> getGoodsLikeList() {
        return goodsLikeList;
    }

    public void setGoodsLikeList(List<LikeGoods> goodsLikeList) {
        this.goodsLikeList = goodsLikeList;
    }

    public List<ReleaseGoods> getReleaseGoodsList() {
        return releaseGoodsList;
    }

    public void setReleaseGoodsList(List<ReleaseGoods> releaseGoodsList) {
        this.releaseGoodsList = releaseGoodsList;
    }

    public static class BannerInfoBean {
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

    public static class NewestInfoBean implements Serializable {
        private String g_id;
        private String g_name;
        private String g_desc;
        private double g_price;
        private double g_originalPrice;
        private String g_pic1;
        private String g_pic2;
        private String g_pic3;
        private int g_state;
        private int g_like;
        private Date g_updateTime;
        private String g_u_id;
        private String g_u_nick;
        private String g_u_photo;
        private int g_u_sex;

        public NewestInfoBean() {
        }

        public NewestInfoBean(String g_id, String g_name, String g_desc, double g_price, double g_originalPrice, String g_pic1, String g_pic2, String g_pic3, int g_state, int g_like, Date g_updateTime, String g_u_id, String g_u_nick, String g_u_photo, int g_u_sex) {
            this.g_id = g_id;
            this.g_name = g_name;
            this.g_desc = g_desc;
            this.g_price = g_price;
            this.g_originalPrice = g_originalPrice;
            this.g_pic1 = g_pic1;
            this.g_pic2 = g_pic2;
            this.g_pic3 = g_pic3;
            this.g_state = g_state;
            this.g_like = g_like;
            this.g_updateTime = g_updateTime;
            this.g_u_id = g_u_id;
            this.g_u_nick = g_u_nick;
            this.g_u_photo = g_u_photo;
            this.g_u_sex = g_u_sex;
        }

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

        public int getG_state() {
            return g_state;
        }

        public void setG_state(int g_state) {
            this.g_state = g_state;
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

        public Date getG_updateTime() {
            return g_updateTime;
        }

        public void setG_updateTime(Date g_updateTime) {
            this.g_updateTime = g_updateTime;
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
                    ", g_state=" + g_state +
                    ", g_like=" + g_like +
                    ", g_updateTime=" + g_updateTime +
                    ", g_u_id='" + g_u_id + '\'' +
                    ", g_u_nick='" + g_u_nick + '\'' +
                    ", g_u_photo='" + g_u_photo + '\'' +
                    ", g_u_sex=" + g_u_sex +
                    '}';
        }
    }
}
