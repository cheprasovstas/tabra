package com.showcase.tabra.ui.showcase;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.showcase.tabra.data.ShowcaseRepository;
import com.showcase.tabra.data.model.Showcase;
import com.showcase.tabra.data.remote.Result;

public class ShowcaseViewModel extends AndroidViewModel {
    private final ShowcaseRepository showcaseRepository;

    public ShowcaseViewModel(Application application, ShowcaseRepository showcaseRepository) {
        super(application);
        this.showcaseRepository = showcaseRepository;
    }

    public LiveData<Result<Showcase>> getShowcaseLiveData() {
        return showcaseRepository.getShowcaseLiveData();
    }
    // TODO: Implement the ViewModel
}