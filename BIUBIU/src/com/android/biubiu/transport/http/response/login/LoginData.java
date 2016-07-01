package com.android.biubiu.transport.http.response.login;

/**
 * Created by yanghj on 16/7/1.
 */
public class LoginData {
    private String password;
    private String nickname;
    private String code;
    private String username;
    private String icon_url;
    private String token;
    private String sex;
    private String virtual_currency;
    private UserSig userSig;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getVirtual_currency() {
        return virtual_currency;
    }

    public void setVirtual_currency(String virtual_currency) {
        this.virtual_currency = virtual_currency;
    }

    public UserSig getUserSig() {
        return userSig;
    }

    public void setUserSig(UserSig userSig) {
        this.userSig = userSig;
    }
}
