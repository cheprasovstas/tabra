package com.showcase.tabra.ui.showcase;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.showcase.tabra.data.ShowcaseRepository;
import com.showcase.tabra.data.remote.RestClient;
import com.showcase.tabra.ui.product.ProductViewModel;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ShowcaseViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application application;
    /**
     * Creates a {@code AndroidViewModelFactory}
     */
    public ShowcaseViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;

    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShowcaseViewModel.class)) {
            return (T) new ShowcaseViewModel(application, ShowcaseRepository.getInstance(new RestClient(), application.getApplicationContext()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}