package com.android.biubiu.bean.community;

import java.io.Serializable;

/**
 * Created by yanghj on 16/5/31.
 */
public class Tag implements Serializable{
    private String content;
    private int id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
