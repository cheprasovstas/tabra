package com.showcase.tabra.utils;

import com.showcase.tabra.data.MyException;

import retrofit2.Response;

public class ExUtil {
    public static <T> MyException convertUnsuccessfulResponseToException(Response<T> response) {

        if (response.code() == 401) {
            return new MyException.LoginFailed401ReasonException();
        }
        if (response.code() == 404) {
            return new MyException.NotFound404ReasonException();
        }

        return new MyException.CommonException();
    }
}
