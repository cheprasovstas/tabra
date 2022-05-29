package com.showcase.tabra.data.model;

import com.google.gson.annotations.Expose;

public class LoginRequest {
    @Expose
    public String login;
    @Expose
    public String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}
