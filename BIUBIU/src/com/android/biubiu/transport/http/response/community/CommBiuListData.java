package com.android.biubiu.transport.http.response.community;

import java.util.ArrayList;

/**
 * Created by yanghj on 16/6/3.
 */
public class CommBiuListData {
    private ArrayList<CommBiuBean> biuList;
    private int hasNext;
    private long time;
    private String token;

    public ArrayList<CommBiuBean> getBiuList() {
        return biuList;
    }

    public void setBiuList(ArrayList<CommBiuBean> biuList) {
        this.biuList = biuList;
    }

    public int getHasNext() {
        return hasNext;
    }

    public void setHasNext(int hasNext) {
        this.hasNext = hasNext;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
