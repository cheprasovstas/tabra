package com.showcase.tabra.ui.product;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.showcase.tabra.R;
import com.showcase.tabra.data.ProductRepository;
import com.showcase.tabra.data.model.Category;
import com.showcase.tabra.data.model.Product;
import com.showcase.tabra.data.remote.Result;

import java.util.List;


public class ProductViewModel extends AndroidViewModel {

    private ProductRepository productRepository;

    private MutableLiveData<ProductFormState> productFormState = new MutableLiveData<>();

    private MutableLiveData<Product> productLiveData = new MutableLiveData<Product>();

    ProductViewModel(Application application, @NonNull ProductRepository productRepository) {
        super(application);
        this.productRepository = productRepository;
    }

    public LiveData<ProductFormState> getProductFormState() {
        return productFormState;
    }

    public LiveData<Result<List<Product>>> getProductListLiveData() {
        return productRepository.getProductListLiveData();
    }

    public void deleteProduct(Product product) {
        productRepository.deleteProduct(product);
    }

    public LiveData<Product> getEditProduct() {
        return this.productLiveData;
    }

    public void setEditProduct(Product product) {
        this.productLiveData.postValue(product);
    }

    public void addProduct(Product product) {
        productRepository.addProduct(product);
    }

    public void editProduct(Product product) {
        productRepository.updateProduct(product);
    }

    public LiveData<Result<Product>> getNewProductLiveData() {
        return productRepository.getNewProductLiveData();
    }

    public LiveData<Result<Product>> getEditedProductLiveData() {
        return productRepository.getEditedProductLiveData();
    }


    public void productDataChanged(String name) {
        if (!isProductNameValid(name)) {
            productFormState.setValue(new ProductFormState(R.string.invalid_username, null));
        } else {
            productFormState.setValue(new ProductFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isProductNameValid(String username) {
        if (username == null) {
            return false;
        }
        return !username.trim().isEmpty();
    }

    public void searchProducts(String s) {
        productRepository.searchProducts(s);
    }

    public LiveData<Result<List<Category>>> getCategoryListLiveData() {
        return productRepository.getCategoryListLiveData();
    }



}