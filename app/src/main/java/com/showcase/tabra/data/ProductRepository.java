package com.showcase.tabra.data;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Category;
import com.showcase.tabra.data.model.Product;
import com.showcase.tabra.data.remote.RestClient;
import com.showcase.tabra.data.remote.RestService;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.utils.ExUtil;

import org.jetbrains.annotations.Nullable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.util.List;

public class ProductRepository {
    //Product repository
    private static volatile ProductRepository instance;
    private RestService apiService;
    private MutableLiveData<Result<List<Product>>> productListLiveData = new MutableLiveData<Result<List<Product>>>();
    private MutableLiveData<Result<List<Category>>> categoryListLiveData = new MutableLiveData<Result<List<Category>>>();
    private MutableLiveData<Result<Product>> newProductLiveData = new MutableLiveData<Result<Product>>();
    private MutableLiveData<Result<Product>> editedProductLiveData = new MutableLiveData<Result<Product>>();

    public ProductRepository(RestService apiService) {
        this.apiService = apiService;
    }

    public static ProductRepository getInstance(RestClient restClient, Context context) {
        if (instance == null) {
            instance = new ProductRepository(RestClient.getClient(context).create(RestService.class));
        }
        return instance;
    }


    public void  searchProducts(@Nullable String searchText,
                                @Nullable String category,
                                @Nullable Boolean inStore,
                                @Nullable Boolean archive
    ) {
        Call<List<Product>> call = apiService.getProducts(searchText, category, inStore, archive);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    productListLiveData.setValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
                    productListLiveData.setValue(new Result.Success(response.body()));
                }
                Log.d("TAG","Response = OK");
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                MyException e = new MyException.ConnectionFailedReasonException(t);
                productListLiveData.setValue(new Result.Error(e));
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }

    public LiveData<Result<List<Product>>> getProductListLiveData() {
//        searchProducts(null, null);
        return productListLiveData;
    }


//    public LiveData<Product> getProductById(String id) {
//        Call<Product> call = apiService.getProductById(id);
//        call.enqueue(new Callback<Product>() {
//            @Override
//            public void onResponse(Call<Product> call, Response<Product> response) {
//                if (response.body() != null) {
//                    selectedProduct.postValue(response.body());
//                }
//            }
//            @Override
//            public void onFailure(Call<Product> call, Throwable t) {
//                Log.d("TAG","Response = "+t.toString());
//            }
//        });
//        return selectedProduct;
//    }


    public void addProduct(Product product) {
        RequestBody namePart = RequestBody.create(MediaType.parse("application/json"), product.getName());
        RequestBody catIdPart = RequestBody.create(MediaType.parse("application/json"), (product.getCategory_id() != null) ? product.getCategory_id().toString(): "");
        RequestBody catNamePart = (product.getCategoryName() != null) ? RequestBody.create(MediaType.parse("application/json"), product.getCategoryName()): null;
        RequestBody unitPricePart = RequestBody.create(MediaType.parse("application/json"), product.getUnitPrice());
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("application/json"), product.getDescription());
        MultipartBody.Part image = null;
        if (product.getF()!=null) {
            File f = product.getF();
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpg"), f);
            image = MultipartBody.Part.createFormData("image", f.getName(), reqFile);
        }
        Call<Product> call = apiService.addProduct(
                namePart,
                catIdPart,
                catNamePart,
                product.getPrice(),
                unitPricePart,
                descriptionPart,
                product.isActive(),
                product.isInStore(),
                image);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    newProductLiveData.postValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
                    newProductLiveData.postValue(new Result.Success(response.body()));
                }
                Log.d("TAG","Response = OK");
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                MyException e = new MyException();
                e.setMessage(R.string.login_failed);
                newProductLiveData.postValue(new Result.Error(e));
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }


    public void updateProduct(Product product) {
        RequestBody namePart = RequestBody.create(MediaType.parse("application/json"), product.getName());
        RequestBody catIdPart = RequestBody.create(MediaType.parse("application/json"), (product.getCategory_id() != null) ? product.getCategory_id().toString(): "");
        RequestBody catNamePart = (product.getCategoryName() != null) ? RequestBody.create(MediaType.parse("application/json"), product.getCategoryName()): null;
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("application/json"), product.getDescription());
        RequestBody unitPricePart = RequestBody.create(MediaType.parse("application/json"), product.getUnitPrice());
        MultipartBody.Part image = null;
        if (product.getF()!=null) {
            File f = product.getF();
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpg"), f);
            image = MultipartBody.Part.createFormData("image", f.getName(), reqFile);
        }

        Call<Product> call = apiService.putProduct(
                product.getId().toString(),
                namePart,
                catIdPart,
                catNamePart,
                product.getPrice(),
                unitPricePart,
                descriptionPart,
                product.isActive(),
                product.isInStore(),
                image);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    editedProductLiveData.postValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
                    editedProductLiveData.postValue(new Result.Success(response.body()));
                }
                Log.d("TAG","Response = OK");
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                MyException e = new MyException();
                e.setMessage(R.string.login_failed);
                editedProductLiveData.postValue(new Result.Error(e));
                Log.d("TAG", "Response = " + t.toString());
            }
        });
    }

    public void deleteProduct(Product product) {
        Call<Product> call = apiService.deleteProduct(product.getId().toString());
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!response.isSuccessful() || response.body() == null) {
//                    productListLiveData.setValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
//                    productListLiveData.setValue(new Result.Success(response.body()));
                }
                Log.d("TAG","Response = OK");
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                MyException e = new MyException();
                e.setMessage(R.string.login_failed);
//                productListLiveData.setValue(new Result.Error(e));
                Log.d("TAG", "Response = " + t.toString());
            }
        });
    }

    public LiveData<Result<Product>> getNewProductLiveData() {
        return newProductLiveData;
    }

    public LiveData<Result<Product>> getEditedProductLiveData() {
        return editedProductLiveData;
    }

    public LiveData<Result<List<Category>>> getCategoryListLiveData() {
        searchCategories();
        return categoryListLiveData;
    }

    private void searchCategories() {
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    categoryListLiveData.setValue(new Result.Error(ExUtil.convertUnsuccessfulResponseToException(response)));
                } else {
                    categoryListLiveData.setValue(new Result.Success(response.body()));
                }
                Log.d("TAG","Response = OK");
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                MyException e = new MyException.ConnectionFailedReasonException(t);
                categoryListLiveData.setValue(new Result.Error(e));
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }
}
