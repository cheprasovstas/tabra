package com.showcase.tabra.data;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.showcase.tabra.R;
import com.showcase.tabra.data.model.LoginRequest;
import com.showcase.tabra.data.model.LoginResponse;
import com.showcase.tabra.data.remote.RestClient;
import com.showcase.tabra.data.remote.RestService;

import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.utils.ExUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private RestService apiService;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private MutableLiveData<Result<LoginResponse>> loginResult = new MutableLiveData<Result<LoginResponse>>();

    // private constructor : singleton access
    private LoginRepository(RestService apiService) {
        this.apiService = apiService;
    }

    public static LoginRepository getInstance(RestClient restClient, Context context) {
        if (instance == null) {
            instance = new LoginRepository(RestClient.getClient(context).create(RestService.class));
        }
        return instance;
    }

//    public boolean isLoggedIn() {
//        return loginResponse != null;
//    }

//    public void logout() {
//        user = null;
//        apiService.logout();
//    }

//    private void setLoggedInUser(LoginResponse user) {
//        this.loginResponse = user;
//        // If user credentials will be cached in local storage, it is recommended it be encrypted
//        // @see https://developer.android.com/training/articles/keystore
//    }

    public void login(String username, String password) {
       apiService.login(new LoginRequest(username, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            loginResult.setValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                        } else {
                            loginResult.setValue(new Result.Success(response.body()));
                        }
                        Log.d("TAG","Response = OK");
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        MyException e = new MyException();
                        e.setMessage(R.string.login_failed);
                        loginResult.setValue(new Result.Error(e));
                        Log.d("TAG","Response = "+t.toString());
                    }
                });
    }



    public MutableLiveData<Result<LoginResponse>> getLoginResult() {
        return loginResult;
    }


//        if (result instanceof Result.Success) {
//            LoginResponse data = ((Result.Success<LoginResponse>) result).getData();
//            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
//        } else {
//
//        }

}