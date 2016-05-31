package com.android.biubiu.bean;

import java.io.Serializable;

/**
 * Created by meetu on 2016/5/31.
 */
public class ImageBean implements Serializable {
    String imgName;
    int imgWidth;
    int imgHeight;

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }
}
