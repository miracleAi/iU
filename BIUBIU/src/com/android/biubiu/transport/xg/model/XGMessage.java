package com.android.biubiu.transport.xg.model;

import com.android.biubiu.bean.BiuBean;

/**
 * Created by yanghj on 16/6/16.
 */
public class XGMessage {
    private String messageType;//101 发biu 102 抢biu 103 社区biu 201 头像审核状态 301 点赞评论通知
    private BiuBean sendBiu;
    private BiuBean grabBiu;
    private HeadVerify iconState;
    private int grabBiuCount;
    private int sendBiuCount;
    private int noticeCount;
    private int comBiuCount;
    private int badge;

    public int getGrabBiuCount() {
        return grabBiuCount;
    }

    public void setGrabBiuCount(int grabBiuCount) {
        this.grabBiuCount = grabBiuCount;
    }

    public int getSendBiuCount() {
        return sendBiuCount;
    }

    public void setSendBiuCount(int sendBiuCount) {
        this.sendBiuCount = sendBiuCount;
    }

    public int getNoticeCount() {
        return noticeCount;
    }

    public void setNoticeCount(int noticeCount) {
        this.noticeCount = noticeCount;
    }

    public int getComBiuCount() {
        return comBiuCount;
    }

    public void setComBiuCount(int comBiuCount) {
        this.comBiuCount = comBiuCount;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public BiuBean getSendBiu() {
        return sendBiu;
    }

    public void setSendBiu(BiuBean sendBiu) {
        this.sendBiu = sendBiu;
    }

    public BiuBean getGrabBiu() {
        return grabBiu;
    }

    public void setGrabBiu(BiuBean grabBiu) {
        this.grabBiu = grabBiu;
    }

    public HeadVerify getIconState() {
        return iconState;
    }

    public void setIconState(HeadVerify iconState) {
        this.iconState = iconState;
    }
}
