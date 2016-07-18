package com.android.biubiu.ui.half.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by meetu on 2016/7/18.
 */
public class SquareBean implements Serializable{
   String  id;
    @SerializedName("icon_url")
    String IconUrl;
    String content;
    @SerializedName("number_duty")
    int numDuty;
    @SerializedName("number_response")
    int numResponse;
    String color;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public void setIconUrl(String iconUrl) {
        IconUrl = iconUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumDuty() {
        return numDuty;
    }

    public void setNumDuty(int numDuty) {
        this.numDuty = numDuty;
    }

    public int getNumResponse() {
        return numResponse;
    }

    public void setNumResponse(int numResponse) {
        this.numResponse = numResponse;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
