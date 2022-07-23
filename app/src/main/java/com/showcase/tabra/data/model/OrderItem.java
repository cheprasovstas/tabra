package com.showcase.tabra.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.UUID;

public class OrderItem implements Serializable {

    @SerializedName("id")
    @Expose
    private BigInteger id;

    @SerializedName("product_name")
    @Expose
    private String product_name;

    @SerializedName("quantity")
    @Expose
    private Integer quantity;

    @SerializedName("price")
    @Expose
    private Number price;

    @SerializedName("order")
    @Expose
    private UUID order_id;


    public UUID getOrder_id() {
        return order_id;
    }

    public void setOrder_id(UUID order_id) {
        this.order_id = order_id;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public String getSum_price() {
        return sum_price;
    }

    public void setSum_price(String sum_price) {
        this.sum_price = sum_price;
    }

    @SerializedName("sum_price")
    @Expose
    private String sum_price;


}
