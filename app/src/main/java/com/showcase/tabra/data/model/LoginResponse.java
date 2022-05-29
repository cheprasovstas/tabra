package com.showcase.tabra.data.model;

import com.google.gson.annotations.Expose;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoginResponse {
    @Expose
    public String detail;
    @Expose
    public String token;

    public LoginResponse(String detail, String token) {
        this.detail = detail;
        this.token = token;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}