package com.android.biubiu.ui.half.bean;

import java.io.Serializable;

/**
 * Created by meetu on 2016/7/18.
 */
public class HalfUserBean implements Serializable{
    String  icon_thumbnailUrl;
    int user_code;
    String nickname;
    String sex;//性别 1 男 2女
    String school;
    int distance;
    long actytime;
    String sign;

    public String getIcon_thumbnailUrl() {
        return icon_thumbnailUrl;
    }

    public void setIcon_thumbnailUrl(String icon_thumbnailUrl) {
        this.icon_thumbnailUrl = icon_thumbnailUrl;
    }

    public int getUser_code() {
        return user_code;
    }

    public void setUser_code(int user_code) {
        this.user_code = user_code;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public long getActytime() {
        return actytime;
    }

    public void setActytime(long actytime) {
        this.actytime = actytime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
