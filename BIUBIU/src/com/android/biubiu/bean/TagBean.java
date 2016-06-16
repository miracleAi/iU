package com.android.biubiu.bean;

import java.io.Serializable;

/**
 * Created by meetu on 2016/5/31.
 */
public class TagBean implements Serializable{
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String content;

}
