package com.android.biubiu.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zlp on 2016/5/12.
 */
public class BiuDefaultBean implements Serializable{
    @SerializedName("title")
    String title;
    @SerializedName("cover")
    String imgUrl;
    @SerializedName("url")
    String url;

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
