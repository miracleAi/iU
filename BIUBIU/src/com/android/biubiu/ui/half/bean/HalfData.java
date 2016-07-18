package com.android.biubiu.ui.half.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by meetu on 2016/7/18.
 */
public class HalfData {
    String token;
    int has_next;
    @SerializedName("users")
    ArrayList<HalfUserBean> userList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getHas_next() {
        return has_next;
    }

    public void setHas_next(int has_next) {
        this.has_next = has_next;
    }

    public ArrayList<HalfUserBean> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<HalfUserBean> userList) {
        this.userList = userList;
    }
}
