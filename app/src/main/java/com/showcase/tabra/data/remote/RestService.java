package com.showcase.tabra.data.remote;


import com.showcase.tabra.data.model.Category;
import com.showcase.tabra.data.model.LoginRequest;
import com.showcase.tabra.data.model.LoginResponse;
import com.showcase.tabra.data.model.Showcase;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;
import com.showcase.tabra.data.model.Product;

import java.util.List;

public interface RestService {

    @GET("products/")
    Call<List<Product>> getProducts();

    @GET("products/")
    Call<List<Product>> getProducts(@Query("search") String search);

    @Multipart
    @POST("products/")
    Call<Product> addProduct(@Part("name") RequestBody name,
                             @Part("category") RequestBody category,
                             @Part("new_category_name") RequestBody category_name,
                             @Part("price") Number price,
                             @Part("unit_price") RequestBody unitPrice,
                             @Part("description") RequestBody description,
                             @Part("active") boolean activePart,
                             @Part MultipartBody.Part image);
//    Call<Product> addProduct(@Part("name") RequestBody name, @Part MultipartBody.Part image);
    //Call<Product> addProduct(@Body Product product);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") String id);

    @Multipart
    @PUT("products/{id}/")
    Call<Product> putProduct(@Path("id") String id,
                             @Part("name") RequestBody name,
                             @Part("category") RequestBody category,
                             @Part("new_category_name") RequestBody category_name,
                             @Part("price") Number price,
                             @Part("unit_price") RequestBody unitPrice,
                             @Part("description") RequestBody description,
                             @Part("active") boolean activePart,
                             @Part MultipartBody.Part image);
//    Call<Product> putProduct(@Path("id") String id, @Body Product product, @Part MultipartBody.Part image);

    @DELETE("products/{id}/")
    Call<Product> deleteProduct(@Path("id") String id);

    @GET("showcase/")
    Call<List<Showcase>> getShowcase();

    @POST("accounts/login/")
    Call<LoginResponse> login(@Body LoginRequest credentials);

    @POST("accounts/logout/")
    Call<LoginResponse> login();

    @GET("categories/")
    Call<List<Category>> getCategories();
}



