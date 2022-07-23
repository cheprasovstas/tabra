package com.showcase.tabra.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

/**
 * A placeholder item representing a piece of content.
 */
public class Order {

    @SerializedName("id")
    @Expose
    private UUID id;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("order_url")
    @Expose
    private String order_url;

    @SerializedName("sum_price")
    @Expose
    private String sum_price;

    @SerializedName("sum_price_currency")
    @Expose
    private String sum_price_currency;

    @SerializedName("step")
    @Expose
    private Step step;

    @SerializedName("items")
    @Expose
    private List<OrderItem> items;

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOrder_url() {
        return order_url;
    }

    public void setOrder_url(String order_url) {
        this.order_url = order_url;
    }

    public String getSum_price() {
        return sum_price;
    }

    public void setSum_price(String sum_price) {
        this.sum_price = sum_price;
    }

    public String getSum_price_currency() {
        return sum_price_currency;
    }

    public void setSum_price_currency(String sum_price_currency) {
        this.sum_price_currency = sum_price_currency;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
