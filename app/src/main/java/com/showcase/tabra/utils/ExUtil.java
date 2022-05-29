package com.showcase.tabra.utils;

import com.showcase.tabra.data.MyException;
import com.showcase.tabra.data.remote.Result;
import retrofit2.Response;

public class ExUtil {
    public static <T> MyException convertUnsuccessfulResponseToException(Response<T> response) {
        if (response.code() == 401) {
            return new MyException.LoginFailed401ReasonException();
        }
        return new MyException();
    }
}
