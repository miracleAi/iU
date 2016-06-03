package com.android.biubiu.bean.community;

import java.util.ArrayList;

/**
 * Created by yanghj on 16/6/3.
 */
public class CommNotifyData {
    private int hasNext;
    private ArrayList<CommNotify> notifies;
    private long time;
    private String token;

    public int getHasNext() {
        return hasNext;
    }

    public void setHasNext(int hasNext) {
        this.hasNext = hasNext;
    }

    public ArrayList<CommNotify> getNotifies() {
        return notifies;
    }

    public void setNotifies(ArrayList<CommNotify> notifies) {
        this.notifies = notifies;
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
