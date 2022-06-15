package com.showcase.tabra.data;

import com.showcase.tabra.R;

public class MyException extends Throwable {

    private int error;


    public int getError() {
        return error;
    }

    public void setMessage(int error) {
        this.error = error;
    }

    public static class LoginFailed401ReasonException extends MyException {


        public LoginFailed401ReasonException() {
            setMessage(R.string.login_failed);
        }
    }
}
