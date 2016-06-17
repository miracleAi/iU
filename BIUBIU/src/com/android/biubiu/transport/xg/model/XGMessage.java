package com.android.biubiu.transport.xg.model;

import com.android.biubiu.bean.BiuBean;

/**
 * Created by yanghj on 16/6/16.
 */
public class XGMessage {
    private String messageType;//101 发biu 102 抢biu 201 头像审核状态
    private BiuBean sendBiu;
    private BiuBean grabBiu;
    private HeadVerify iconState;

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
