package com.android.biubiu.bean.community;

import java.util.ArrayList;

/**
 * Created by yanghj on 16/6/2.
 */
public class PostsListTagData {
    private ArrayList<Posts> postList;
    private int hasNext;
    private long time;
    private String token;

    public ArrayList<Posts> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<Posts> postList) {
        this.postList = postList;
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
