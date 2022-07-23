package com.showcase.tabra.ui.order;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.showcase.tabra.data.OrderRepository;
import com.showcase.tabra.data.model.Order;
import com.showcase.tabra.data.model.OrderItem;
import com.showcase.tabra.data.remote.Result;

import java.util.List;


public class OrderViewModel extends AndroidViewModel {

    private OrderRepository orderRepository;



    OrderViewModel(Application application, @NonNull OrderRepository orderRepository) {
        super(application);
        this.orderRepository = orderRepository;
    }

    public LiveData<Result<List<Order>>> getOrderListLiveData() {
        return orderRepository.getOrderListLiveData();
    }

    public MutableLiveData<Result<Order>> getEditOrder(String id) {
        if (id!=null) {
            orderRepository.searchEditOrder(id);
        }
        return orderRepository.getEditOrder();
    }

    public LiveData<Result<OrderItem>> getEditOrderItem() {
        return orderRepository.getEditOrderItem();
    }

    public void updateOrderItem(OrderItem item) {
        orderRepository.updateOrderItem(item);
    }
}