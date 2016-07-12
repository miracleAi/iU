package com.android.biubiu.transport.http.response.conversation;

/**
 * Created by yanghj on 16/7/11.
 */
public class CPApply {
    private String userName;
    private int userCode;
    //    private int age;
    private String userSchool;
    private String userHead;
    private int isAccept;
    private String sex;
    private String starsign;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public int getIsAccept() {
        return isAccept;
    }

    public void setIsAccept(int isAccept) {
        this.isAccept = isAccept;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStarsign() {
        return starsign;
    }

    public void setStarsign(String starsign) {
        this.starsign = starsign;
    }
}
