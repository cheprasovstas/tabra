package com.showcase.tabra.data;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Product;
import com.showcase.tabra.data.remote.RestClient;
import com.showcase.tabra.data.remote.RestService;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.utils.ExUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

import java.io.File;
import java.util.List;

public class ProductRepository {
    //Product repository
    private static volatile ProductRepository instance;
    private RestService apiService;
    private MutableLiveData<Result<List<Product>>> productListLiveData = new MutableLiveData<Result<List<Product>>>();
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

    private void searchProducts() {
        apiService.getProducts()
        .enqueue(new Callback<List<Product>>() {
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
                MyException e = new MyException();
                e.setMessage(R.string.login_failed);
                productListLiveData.setValue(new Result.Error(e));
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }

    public LiveData<Result<List<Product>>> getProductListLiveData() {
        searchProducts();
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
                product.getPrice(),
                unitPricePart,
                descriptionPart,
                product.isActive(),
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
                product.getPrice(),
                unitPricePart,
                descriptionPart,
                product.isActive(),
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
}
