package com.cose.easywu.db;

import org.litepal.crud.LitePalSupport;

public class FindType extends LitePalSupport {

    private String ft_id;
    private String ft_name;
    private String ft_pic;

    public FindType() {
    }

    public FindType(String ft_id, String ft_name, String ft_pic) {
        this.ft_id = ft_id;
        this.ft_name = ft_name;
        this.ft_pic = ft_pic;
    }

    public String getFt_id() {
        return ft_id;
    }

    public void setFt_id(String ft_id) {
        this.ft_id = ft_id;
    }

    public String getFt_name() {
        return ft_name;
    }

    public void setFt_name(String ft_name) {
        this.ft_name = ft_name;
    }

    public String getFt_pic() {
        return ft_pic;
    }

    public void setFt_pic(String ft_pic) {
        this.ft_pic = ft_pic;
    }

    @Override
    public String toString() {
        return "FindType{" +
                "ft_id='" + ft_id + '\'' +
                ", ft_name='" + ft_name + '\'' +
                ", ft_pic='" + ft_pic + '\'' +
                '}';
    }
}
