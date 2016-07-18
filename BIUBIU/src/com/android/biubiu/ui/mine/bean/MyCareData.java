package com.android.biubiu.ui.mine.bean;

import com.android.biubiu.ui.half.bean.HalfUserBean;

import java.util.ArrayList;

/**
 * Created by meetu on 2016/7/18.
 */
public class MyCareData {
    String token;
    ArrayList<HalfUserBean> users;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<HalfUserBean> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<HalfUserBean> users) {
        this.users = users;
    }
}
