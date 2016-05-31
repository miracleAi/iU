package com.android.biubiu.bean.community;

import com.android.biubiu.bean.SimpleData;

/**
 * Created by yanghj on 16/5/31.
 */
public class DiscoveryResponse extends SimpleData {
    private DiscoveryData data;

    public DiscoveryData getData() {
        return data;
    }

    public void setData(DiscoveryData data) {
        this.data = data;
    }
}
