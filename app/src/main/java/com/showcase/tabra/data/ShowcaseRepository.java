package com.showcase.tabra.data;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Showcase;
import com.showcase.tabra.data.remote.RestClient;
import com.showcase.tabra.data.remote.RestService;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.utils.ExUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

import static okhttp3.internal.Internal.instance;

public class ShowcaseRepository {
    //Product repository
    private static volatile ShowcaseRepository instance;
    private RestService apiService;
    private MutableLiveData<Result<Showcase>> showcaseLiveData = new MutableLiveData<Result<Showcase>>();


    public ShowcaseRepository(RestService apiService) {
        this.apiService = apiService;
    }

    public static ShowcaseRepository getInstance(RestClient restClient, Context context) {
        if (instance == null) {
            instance = new ShowcaseRepository(RestClient.getClient(context).create(RestService.class));
        }
        return instance;
    }

    public LiveData<Result<Showcase>> getShowcaseLiveData() {
        getShowcase();
        return showcaseLiveData;
    }

    private void getShowcase() {
        apiService.getShowcase()
                .enqueue(new Callback<List<Showcase>>() {
            @Override
            public void onResponse(Call<List<Showcase>> call, Response<List<Showcase>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showcaseLiveData.postValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
                    if (response.body().size() >=1) {
                        showcaseLiveData.postValue(new Result.Success(response.body().get(0)));
                    }
                }
                Log.d("TAG","Response = OK");
            }

            @Override
            public void onFailure(Call<List<Showcase>> call, Throwable t) {
                MyException e = new MyException.ConnectionFailedReasonException(t);
                showcaseLiveData.postValue(new Result.Error(e));
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }
}
