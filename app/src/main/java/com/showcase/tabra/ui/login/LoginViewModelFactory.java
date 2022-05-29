package com.showcase.tabra.ui.login;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.showcase.tabra.data.LoginRepository;
import com.showcase.tabra.data.remote.RestClient;
import org.jetbrains.annotations.NotNull;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {


    private Application application;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
    */
    public LoginViewModelFactory(@NonNull @NotNull Application application) {
        super(application);
        this.application = application;

    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(application, LoginRepository.getInstance(new RestClient(), application.getApplicationContext()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}