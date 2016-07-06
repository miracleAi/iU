package com.android.biubiu.transport.http.response.community;

/**
 * Created by yanghj on 16/6/1.
 */
public class PublishCommentData {
    private String token;
    private Comment comment;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

}
