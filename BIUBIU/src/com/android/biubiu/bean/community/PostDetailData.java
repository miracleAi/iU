package com.android.biubiu.bean.community;

import java.util.ArrayList;

/**
 * Created by yanghj on 16/6/1.
 */
public class PostDetailData {
    private Posts post;
    private int hasNext;
    private String token;
    private ArrayList<Comment> commentList;

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public int getHasNext() {
        return hasNext;
    }

    public void setHasNext(int hasNext) {
        this.hasNext = hasNext;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }
}
