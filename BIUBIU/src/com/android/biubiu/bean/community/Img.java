package com.android.biubiu.bean.community;

import java.io.Serializable;

/**
 * Created by yanghj on 16/5/31.
 */
public class Img implements Serializable{
    private int w;
    private int h;
    private String url;

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
