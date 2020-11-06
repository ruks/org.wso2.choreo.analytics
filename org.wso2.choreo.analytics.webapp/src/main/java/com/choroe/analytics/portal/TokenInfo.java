package com.choroe.analytics.portal;

import javax.servlet.http.Cookie;

public class TokenInfo {
    private String user;
    private String tenant;
    private String[] environments;
    private int expireIn;
    private Cookie[] Cookies;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String[] getEnvironments() {
        return environments;
    }

    public void setEnvironments(String[] environments) {
        this.environments = environments;
    }

    public int getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(int expireIn) {
        this.expireIn = expireIn;
    }

    public Cookie[] getCookies() {
        return Cookies;
    }

    public void setCookies(Cookie[] cookies) {
        Cookies = cookies;
    }

}
