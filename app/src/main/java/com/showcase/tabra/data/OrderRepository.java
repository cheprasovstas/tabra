package com.showcase.tabra.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Order;
import com.showcase.tabra.data.model.OrderItem;
import com.showcase.tabra.data.remote.RestClient;
import com.showcase.tabra.data.remote.RestService;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.utils.ExUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class OrderRepository {

    //Product repository
    private static volatile OrderRepository instance;
    private RestService apiService;

    private MutableLiveData<Result<List<Order>>> orderListLiveData = new MutableLiveData<Result<List<Order>>>();
    private MutableLiveData<Result<Order>> editOrderLiveData = new MutableLiveData<Result<Order>>();
    private MutableLiveData<Result<OrderItem>> editOrderItemLiveData = new MutableLiveData<Result<OrderItem>>();
    

    public static OrderRepository getInstance(RestClient restClient, Context context) {
        if (instance == null) {
            instance = new OrderRepository(RestClient.getClient(context).create(RestService.class));
        }
        return instance;
    }

    public OrderRepository(RestService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<Result<List<Order>>> getOrderListLiveData() {
        searchOrders();
        return orderListLiveData;
    }

    public void  searchOrders() {
        Call<List<Order>> call = apiService.getOrders();

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    orderListLiveData.setValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
                    orderListLiveData.setValue(new Result.Success(response.body()));
                }
                Log.d("TAG","Response = OK");
            }
            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                MyException e = new MyException.ConnectionFailedReasonException(t);
                orderListLiveData.setValue(new Result.Error(e));
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }

    public void updateOrderItem(OrderItem item) {

        Call<OrderItem> call = apiService.updateOrderItem(item.getOrder_id().toString(), item.getId(), item);
        call.enqueue(new Callback<OrderItem>() {
            @Override
            public void onResponse(Call<OrderItem> call, Response<OrderItem> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    editOrderItemLiveData.postValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
                    editOrderItemLiveData.postValue(new Result.Success(response.body()));
                }
                Log.d("TAG","Response = OK");
            }
            @Override
            public void onFailure(Call<OrderItem> call, Throwable t) {
                MyException e = new MyException();
                e.setMessage(R.string.login_failed);
                editOrderItemLiveData.postValue(new Result.Error(e));
                Log.d("TAG", "Response = " + t.toString());
            }
        });
    }

    public void searchEditOrder(String id) {
        Call<Order> call = apiService.getOrder(id);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    editOrderLiveData.postValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
                    editOrderLiveData.postValue(new Result.Success(response.body()));
                }
                Log.d("TAG","Response = OK");
            }
            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                MyException e = new MyException();
                e.setMessage(R.string.login_failed);
                editOrderLiveData.postValue(new Result.Error(e));
                Log.d("TAG", "Response = " + t.toString());
            }
        });
    }

    public MutableLiveData<Result<Order>> getEditOrder() {
        return editOrderLiveData;
    }

    public LiveData<Result<OrderItem>> getEditOrderItem() {
        return editOrderItemLiveData;
    }
}