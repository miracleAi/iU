package com.android.biubiu.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by meetu on 2016/5/17.
 */
public class BiuBean {
    @SerializedName("icon_thumbnailUrl")
    private String iconUrl;
    @SerializedName("user_code")
    private int userCode;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("sex")
    private String sex;
    @SerializedName("age")
    private int age;
    @SerializedName("starsign")
    private String starsign;
    @SerializedName("school")
    private String school;
    @SerializedName("matching_score")
    private int matchScore;
    @SerializedName("distance")
    private int distance;
    @SerializedName("time")
    private long time;

    //biubiu是否已显示过
    private String isRead;
    //bean在圆圈上的位置index
    int index;
    //bean的x坐标
    int x;
    //bean的y坐标
    int y;

    private int status;//0 未被接受,1已被接受

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getStarsign() {
        return starsign;
    }

    public void setStarsign(String starsign) {
        this.starsign = starsign;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
