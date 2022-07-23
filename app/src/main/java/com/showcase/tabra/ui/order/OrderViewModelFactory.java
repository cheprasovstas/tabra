package com.showcase.tabra.ui.order;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.showcase.tabra.data.OrderRepository;
import com.showcase.tabra.data.remote.RestClient;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class OrderViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application application;
    /**
     * Creates a {@code AndroidViewModelFactory}
     */
    public OrderViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;

    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            return (T) new OrderViewModel(application, OrderRepository.getInstance(new RestClient(), application.getApplicationContext()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}