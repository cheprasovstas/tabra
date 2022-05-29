package com.showcase.tabra.ui.product;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.showcase.tabra.data.ProductRepository;
import com.showcase.tabra.data.remote.RestClient;
import org.jetbrains.annotations.NotNull;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ProductViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application application;
    /**
     * Creates a {@code AndroidViewModelFactory}
     */
    public ProductViewModelFactory(@NonNull @NotNull Application application) {
        super(application);
        this.application = application;

    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(application, ProductRepository.getInstance(new RestClient(), application.getApplicationContext()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}