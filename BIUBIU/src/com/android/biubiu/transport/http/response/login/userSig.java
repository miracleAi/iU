package com.android.biubiu.transport.http.response.login;

/**
 * Created by yanghj on 16/7/1.
 */
class UserSig {
    private String sig;
    private long expire;
    private long init;

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getInit() {
        return init;
    }

    public void setInit(long init) {
        this.init = init;
    }
}
