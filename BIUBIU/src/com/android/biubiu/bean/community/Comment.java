package com.android.biubiu.bean.community;

/**
 * Created by yanghj on 16/6/1.
 */
public class Comment {
    private int parentId;
    private int userToCode;
    private String userFromSchool;
    private int commentId;
    private String userFromSex;
    private long createAt;
    private int userFromCode;
    private String userFromHead;
    private String userToName;
    private String userFromName;
    private String userToSex;
    private String content;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getUserToCode() {
        return userToCode;
    }

    public void setUserToCode(int userToCode) {
        this.userToCode = userToCode;
    }

    public String getUserFromSchool() {
        return userFromSchool;
    }

    public void setUserFromSchool(String userFromSchool) {
        this.userFromSchool = userFromSchool;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getUserFromSex() {
        return userFromSex;
    }

    public void setUserFromSex(String userFromSex) {
        this.userFromSex = userFromSex;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public int getUserFromCode() {
        return userFromCode;
    }

    public void setUserFromCode(int userFromCode) {
        this.userFromCode = userFromCode;
    }

    public String getUserFromHead() {
        return userFromHead;
    }

    public void setUserFromHead(String userFromHead) {
        this.userFromHead = userFromHead;
    }

    public String getUserToName() {
        return userToName;
    }

    public void setUserToName(String userToName) {
        this.userToName = userToName;
    }

    public String getUserFromName() {
        return userFromName;
    }

    public void setUserFromName(String userFromName) {
        this.userFromName = userFromName;
    }

    public String getUserToSex() {
        return userToSex;
    }

    public void setUserToSex(String userToSex) {
        this.userToSex = userToSex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
